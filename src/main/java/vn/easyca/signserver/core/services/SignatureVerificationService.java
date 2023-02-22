package vn.easyca.signserver.core.services;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.poifs.crypt.dsig.SignatureConfig;
import org.apache.poi.poifs.crypt.dsig.SignatureInfo;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import sun.security.provider.certpath.OCSP;
import sun.security.x509.X509CertImpl;
import vn.easyca.signserver.core.domain.CertificateDTO;
import vn.easyca.signserver.core.dto.verification.*;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.core.dto.SignatureVerificationRequest;
import vn.easyca.signserver.core.dto.SignatureVerificationResponse;
import vn.easyca.signserver.core.exception.CertificateNotFoundAppException;
import vn.easyca.signserver.core.exception.VerifiedAppException;
import vn.easyca.signserver.pki.sign.rawsign.SignatureValidator;
import vn.easyca.signserver.webapp.config.Constants;
import vn.easyca.signserver.webapp.service.CertificateService;
import vn.easyca.signserver.webapp.service.FileResourceService;
import vn.easyca.signserver.webapp.utils.DateTimeUtils;

import javax.xml.crypto.*;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class SignatureVerificationService {

    private final CertificateService certificateService;
    private final FileResourceService fileResourceService;
    private static BouncyCastleProvider provider = null;

    public SignatureVerificationService(CertificateService certificateService, FileResourceService fileResourceService) {
        this.certificateService = certificateService;
        this.fileResourceService = fileResourceService;
    }

    public Object verifyHash(SignatureVerificationRequest request) throws ApplicationException {
        SignatureVerificationResponse response = new SignatureVerificationResponse();
        CertificateDTO certificateDTO = certificateService.getBySerial(request.getSerial());
        if (certificateDTO == null)
            throw new CertificateNotFoundAppException();

        X509Certificate x509Certificate = certificateDTO.getX509Certificate();


        SignatureValidator rawValidator = new SignatureValidator();
        for (SignatureVerificationRequest.Element element : request.getElements()) {
            try {
                boolean result = rawValidator.verify(element.getOriginalData(), element.getSignature(), x509Certificate);
                response.add(element.getKey(), result);
            } catch (Exception exception) {
                throw new VerifiedAppException(exception);
            }
        }
        response.setCertificate(certificateDTO.getRawData());
        return response;
    }

    public SignatureVerificationResponse verifyRaw(SignatureVerificationRequest request) throws Exception {
        if (request.getHashAlgorithm() == null)
            request.setHashAlgorithm(Constants.HASH_ALGORITHM.SHA1);
        SignatureVerificationResponse response = new SignatureVerificationResponse();
        CertificateDTO certificateDTO = certificateService.getBySerial(request.getSerial());
        if (certificateDTO == null)
            throw new CertificateNotFoundAppException();
        response.setCertificate(certificateDTO.getRawData());
        X509Certificate x509Certificate = certificateDTO.getX509Certificate();
        SignatureValidator rawValidator = new SignatureValidator();
        for (SignatureVerificationRequest.Element element : request.getElements()) {
            boolean result = rawValidator.verify(element.getOriginalData(), element.getSignature(), x509Certificate, request.getHashAlgorithm());
            response.add(element.getKey(), result);
        }
        return response;
    }

    private RevocationStatus checkRevocation(PdfPKCS7 pkcs7, X509Certificate signCert, X509Certificate issuerCert, Date date) {
        try {
            List<BasicOCSPResp> ocsps = new ArrayList<BasicOCSPResp>();
            if (pkcs7.getOcsp() != null) {
                ocsps.add(pkcs7.getOcsp());
            }

            // Check if the OCSP responses in the list were valid for the certificate on a specific date.
            OCSPVerifier ocspVerifier = new OCSPVerifier(null, ocsps);
            List<VerificationOK> verification = ocspVerifier.verify(signCert, issuerCert, date);

            // If that list is empty, we can't verify using OCSP, and we need to look for CRLs.
            if (verification.size() == 0) {
                List<X509CRL> crls = new ArrayList<X509CRL>();
                if (pkcs7.getCRLs() != null) {
                    for (CRL crl : pkcs7.getCRLs()) {
                        crls.add((X509CRL) crl);
                    }
                }

                // Check if the CRLs in the list were valid on a specific date.
                CRLVerifier crlVerifier = new CRLVerifier(null, crls);
                verification.addAll(crlVerifier.verify(signCert, issuerCert, date));
            }

            if (verification.size() == 0) {
//            OUT_STREAM.println("The signing certificate couldn't be verified");
                return RevocationStatus.CANT_VERIFY;
            } else {
                return RevocationStatus.GOOD;
//            for (VerificationOK v : verification) {
//                OUT_STREAM.println(v);
//            }
            }
        } catch (Exception ex) {
            return RevocationStatus.REVOKED;
        }
    }

    private CertificateVfDTO getCertificateInfo(PdfPKCS7 pkcs7, X509Certificate cert, X509Certificate issuerCert, Date signDate, boolean isSigningCert) {
        CertificateVfDTO certificateVfDTO = new CertificateVfDTO();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Universal"));

        CertStatus signTimeStatus = null;
        CertStatus currentStatus = null;
        try {
            cert.checkValidity(signDate);
            signTimeStatus = CertStatus.VALID;
        } catch (CertificateExpiredException e) {
            signTimeStatus = CertStatus.EXPIRED;
        } catch (CertificateNotYetValidException e) {
            signTimeStatus = CertStatus.INVALID;
        }

        // Check if a certificate is still valid now
        try {
            cert.checkValidity();
            currentStatus = CertStatus.VALID;
        } catch (CertificateExpiredException e) {
            currentStatus = CertStatus.EXPIRED;
        } catch (CertificateNotYetValidException e) {
            currentStatus = CertStatus.INVALID;
        }
        RevocationStatus revocationStatus = RevocationStatus.UNCHECKED;
        if (isSigningCert)
            revocationStatus = checkRevocation(pkcs7, cert, issuerCert, signDate);

        certificateVfDTO.setIssuer(cert.getIssuerDN().toString());
        certificateVfDTO.setSubjectDn(cert.getSubjectDN().toString());
        certificateVfDTO.setValidFrom(simpleDateFormat.format(cert.getNotBefore()));
        certificateVfDTO.setValidTo(simpleDateFormat.format(cert.getNotAfter()));
        certificateVfDTO.setCurrentStatus(currentStatus);
        certificateVfDTO.setSignTimeStatus(signTimeStatus);
        certificateVfDTO.setRevocationStatus(revocationStatus);
        return certificateVfDTO;
    }

    public VerificationResponseDTO verifyPDF(InputStream pdfStream) throws ApplicationException {
        try {
            if (provider == null) {
                provider = new BouncyCastleProvider();
                Security.addProvider(provider);
            }

            VerificationResponseDTO result = new VerificationResponseDTO();
            List<SignatureVfDTO> signatureVfDTOList = new ArrayList<>();

            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(null, null);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            try (InputStream rootCaStream = fileResourceService.getRootCer(FileResourceService.EASY_CA)) {
                ks.setCertificateEntry("root", cf.generateCertificate(rootCaStream));
            }
            PdfDocument pdfDoc = new PdfDocument(new PdfReader(pdfStream));
            SignatureUtil signUtil = new SignatureUtil(pdfDoc);
            List<String> signatureNames = signUtil.getSignatureNames();

            boolean isCoverWholeDocument;
            int documentRevision;
            int totalRevision;
            boolean isIntegrity;
            String signTime;
            SignatureVfDTO signatureVfDTO;
            CertificateVfDTO certificateVfDTO;
            List<CertificateVfDTO> certificateVfDTOList;
            for (String name : signatureNames) {
                certificateVfDTOList = new ArrayList<>();

                PdfPKCS7 pkcs7 = signUtil.readSignatureData(name);
                Certificate[] certs = pkcs7.getSignCertificateChain();

                isIntegrity = pkcs7.verifySignatureIntegrityAndAuthenticity();
                isCoverWholeDocument = signUtil.signatureCoversWholeDocument(name);
                documentRevision = signUtil.getRevision(name);
                totalRevision = signUtil.getTotalRevisions();

                // Timestamp is a secure source of signature creation time,
                // because it's based on Time Stamping Authority service.
                Calendar cal = pkcs7.getSignDate();

                // If there is no timestamp, use the current date
                if (TimestampConstants.UNDEFINED_TIMESTAMP_DATE == cal) {
                    cal = Calendar.getInstance();
                }
                signTime = DateTimeUtils.format(cal.getTime(), DateTimeUtils.HHmmss_ddMMyyyy);

                List<VerificationException> errors = CertificateVerification.verifyCertificates(certs, ks, Calendar.getInstance());
                boolean isEasyCACert = errors.size() == 0;
//                OUT_STREAM.println(errors);

                X509Certificate issuerCert = isEasyCACert ? (X509Certificate) ks.getCertificate("root") : (certs.length > 1 ? (X509Certificate) certs[1] : null);
                int signCertIndex = 0;
                for (int i = 0; i < certs.length; i++) {
                    X509Certificate cert = (X509Certificate) certs[i];
                    certificateVfDTO = getCertificateInfo(pkcs7, cert, issuerCert, cal.getTime(), signCertIndex == i);
                    certificateVfDTO.setEasyCACert(isEasyCACert);
                    certificateVfDTOList.add(certificateVfDTO);
                }

                signatureVfDTO = new SignatureVfDTO(isIntegrity, isCoverWholeDocument, documentRevision, totalRevision, signTime);
                signatureVfDTO.setCertificateVfDTOs(certificateVfDTOList);
                signatureVfDTOList.add(signatureVfDTO);
            }
            result.setSignatureVfDTOs(signatureVfDTOList);
            return result;
        }
        catch (Exception ex) {
            throw new ApplicationException("Has error when verify PDF file", ex);
        }
    }

    private CertificateVfDTO getCertificateInfoDoc(X509Certificate cert, Date signDate) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, ApplicationException, CertPathValidatorException {
        CertificateVfDTO certificateVfDTO = new CertificateVfDTO();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Universal"));

        CertStatus signTimeStatus = null;
        CertStatus currentStatus = null;
        try {
            cert.checkValidity(signDate);
            signTimeStatus = CertStatus.VALID;
        } catch (CertificateExpiredException e) {
            signTimeStatus = CertStatus.EXPIRED;
        } catch (CertificateNotYetValidException e) {
            signTimeStatus = CertStatus.INVALID;
        }

        // Check if a certificate is still valid now
        try {
            cert.checkValidity();
            currentStatus = CertStatus.VALID;
        } catch (CertificateExpiredException e) {
            currentStatus = CertStatus.EXPIRED;
        } catch (CertificateNotYetValidException e) {
            currentStatus = CertStatus.INVALID;
        }
        RevocationStatus revocationStatus = RevocationStatus.UNCHECKED;
//        if (isSigningCert)
//            revocationStatus = checkRevocation(pkcs7, cert, issuerCert, signDate);
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream rootCaStream = fileResourceService.getRootCer(FileResourceService.EASY_CA);
        ks.setCertificateEntry("root", cf.generateCertificate(rootCaStream));
//        OCSP.RevocationStatus revoStatus =  OCSP.check(cert, (X509Certificate) ks.getCertificate("root"));
        OCSP.RevocationStatus revoStatus =  OCSP.check(cert, (X509Certificate) ks.getCertificate("root"), OCSP.getResponderURI(X509CertImpl.toImpl(cert)), null, null);
        if(revoStatus.getCertStatus().toString().trim().equals(RevocationStatus.REVOKED.toString()))
            revocationStatus = RevocationStatus.REVOKED;
        else if(revoStatus.getCertStatus().toString().trim().equals(RevocationStatus.GOOD.toString()))
            revocationStatus = RevocationStatus.GOOD;
        else if(revoStatus.getCertStatus().toString().trim().equals("UNKNOWN"))
            revocationStatus = RevocationStatus.CANT_VERIFY;

        certificateVfDTO.setIssuer(cert.getIssuerDN().toString());
        certificateVfDTO.setSubjectDn(cert.getSubjectDN().toString());
        certificateVfDTO.setValidFrom(simpleDateFormat.format(cert.getNotBefore()));
        certificateVfDTO.setValidTo(simpleDateFormat.format(cert.getNotAfter()));
        certificateVfDTO.setCurrentStatus(currentStatus);
        certificateVfDTO.setSignTimeStatus(signTimeStatus);
        certificateVfDTO.setRevocationStatus(revocationStatus);
        return certificateVfDTO;
    }

    public VerificationResponseDTO verifyDocx(InputStream stream) throws IOException, InvalidFormatException, ApplicationException {
        OPCPackage pkg = OPCPackage.open(stream);
        try{
            if (provider == null) {
                provider = new BouncyCastleProvider();
                Security.addProvider(provider);
            }

            SignatureConfig sic = new SignatureConfig();
            sic.setOpcPackage(pkg);
            SignatureInfo si = new SignatureInfo();
            si.setSignatureConfig(sic);

            VerificationResponseDTO result = new VerificationResponseDTO();

//            VerificationResponseDTO verificationResponseDTO = new VerificationResponseDTO();
            List<SignatureVfDTO> signatureVfDTOList = new ArrayList<>();
            List<CertificateVfDTO> certificateVfDTOList = new ArrayList<>();
//            List<X509Certificate> result = new ArrayList<>();

            for(SignatureInfo.SignaturePart sp : si.getSignatureParts()){
                if(sp.validate()){
                    try (InputStream inputStream = new ByteArrayInputStream(sp.getSignatureDocument().toString().getBytes())){
                        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                        DocumentBuilder db = dbf.newDocumentBuilder();
                        Document doc = db.parse(inputStream);
                        doc.getDocumentElement().normalize();

                        NodeList nodeList = doc.getElementsByTagName("mdssi:SignatureTime");
                        Node node = nodeList.item(0);
                        Element element = (Element) node;
                        String dateStr = element.getElementsByTagName("mdssi:Value").item(0).getTextContent();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Universal"));
                        Date date = simpleDateFormat.parse(dateStr);
                        String signTime = DateTimeUtils.format(date, DateTimeUtils.HHmmss_ddMMyyyy);

                        SignatureVfDTO signatureVfDTO = new SignatureVfDTO();
                        signatureVfDTO.setIntegrity(si.verifySignature());
                        signatureVfDTO.setSignTime(signTime);
                        X509Certificate signer = sp.getSigner();
                        certificateVfDTOList.add(getCertificateInfoDoc(signer, DateTimeUtils.parse(dateStr)));
                        signatureVfDTO.setCertificateVfDTOs(certificateVfDTOList);


//                    result.add(sp.getSigner());
                        signatureVfDTOList.add(signatureVfDTO);
                        result.setSignatureVfDTOs(signatureVfDTOList);
                    }
                }
            }
//            X509Certificate signer = result.get(0);
//            System.out.println( "signer: " + signer.getSubjectX500Principal());
//            boolean b = si.verifySignature();
//            System.out.println("test-file: " + b);

            pkg.revert();
            return result;
        }catch (Exception ex){
            throw new ApplicationException("Has error when verify Docx file", ex);
        }finally {
            pkg.close();
        }
    }

    public VerificationResponseDTO verifyXml(InputStream stream) throws ApplicationException, IOException, InvalidFormatException {
        try{
            if (provider == null) {
                provider = new BouncyCastleProvider();
                Security.addProvider(provider);
            }

            VerificationResponseDTO result = new VerificationResponseDTO();
            List<SignatureVfDTO> signatureVfDTOList = new ArrayList<>();
            List<CertificateVfDTO> certificateVfDTOList = new ArrayList<>();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document doc = dbf.newDocumentBuilder().parse(stream);
            doc.getDocumentElement().normalize();

            Element rootElement = doc.getDocumentElement();



            NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
            if (nl.getLength() == 0) {
                throw new Exception("Cannot find Signature element");
            }

            Node node = nl.item(0);
            while(node.getParentNode() .getParentNode()!= null){
                node = node.getParentNode();
            }
//            NodeList content = node.getChildNodes();
//            int i=0;
//            while (content.item(i).getLocalName()==null) i++;
//
//            Element context = (Element) content.item(i);
//            if (context.getAttribute("Id") != "") {
//                context.setIdAttribute("Id", true);
//            } else if (context.getAttribute("ID") != "") {
//                context.setIdAttribute("ID", true);
//            } else if (context.getAttribute("id") != "") {
//                context.setIdAttribute("id", true);
//            }

            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
            DOMValidateContext valContext = new DOMValidateContext( new X509KeySelector(), nl.item(0));
            setIdAttribute(valContext,node);
            XMLSignature signature = fac.unmarshalXMLSignature(valContext);
            boolean coreValidity = signature.validate(valContext);

            //get signature value
            KeyInfo keyInfo = signature.getKeyInfo();
            Iterator ki = keyInfo.getContent().iterator();
            while(ki.hasNext()){
                XMLStructure info = (XMLStructure) ki.next();
                if(info instanceof X509Data){
                    X509Data x509Data = (X509Data) info;
                    Iterator xi = x509Data.getContent().iterator();
                    SignatureVfDTO signatureVfDTO = new SignatureVfDTO();
                    signatureVfDTO.setIntegrity(coreValidity);

                    while(xi.hasNext()){
                        Object o = xi.next();
                        if (o instanceof X509Certificate) {
                            X509Certificate cert = (X509Certificate) o;
                            certificateVfDTOList.add(getCertificateInfoXml(cert));
                            signatureVfDTO.setCertificateVfDTOs(certificateVfDTOList);
                        }
                    }
                    signatureVfDTOList.add(signatureVfDTO);

                }
            }
            result.setSignatureVfDTOs(signatureVfDTOList);




            return result;
        }catch (Exception ex){
            throw new ApplicationException("Has error when verify Xml file", ex);
        }


    }

    public  void setIdAttribute(DOMValidateContext validateContext,Node node){
        final NamedNodeMap attributes = node.getAttributes();
        if(attributes != null) {
            for (int jj = 0; jj < attributes.getLength(); jj++) {
                final Node item = attributes.item(jj);
                final String localName = item.getNodeName();
                if (localName != null) {
                    final String id = localName.toLowerCase();
                    if ("id".equals(id)) {
                        validateContext.setIdAttributeNS((Element) node, null, localName);
                        break;
                    }
                }
            }
        }
        NodeList nl = node.getChildNodes();
        for(int jj = 0; jj < nl.getLength(); jj++){
            setIdAttribute(validateContext, nl.item(jj));
        }
    }




    private CertificateVfDTO getCertificateInfoXml(X509Certificate cert) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, ApplicationException, CertPathValidatorException {
        CertificateVfDTO certificateVfDTO = new CertificateVfDTO();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Universal"));

        CertStatus signTimeStatus = null;
        CertStatus currentStatus = null;

        signTimeStatus = CertStatus.VALID;


        // Check if a certificate is still valid now
        try {
            cert.checkValidity();
            currentStatus = CertStatus.VALID;
        } catch (CertificateExpiredException e) {
            currentStatus = CertStatus.EXPIRED;
        } catch (CertificateNotYetValidException e) {
            currentStatus = CertStatus.INVALID;
        }
        RevocationStatus revocationStatus = RevocationStatus.UNCHECKED;
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream rootCaStream = fileResourceService.getRootCer(FileResourceService.EASY_CA);
        Certificate issuesCert = cf.generateCertificate(rootCaStream);
        X509Certificate x509IssuesCert = (X509Certificate) issuesCert;

        if(x509IssuesCert.getSubjectDN().equals(cert.getIssuerDN())) {
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(null, null);
            ks.setCertificateEntry("root", issuesCert);
            OCSP.RevocationStatus revoStatus = OCSP.check(cert, (X509Certificate) ks.getCertificate("root"), OCSP.getResponderURI(X509CertImpl.toImpl(cert)), null, null);
            if (revoStatus.getCertStatus().toString().trim().equals(RevocationStatus.REVOKED.toString()))
                revocationStatus = RevocationStatus.REVOKED;
            else if (revoStatus.getCertStatus().toString().trim().equals(RevocationStatus.GOOD.toString()))
                revocationStatus = RevocationStatus.GOOD;
            else if (revoStatus.getCertStatus().toString().trim().equals("UNKNOWN"))
                revocationStatus = RevocationStatus.CANT_VERIFY;
        }
        certificateVfDTO.setIssuer(cert.getIssuerDN().toString());
        certificateVfDTO.setSubjectDn(cert.getSubjectDN().toString());
        certificateVfDTO.setValidFrom(simpleDateFormat.format(cert.getNotBefore()));
        certificateVfDTO.setValidTo(simpleDateFormat.format(cert.getNotAfter()));
        certificateVfDTO.setCurrentStatus(currentStatus);
        certificateVfDTO.setSignTimeStatus(signTimeStatus);
        certificateVfDTO.setRevocationStatus(revocationStatus);
        return certificateVfDTO;
    }

    public static class X509KeySelector extends KeySelector {
        public KeySelectorResult select(KeyInfo keyInfo,
                                        KeySelector.Purpose purpose,
                                        AlgorithmMethod method,
                                        XMLCryptoContext context)
            throws KeySelectorException {
            Iterator ki = keyInfo.getContent().iterator();
            while (ki.hasNext()) {
                XMLStructure info = (XMLStructure) ki.next();
                if (!(info instanceof X509Data))
                    continue;
                X509Data x509Data = (X509Data) info;
                Iterator xi = x509Data.getContent().iterator();
                while (xi.hasNext()) {
                    Object o = xi.next();
                    if (!(o instanceof X509Certificate))
                        continue;
                    final PublicKey key = ((X509Certificate)o).getPublicKey();
                    // Make sure the algorithm is compatible
                    // with the method.
                    if (algEquals(method.getAlgorithm(), key.getAlgorithm())) {
                        return new KeySelectorResult() {
                            public Key getKey() { return key; }
                        };
                    }
                }
            }
            throw new KeySelectorException("No key found!");
        }
        //
        static boolean algEquals(String algURI, String algName) {
            if ((algName.equalsIgnoreCase("DSA") &&
                algURI.equalsIgnoreCase(SignatureMethod.DSA_SHA1)) ||
                (algName.equalsIgnoreCase("RSA") &&
                    algURI.equalsIgnoreCase(SignatureMethod.RSA_SHA1))) {
                return true;
            } else {
                return false;
            }
        }
    }

}
