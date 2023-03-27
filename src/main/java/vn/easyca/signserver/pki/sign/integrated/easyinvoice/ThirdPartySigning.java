package vn.easyca.signserver.pki.sign.integrated.easyinvoice;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import vn.easyca.signserver.core.domain.CertificateDTO;
import vn.easyca.signserver.core.dto.sign.request.SignRequest;
import vn.easyca.signserver.core.dto.sign.response.SignDataResponse;
import vn.easyca.signserver.core.dto.sign.response.SignResultElement;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.core.exception.CertificateNotFoundAppException;
import vn.easyca.signserver.core.services.SigningService;
import vn.easyca.signserver.pki.sign.integrated.easyinvoice.rsspDTO.SignatureHashData;
import vn.easyca.signserver.pki.sign.integrated.easyinvoice.rsspDTO.SigningHashData;
import vn.easyca.signserver.pki.sign.integrated.easyinvoice.rsspDTO.Types;
import vn.easyca.signserver.pki.sign.integrated.easyinvoice.rsspDTO.request.RsSignHashesRequest;
import vn.easyca.signserver.pki.sign.integrated.easyinvoice.rsspDTO.response.RASignHashResponse;
import vn.easyca.signserver.pki.sign.integrated.easyinvoice.rsspDTO.response.RsSignHashResponse;
import vn.easyca.signserver.pki.sign.utils.X509Utils;
import vn.easyca.signserver.webapp.service.CertificateService;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.SignElementVM;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class ThirdPartySigning {
    private static final Logger log = LoggerFactory.getLogger(ThirdPartySigning.class);
    private final RestTemplate restTemplate;
    private final SigningService signService;
    private final CertificateService certificateService;
    private String raURL = "http://172.16.11.84:8787/api/";

    public ThirdPartySigning(RestTemplate restTemplate, SigningService signService, CertificateService certificateService) {
        this.restTemplate = restTemplate;
        this.signService = signService;
        this.certificateService = certificateService;
    }


    private HttpHeaders buildRequestHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("Authorization", this.bearer);
        headers.set("X-RSSP-BACKEND", "rssp02");
        return headers;
    }

    public CertificateDTO getCertInfo(String serial) throws CertificateNotFoundAppException {
        CertificateDTO cert = certificateService.getBySerial(serial);
        return cert;
    }

    public void checkCertInformation(CertificateDTO cert) throws Exception {
        X509Certificate certificate = X509Utils.StringToX509Certificate(cert.getRawData());
        Date now = new Date();
        if (certificate.getNotAfter().before(now)) {
            throw new Exception("-1,The certificate has expired");
        }
    }

    public RASignHashResponse signHashRssp(RsSignHashesRequest request) throws Exception {
        log.info("SignHash, username : {}", request.getUsername());
        HttpHeaders headers = buildRequestHeaders();
        HttpEntity<RsSignHashesRequest> httpRequest = new HttpEntity<>(request, headers);
        String url = raURL + "p/rssp/sign/signHashes";
        RASignHashResponse response = restTemplate.postForObject(url, httpRequest, RASignHashResponse.class);
        if (response.getStatus() != 0) {
            log.error("Sign Hash failed failed with error {}", response.getMsg());
            throw new Exception(response.getStatus() + "," + response.getMsg());
        }
        return response;
    }

    public RASignHashResponse sign(SignThirdPartyRequest request) throws Exception {
        CertificateDTO cert = getCertInfo(request.getData().getTokenInfo().getSerial());
        RASignHashResponse response = null;
        if (cert.getType() == 0) {
            response = handleSignHashEasySign(response, request);
        } else {
            RsSignHashesRequest signHashesRequest = setSignHashRsspRequest(request, cert);
            response = signHashRssp(signHashesRequest);
        }
        int size = response.getData().getNumSignature();
        if (!request.getData().getOptional().isReturnInputData()) {
            for (int i = 0; i < size; i++) {
                response.getData().getSignatures().get(i).setHashData(null);
            }
        }
        cert.setSignedTurnCount(cert.getSignedTurnCount() + size);
        certificateService.save(cert);
        return response;
    }

    public RsSignHashesRequest setSignHashRsspRequest(SignThirdPartyRequest request, CertificateDTO cert) throws Exception {
        String username = request.getUsername();
        String serial = request.getData().getTokenInfo().getSerial();
        String pin = request.getData().getTokenInfo().getPin();
        List<SignElementVM<String>> listHashes = request.getData().getElements();
        checkCertInformation(cert);

        RsSignHashesRequest signHashesRequest = new RsSignHashesRequest();
        signHashesRequest.setSerial(serial);
        signHashesRequest.setUsername(username);
        String hashAlgorithmRequest = request.getData().getOptional().getHashAlgorithm();
        Types.HashAlgorithmOID hashAlgorithm;
        if (StringUtils.isEmpty(hashAlgorithmRequest)) {
            hashAlgorithm = Types.HashAlgorithmOID.SHA_1;
        } else {
            hashAlgorithm = Types.HashAlgorithmOID.valueOf(hashAlgorithmRequest);
        }
        signHashesRequest.setHashAlgo(hashAlgorithm);
        SigningHashData[] hashData = new SigningHashData[listHashes.size()];
        for (int i = 0; i < listHashes.size(); i++) {
            hashData[i] = new SignatureHashData();
            hashData[i].setHashId(listHashes.get(i).getKey());
            hashData[i].setHashData(listHashes.get(i).getContent());
        }
        signHashesRequest.setHashData(hashData);
        if (cert.getAuthMode().equalsIgnoreCase("EXPLICIT/PIN")) {
            signHashesRequest.setConfirmType(2);
        } else {
            signHashesRequest.setConfirmType(1);
        }
        request.getConfirmData().setPin(pin);
        signHashesRequest.setConfirmData(request.getConfirmData());
        signHashesRequest.setNumHash(hashData.length);

        return signHashesRequest;
    }

    public RASignHashResponse mapEasySigningResponse(SignDataResponse<List<SignResultElement>> signingDataResponse, SignThirdPartyRequest request) {
        boolean returnInput = request.getData().getOptional().isReturnInputData();
        List<SignResultElement> signResults = signingDataResponse.getSignResult();
        int numSignatures = signResults.size();
        List<SignatureHashData> signatureHashData = new ArrayList<>();
        for (SignResultElement element : signResults) {
            SignatureHashData signatureHash = new SignatureHashData();
            signatureHash.setHashId(element.getKey());
            signatureHash.setSignature(element.getBase64Signature());
            if (returnInput) {
                signatureHash.setHashData(element.getInputData());
            }
            signatureHashData.add(signatureHash);
        }
        RsSignHashResponse rsSignHashResponse = new RsSignHashResponse();
        rsSignHashResponse.setNumSignature(numSignatures);
        rsSignHashResponse.setRemainingSigningCounter(-1);
        rsSignHashResponse.setSignatures(signatureHashData);
        RASignHashResponse response = new RASignHashResponse();
        response.setStatus(0);
        response.setData(rsSignHashResponse);
        return response;
    }


    public RASignHashResponse handleException(RASignHashResponse response, SignThirdPartyRequest request, Exception e) {
        if (e.getMessage().contains(",")) {
            String[] s = e.getMessage().split(",");
            response.setStatus(Integer.parseInt(s[0]));
            response.setMsg(s[1]);
        } else {
            response.setStatus(-1);
            response.setMsg(e.getMessage());
        }
        return response;
    }

    private RASignHashResponse handleSignHashEasySign(RASignHashResponse response, SignThirdPartyRequest request) {
        try {
            String hashAlgorithmRequest = request.getData().getOptional().getHashAlgorithm();
            String hashAlgorithm = hashAlgorithmRequest.replace("_", "");
            request.getData().getOptional().setHashAlgorithm(hashAlgorithm);
            SignRequest<String> signRequest = request.getData().getDTO(String.class);
            SignDataResponse<List<SignResultElement>> signingDataResponse = signService.signHash(signRequest, false);
            response = mapEasySigningResponse(signingDataResponse, request);
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            response.setStatus(applicationException.getCode());
            response.setMsg(applicationException.getMessage());
        } catch (Exception ex) {
            log.error(ex.getMessage());
            response.setStatus(-1);
            response.setMsg(ex.getMessage());
        }
        return response;
    }
}
