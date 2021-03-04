package vn.easyca.signserver.core.services;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.core.domain.CertificateDTO;
import vn.easyca.signserver.core.dto.OptionalDTO;
import vn.easyca.signserver.core.dto.sign.newrequest.Location;
import vn.easyca.signserver.core.dto.sign.newrequest.SigningRequest;
import vn.easyca.signserver.core.dto.sign.newrequest.VisibleRequestContent;
import vn.easyca.signserver.core.dto.sign.request.SignElement;
import vn.easyca.signserver.core.exception.*;
import vn.easyca.signserver.core.dto.sign.TokenInfoDTO;
import vn.easyca.signserver.core.dto.sign.request.SignRequest;
import vn.easyca.signserver.core.dto.sign.response.PDFSigningDataRes;
import vn.easyca.signserver.core.dto.sign.response.SignDataResponse;
import vn.easyca.signserver.core.dto.sign.response.SignResultElement;
import vn.easyca.signserver.core.factory.CryptoTokenProxyFactory;
import vn.easyca.signserver.core.factory.CryptoTokenProxy;
import vn.easyca.signserver.core.utils.CertUtils;
import vn.easyca.signserver.pki.sign.integrated.pdf.visible.PartyMode;
import vn.easyca.signserver.pki.sign.integrated.pdf.visible.SignPDFDto;
import vn.easyca.signserver.pki.sign.integrated.pdf.visible.SignPDFPlugin;
import vn.easyca.signserver.pki.sign.rawsign.RawSigner;
import vn.easyca.signserver.pki.sign.utils.UniqueID;
import vn.easyca.signserver.pki.cryptotoken.error.*;
import vn.easyca.signserver.webapp.enm.SignatureTemplateParserType;
import vn.easyca.signserver.webapp.service.CertificateService;
import vn.easyca.signserver.webapp.service.parser.SignatureTemplateParseService;
import vn.easyca.signserver.webapp.service.parser.SignatureTemplateParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SigningService {
    private final static String TEM_DIR = "./TemFile/";

    private final CryptoTokenProxyFactory cryptoTokenProxyFactory;
    private final SignatureTemplateParserFactory signatureTemplateParserFactory;
    private final CertificateService certificateService;

    public SigningService(CertificateService certificateService, CryptoTokenProxyFactory cryptoTokenProxyFactory, SignatureTemplateParserFactory signatureTemplateParserFactory) {
        this.cryptoTokenProxyFactory = cryptoTokenProxyFactory;
        this.certificateService = certificateService;
        this.signatureTemplateParserFactory = signatureTemplateParserFactory;
        File file = new File(TEM_DIR);
        if (!file.exists())
            file.mkdir();
    }

    public PDFSigningDataRes signPDFFile(SigningRequest request) throws ApplicationException {
        CertificateDTO certificateDTO = certificateService.getBySerial(request.getTokenInfo().getSerial());
        if (certificateDTO == null)
            throw new CertificateNotFoundAppException();
        CryptoTokenProxy cryptoTokenProxy = cryptoTokenProxyFactory.resolveCryptoTokenProxy(certificateDTO, request.getTokenInfo().getPin(), request.getOptional().getOtpCode());

        String temFilePath = TEM_DIR + UniqueID.generate() + ".pdf";
        File file = new File(temFilePath);

        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new ApplicationInternalError(e);
        }

        SignPDFPlugin signPDFPlugin = new SignPDFPlugin();
        SignPDFDto signPDFDto = null;
        List<VisibleRequestContent> visibleRequestContents = request.getSigningRequestContents();
        if (visibleRequestContents == null) {
            throw new ApplicationException("Chưa có nội dung đẩy lên");
        }
        VisibleRequestContent firstContent = visibleRequestContents.get(0);
        try {
            signPDFDto = SignPDFDto.build(
                PartyMode.SIGN_SERVER,
                firstContent.getData(),
                cryptoTokenProxy.getPrivateKey(),
                new Certificate[]{cryptoTokenProxy.getX509Certificate()},
                temFilePath
            );
        } catch (CryptoTokenException e) {
            throw new CertificateAppException("Certificate has error", e);
        }

        String subjectDN = certificateDTO.getX509Certificate().getSubjectDN().getName();
        SignatureTemplateParseService signatureTemplateParseService = signatureTemplateParserFactory.resolve(SignatureTemplateParserType.DEFAULT);
        String signer = signatureTemplateParseService.getSigner(subjectDN);

        Location location = firstContent.getLocation();

        signPDFDto.setSignField(signer);
        signPDFDto.setSigner(signer);
        signPDFDto.setSignDate(new Date());
        signPDFDto.setVisibleWidth(location.getVisibleWidth());
        signPDFDto.setVisibleHeight(location.getVisibleHeight());
        signPDFDto.setVisibleX(location.getVisibleX());
        signPDFDto.setVisibleY(location.getVisibleY());
        signPDFDto.setPageNumber(firstContent.getExtraInfo().getPageNum());
        try {
            signPDFPlugin.sign(signPDFDto);
            byte[] res = IOUtils.toByteArray(new FileInputStream(temFilePath));
            file.delete();
            return new PDFSigningDataRes(res);
        } catch (Exception exception) {
            throw new SigningAppException("Sign PDF occurs error", exception);
        }
    }

    public SignDataResponse<List<SignResultElement>> signHash(SignRequest<String> request) throws ApplicationException {
        TokenInfoDTO tokenInfoDTO = request.getTokenInfoDTO();
        CertificateDTO certificateDTO = certificateService.getBySerial(tokenInfoDTO.getSerial());
        if (certificateDTO == null)
            throw new CertificateNotFoundAppException();
        OptionalDTO optionalDTO = request.getOptional();
        String otp = optionalDTO.getOtpCode();
        CryptoTokenProxy cryptoTokenProxy = cryptoTokenProxyFactory.resolveCryptoTokenProxy(certificateDTO, request.getTokenInfoDTO().getPin(), otp);

        List<SignResultElement> resultElements = new ArrayList<>();
        List<SignElement<String>> signElements = request.getSignElements();
        RawSigner rawSigner = new RawSigner();
//        String hashAlgorithm = request.getOptional().getHashAlgorithm();
        for (SignElement<String> signElement : signElements) {
            byte[] hash = CertUtils.decodeBase64(signElement.getContent());
            byte[] signature = new byte[0];
            try {
//                signature = rawSigner.signHash(hash, cryptoTokenProxy.getPrivateKey(), hashAlgorithm);
                signature = rawSigner.signHashWithoutDigestInfo(hash, cryptoTokenProxy.getPrivateKey());
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
            throw new BadServiceInputAppException("tokeninfo is empty");

        OptionalDTO optionalDTO = request.getOptional();
        String otp = optionalDTO.getOtpCode();

        CertificateDTO certificateDTO = certificateService.getBySerial(tokenInfoDTO.getSerial());
        if (certificateDTO == null)
            throw new CertificateNotFoundAppException();

        CryptoTokenProxy cryptoTokenProxy = cryptoTokenProxyFactory.resolveCryptoTokenProxy(certificateDTO, tokenInfoDTO.getPin(), otp);

        PrivateKey privateKey = null;
        try {
            privateKey = cryptoTokenProxy.getPrivateKey();
        } catch (CryptoTokenException e) {
            throw new SigningAppException("Sign has occurs error, please check PIN number", e);
        }

        List<SignResultElement> resultElements = new ArrayList<>();
        RawSigner rawSigner = new RawSigner();
        for (SignElement<String> signElement : signElements) {
            byte[] data = CertUtils.decodeBase64(signElement.getContent());
            byte[] signature = new byte[0];
            try {
                signature = rawSigner.signData(data, privateKey, cryptoTokenProxy.getCryptoToken().getSignatureInstance(request.getHashAlgorithm()));
            } catch (Exception exception) {
                throw new SigningAppException("Sign data occurs error!", exception);
            }
            String signContent = request.isReturnInputData() ? signElement.getContent() : null;
            SignResultElement signResultElement = SignResultElement.create(signature, signContent, signElement.getKey());
            resultElements.add(signResultElement);
        }
        return new SignDataResponse<>(resultElements, cryptoTokenProxy.getBase64Certificate());
    }


}
