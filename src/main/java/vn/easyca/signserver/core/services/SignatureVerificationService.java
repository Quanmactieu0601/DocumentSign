package vn.easyca.signserver.core.services;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.*;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;
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

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.*;
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

    public SignatureVerificationResponse verifyRaw(SignatureVerificationRequest request) throws ApplicationException {
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
            try {
                boolean result = rawValidator.verify(element.getOriginalData(), element.getSignature(), x509Certificate, request.getHashAlgorithm());
                response.add(element.getKey(), result);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
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
            InputStream rootCaStream = fileResourceService.getRootCer(FileResourceService.EASY_CA);
            ks.setCertificateEntry("root", cf.generateCertificate(rootCaStream));

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
}
