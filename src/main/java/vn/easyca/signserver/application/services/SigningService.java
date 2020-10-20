package vn.easyca.signserver.application.services;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.application.dto.sign.request.SignElement;
import vn.easyca.signserver.application.exception.*;
import vn.easyca.signserver.application.dto.sign.TokenInfoDTO;
import vn.easyca.signserver.application.dto.sign.request.SignRequest;
import vn.easyca.signserver.application.dto.sign.request.content.PDFSignContent;
import vn.easyca.signserver.application.dto.sign.request.content.XMLSignContent;
import vn.easyca.signserver.application.dto.sign.response.PDFSigningDataRes;
import vn.easyca.signserver.application.dto.sign.response.SignDataResponse;
import vn.easyca.signserver.application.dto.sign.response.SignResultElement;
import vn.easyca.signserver.application.model.token.CryptoTokenProxyException;
import vn.easyca.signserver.application.model.token.CryptoTokenProxyFactory;
import vn.easyca.signserver.application.model.token.CryptoTokenProxy;
import vn.easyca.signserver.application.repository.CertificateRepository;
import vn.easyca.signserver.application.utils.CommonUtils;
import vn.easyca.signserver.pki.sign.integrated.pdf.PartyMode;
import vn.easyca.signserver.pki.sign.integrated.pdf.SignPDFDto;
import vn.easyca.signserver.pki.sign.integrated.pdf.SignPDFPlugin;
import vn.easyca.signserver.pki.sign.integrated.xml.SignXMLDto;
import vn.easyca.signserver.pki.sign.integrated.xml.SignXMLLib;
import vn.easyca.signserver.pki.sign.rawsign.RawSigner;
import vn.easyca.signserver.pki.sign.utils.UniqueID;
import vn.easyca.signserver.pki.cryptotoken.error.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SigningService {
    private final static String TEM_DIR = "./TemFile/";

    private final CryptoTokenProxyFactory cryptoTokenProxyFactory;

    private final CertificateRepository certificateRepository;

    public SigningService(CryptoTokenProxyFactory cryptoTokenProxyFactory, CertificateRepository certificateRepository) {
        this.cryptoTokenProxyFactory = cryptoTokenProxyFactory;
        this.certificateRepository = certificateRepository;
        File file = new File(TEM_DIR);
        if (!file.exists())
            file.mkdir();
    }

    public PDFSigningDataRes signPDFFile(SignRequest<PDFSignContent> request) throws ApplicationException {
        SignElement<PDFSignContent> element = request.getSignElements().get(0);
        if (element == null)
            throw new BadServiceInputAppException("have not element to sign", null);

        TokenInfoDTO tokenInfoDTO = request.getTokenInfoDTO();
        vn.easyca.signserver.application.domain.Certificate certificate = certificateRepository.getBySerial(tokenInfoDTO.getSerial());
        if (certificate == null)
            throw new CertificateNotFoundAppException();
        CryptoTokenProxy cryptoTokenProxy = null;
        try {
            cryptoTokenProxy = cryptoTokenProxyFactory.resolveCryptoTokenProxy(certificate);
        } catch (CryptoTokenProxyException e) {
            throw new CertificateAppException("Certificate has error", e);
        }

        String temFilePath = TEM_DIR + UniqueID.generate() + ".pdf";
        File file = new File(temFilePath);
        PDFSignContent content = element.getContent();
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new ApplicationInternalError(e);
        }

        SignPDFPlugin signPDFPlugin = new SignPDFPlugin();
        SignPDFDto signPDFDto = null;
        try {
            signPDFDto = SignPDFDto.build(
                PartyMode.SIGN_SERVER,
                content.getFileData(),
                cryptoTokenProxy.getPrivateKey(),
                new Certificate[]{cryptoTokenProxy.getX509Certificate()},
                temFilePath
            );
        } catch (CryptoTokenException | CertificateException e) {
            throw new CertificateAppException("Certificate has error", e);
        }
        signPDFDto.setSignField(element.getSigner());
        signPDFDto.setSigner(element.getSigner());
        signPDFDto.setSignDate(element.getSignDate());
        signPDFDto.setLocation(content.getInfo().getLocation());
        signPDFDto.setVisibleWidth(content.getVisible().getVisibleWidth());
        signPDFDto.setVisibleHeight(content.getVisible().getVisibleHeight());
        signPDFDto.setVisibleX(content.getVisible().getVisibleX());
        signPDFDto.setVisibleY(content.getVisible().getVisibleY());
        try {
            signPDFPlugin.sign(signPDFDto);
            byte[] res = IOUtils.toByteArray(new FileInputStream(temFilePath));
            return new PDFSigningDataRes(res);
        } catch (Exception exception) {
            throw new SigningAppException("Sign PDF occurs error", exception);
        }
    }

    public SignDataResponse<List<SignResultElement>> signHash(SignRequest<String> request) throws ApplicationException {
        TokenInfoDTO tokenInfoDTO = request.getTokenInfoDTO();
        vn.easyca.signserver.application.domain.Certificate certificate = certificateRepository.getBySerial(tokenInfoDTO.getSerial());
        if (certificate == null)
            throw new CertificateNotFoundAppException();
        CryptoTokenProxy cryptoTokenProxy = null;
        try {
            cryptoTokenProxy = cryptoTokenProxyFactory.resolveCryptoTokenProxy(certificate);
        } catch (CryptoTokenProxyException e) {
            throw new CertificateAppException("Certificate has error!please check serial and pin", e);
        }
        List<SignResultElement> resultElements = new ArrayList<>();
        List<SignElement<String>> signElements = request.getSignElements();
        vn.easyca.signserver.pki.sign.rawsign.RawSigner rawSigner = new vn.easyca.signserver.pki.sign.rawsign.RawSigner();
        for (SignElement<String> signElement : signElements) {
            byte[] hash = CommonUtils.decodeBase64(signElement.getContent());
            byte[] signature = new byte[0];
            try {
                signature = rawSigner.signHash(hash, cryptoTokenProxy.getPrivateKey());
            } catch (Exception exception) {
                throw new SigningAppException("Sign has occurs error", exception);
            }
            String signContent = request.isReturnInputData() ? signElement.getContent() : null;
            SignResultElement signResultElement = SignResultElement.create(signature, signContent, signElement.getKey());
            resultElements.add(signResultElement);
        }
        return new SignDataResponse<>(resultElements, cryptoTokenProxy.getBase64Certificate());
    }

    public SignDataResponse<List<SignResultElement>> signRaw(SignRequest<String> request) throws ApplicationException {
        List<SignElement<String>> signElements = request.getSignElements();
        if (signElements == null || signElements.size() == 0)
            throw new BadServiceInputAppException("have not element to sign");

        TokenInfoDTO tokenInfoDTO = request.getTokenInfoDTO();
        if (tokenInfoDTO == null)
            throw new BadServiceInputAppException("have not token info");
        vn.easyca.signserver.application.domain.Certificate certificate = certificateRepository.getBySerial(tokenInfoDTO.getSerial());
        if (certificate == null)
            throw new CertificateNotFoundAppException();

        CryptoTokenProxy cryptoTokenProxy = null;
        try {
            cryptoTokenProxy = cryptoTokenProxyFactory.resolveCryptoTokenProxy(certificate);
        } catch (CryptoTokenProxyException e) {
            throw new CertificateAppException(e);
        }

        PrivateKey privateKey = null;
        try {
            privateKey = cryptoTokenProxy.getPrivateKey();
        } catch (CryptoTokenException e) {
            throw new SigningAppException("Sign has occurs error", e);
        }

        List<SignResultElement> resultElements = new ArrayList<>();
        RawSigner rawSigner = new RawSigner();
        for (SignElement<String> signElement : signElements) {
            byte[] data = CommonUtils.decodeBase64(signElement.getContent());
            byte[] signature = new byte[0];
            try {
                signature = rawSigner.signData(data, privateKey, request.getHashAlgorithm());
            } catch (Exception exception) {
                throw new SigningAppException("Sign data occurs error!", exception);
            }
            String signContent = request.isReturnInputData() ? signElement.getContent() : null;
            SignResultElement signResultElement = SignResultElement.create(signature, signContent, signElement.getKey());
            resultElements.add(signResultElement);
        }
        return new SignDataResponse<>(resultElements, cryptoTokenProxy.getBase64Certificate());
    }

    public SignDataResponse<String> signXML(SignRequest<XMLSignContent> request) throws ApplicationException {
        TokenInfoDTO tokenInfoDTO = request.getTokenInfoDTO();
        vn.easyca.signserver.application.domain.Certificate certificate = certificateRepository.getBySerial(tokenInfoDTO.getSerial());
        if (certificate == null)
            throw new CertificateNotFoundAppException();
        CryptoTokenProxy cryptoTokenProxy = null;
        try {
            cryptoTokenProxy = cryptoTokenProxyFactory.resolveCryptoTokenProxy(certificate);
        } catch (CryptoTokenProxyException e) {
            throw new CertificateAppException(e);
        }
        SignXMLLib lib = new SignXMLLib();
        SignElement<XMLSignContent> signElement = request.getSignElements().get(0);
        SignXMLDto signXMLDto = null;
        try {
            signXMLDto = new SignXMLDto(signElement.getContent().getXml(),
                cryptoTokenProxy.getPrivateKey(),
                cryptoTokenProxy.getPublicKey(),
                cryptoTokenProxy.getX509Certificate());
        } catch (CryptoTokenException | CertificateException e) {
            throw new CertificateAppException(e);
        }
        try {
            String xml = lib.generateXMLDigitalSignature(signXMLDto);
            return new SignDataResponse<>(xml, cryptoTokenProxy.getBase64Certificate());
        } catch (Exception ex) {
            throw new SigningAppException("Sign Xml hash error", ex);
        }
    }
}
