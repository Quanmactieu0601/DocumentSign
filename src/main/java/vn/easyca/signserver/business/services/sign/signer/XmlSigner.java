package vn.easyca.signserver.business.services.sign.signer;

import vn.easyca.signserver.business.services.sign.dto.request.SignElement;
import vn.easyca.signserver.business.services.sign.dto.response.SignDataResponse;
import vn.easyca.signserver.pki.sign.integrated.xml.SignXMLDto;
import vn.easyca.signserver.pki.sign.integrated.xml.SignXMLLib;
import vn.easyca.signserver.business.services.sign.dto.request.SignRequest;
import vn.easyca.signserver.business.services.sign.dto.request.content.XMLSignContent;

public class XmlSigner {

    private final CryptoTokenProxy cryptoTokenProxy;

    public XmlSigner(CryptoTokenProxy cryptoTokenProxy) {
        this.cryptoTokenProxy = cryptoTokenProxy;
    }

    public SignDataResponse<String> sign(SignRequest<XMLSignContent> request) throws Exception {
        SignXMLLib lib = new SignXMLLib();
        SignElement<XMLSignContent> signElement = request.getSignElements().get(0);
        SignXMLDto signXMLDto = new SignXMLDto(signElement.getContent().getXml(),
            cryptoTokenProxy.getPrivateKey(),
            cryptoTokenProxy.getPublicKey(),
            cryptoTokenProxy.getX509Certificate());
        String xml = lib.generateXMLDigitalSignature(signXMLDto);
        return new SignDataResponse<>(xml, cryptoTokenProxy.getBase64Certificate());
    }
}
