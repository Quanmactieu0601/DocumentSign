package vn.easyca.signserver.business.services.signing.signer;

import vn.easyca.signserver.business.services.signing.dto.request.SignElement;
import vn.easyca.signserver.business.services.signing.dto.response.SigningDataResponse;
import vn.easyca.signserver.pki.sign.integrated.xml.SignXMLDto;
import vn.easyca.signserver.pki.sign.integrated.xml.SignXMLLib;
import vn.easyca.signserver.business.services.signing.dto.request.SignRequest;
import vn.easyca.signserver.business.services.signing.dto.request.content.XMLSignContent;

import java.util.Map;

public class XmlSigner {

    private final CryptoTokenProxy cryptoTokenProxy;

    public XmlSigner(CryptoTokenProxy cryptoTokenProxy) {
        this.cryptoTokenProxy = cryptoTokenProxy;
    }

    public SigningDataResponse<String> sign(SignRequest<XMLSignContent> request) throws Exception {
        SignXMLLib lib = new SignXMLLib();
        Map.Entry<String, SignElement<XMLSignContent>> entry = request.getSignElements().entrySet().iterator().next();
        SignXMLDto signXMLDto = new SignXMLDto(entry.getValue().getContent().getXml(),
            cryptoTokenProxy.getPrivateKey(),
            cryptoTokenProxy.getPublicKey(),
            cryptoTokenProxy.getX509Certificate());
        String xml = lib.generateXMLDigitalSignature(signXMLDto);
        return new SigningDataResponse<String>(xml, cryptoTokenProxy.getBase64Certificate());
    }
}
