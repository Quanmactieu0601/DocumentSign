package vn.easyca.signserver.pki.sign.integrated.easyinvoice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import vn.easyca.signserver.core.dto.sign.response.SignDataResponse;
import vn.easyca.signserver.core.dto.sign.response.SignResultElement;
import vn.easyca.signserver.core.factory.CryptoTokenProxyFactory;
import vn.easyca.signserver.pki.sign.integrated.easyinvoice.rsspDTO.SignatureHashData;
import vn.easyca.signserver.pki.sign.integrated.easyinvoice.rsspDTO.SigningHashData;
import vn.easyca.signserver.pki.sign.integrated.easyinvoice.rsspDTO.request.CertificateInfoRequest;
import vn.easyca.signserver.pki.sign.integrated.easyinvoice.rsspDTO.request.RsSignHashesRequest;
import vn.easyca.signserver.pki.sign.integrated.easyinvoice.rsspDTO.response.CredentialInfoResponse;
import vn.easyca.signserver.pki.sign.integrated.easyinvoice.rsspDTO.response.RACertificateResponse;
import vn.easyca.signserver.pki.sign.integrated.easyinvoice.rsspDTO.response.RASignHashResponse;
import vn.easyca.signserver.pki.sign.integrated.easyinvoice.rsspDTO.response.RsSignHashResponse;
import vn.easyca.signserver.webapp.service.CertificateService;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.SignElementVM;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class ThirdPartySigning {
    private static final Logger log = LoggerFactory.getLogger(ThirdPartySigning.class);
    private final RestTemplate restTemplate;
    private final CryptoTokenProxyFactory cryptoTokenProxyFactory;
    private final CertificateService certificateService;

    public ThirdPartySigning(RestTemplate restTemplate, CryptoTokenProxyFactory cryptoTokenProxyFactory, CertificateService certificateService) {
        this.restTemplate = restTemplate;
        this.cryptoTokenProxyFactory = cryptoTokenProxyFactory;
        this.certificateService = certificateService;
    }


    private HttpHeaders buildRequestHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("Authorization", this.bearer);
        headers.set("X-RSSP-BACKEND", "rssp02");
        return headers;
    }


    public RACertificateResponse getCertificateInfo(String serial, String username) throws Exception {
        CertificateInfoRequest request = new CertificateInfoRequest();
        request.setUsername(username);
        request.setSerial(serial);
        request.setCertificates("");
        request.setCertInfoEnabled(true);
        request.setAuthInfoEnabled(true);
        log.info("Get certificate-info, serial: {}", request.getSerial());
        HttpHeaders headers = buildRequestHeaders();
        HttpEntity<CertificateInfoRequest> httpRequest = new HttpEntity<>(request, headers);
        String url = "http://172.16.11.84:8787/api/p/rssp/enroll/cert-info";
        RACertificateResponse response = restTemplate.postForObject(url, httpRequest, RACertificateResponse.class);
        if (response.getStatus() != 0) {
            log.error("Certificate info failed failed with error {}", response.getMsg());
            throw new Exception(response.getStatus() + "," + response.getMsg());
        }
        return response;
    }

    public void checkCertInformation(CredentialInfoResponse credentialInfoResponse) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        Date validTo = format.parse(credentialInfoResponse.getNotAfter());
        Date now = new Date();
        if (validTo.before(now)) {
            throw new Exception("-1,The certificate has expired");
        }
        int signRemainingCounter = credentialInfoResponse.getRemainingSigningCounter();
        if (signRemainingCounter <= 0)
            throw new Exception("-1,The certificate has run out of Remaining Signing Counter");
    }

    public RASignHashResponse signHash(RsSignHashesRequest request) throws Exception {
        log.info("SignHash, username : {}", request.getUsername());
        HttpHeaders headers = buildRequestHeaders();
        HttpEntity<RsSignHashesRequest> httpRequest = new HttpEntity<>(request, headers);
        String url = "http://172.16.11.84:8787/api/p/rssp/sign/signHashes";
        RASignHashResponse response = restTemplate.postForObject(url, httpRequest, RASignHashResponse.class);
        if (response.getStatus() != 0) {
            log.error("Sign Hash failed failed with error {}", response.getMsg());
            throw new Exception(response.getStatus() + "," + response.getMsg());
        }
        return response;
    }

    public RASignHashResponse sign(SignThirdPartyRequest request) throws Exception {
        RsSignHashesRequest signHashesRequest = setSignHashRequest(request);
        RASignHashResponse response = signHash(signHashesRequest);
        int size = response.getData().getNumSignature();
        return response;
    }

    public RsSignHashesRequest setSignHashRequest(SignThirdPartyRequest request) throws Exception {
        String username = request.getUsername();
        String serial = request.getData().getTokenInfo().getSerial();
        String pin = request.getData().getTokenInfo().getPin();
        List<SignElementVM<String>> listHashes = request.getData().getElements();
        CredentialInfoResponse credentialInfoResponse = getCertificateInfo(serial, username).getData();
        checkCertInformation(credentialInfoResponse);

        RsSignHashesRequest signHashesRequest = new RsSignHashesRequest();
        signHashesRequest.setSerial(serial);
        signHashesRequest.setUsername(username);
        signHashesRequest.setHashAlgo(request.getData().getOptional().getHashAlgorithm());
        SigningHashData[] hashData = new SigningHashData[listHashes.size()];
        for (int i = 0; i < listHashes.size(); i++) {
            hashData[i] = new SignatureHashData();
            hashData[i].setHashId(listHashes.get(i).getKey());
            hashData[i].setHashData(listHashes.get(i).getContent());
        }
        signHashesRequest.setHashData(hashData);
        if (credentialInfoResponse.getAuthMode().getValue().equalsIgnoreCase("EXPLICIT/PIN")) {
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
        RASignHashResponse response = new RASignHashResponse();
        RsSignHashResponse rsSignHashResponse = new RsSignHashResponse();
        boolean returnInput = request.getData().getOptional().isReturnInputData();
        int size = request.getData().getElements().size();
        rsSignHashResponse.setNumSignature(size);
        rsSignHashResponse.setRemainingSigningCounter(-1);
        List<SignatureHashData> signatureHashData = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            signatureHashData.add(new SignatureHashData());
            SignResultElement element = signingDataResponse.getSignResult().get(i);
            signatureHashData.get(i).setHashId(element.getKey());
            signatureHashData.get(i).setSignature(element.getBase64Signature());
            if (returnInput)
                signatureHashData.get(i).setHashData(element.getInputData());
        }
        rsSignHashResponse.setSignatures(signatureHashData);
        response.setStatus(0);
        response.setData(rsSignHashResponse);
        return response;
    }
}
