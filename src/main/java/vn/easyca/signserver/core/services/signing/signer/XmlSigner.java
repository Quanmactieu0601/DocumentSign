package vn.easyca.signserver.core.services.signing.signer;

import vn.easyca.signserver.core.services.signing.dto.response.SigningDataResponse;
import vn.easyca.signserver.sign.core.sign.integrated.xml.SignXMLDto;
import vn.easyca.signserver.sign.core.sign.integrated.xml.SignXMLLib;
import vn.easyca.signserver.core.services.signing.dto.request.SigningRequest;
import vn.easyca.signserver.core.services.signing.dto.request.content.XMLSigningContent;

public class XmlSigner {

    private final CryptoTokenProxy cryptoTokenProxy;

    public XmlSigner(CryptoTokenProxy cryptoTokenProxy) {
        this.cryptoTokenProxy = cryptoTokenProxy;
    }

    public SigningDataResponse<String> sign(SigningRequest<XMLSigningContent> request) throws Exception {

        SignXMLLib lib = new SignXMLLib();
        SignXMLDto signXMLDto = new SignXMLDto(request.getContent().getXml(),
            cryptoTokenProxy.getPrivateKey(),
            cryptoTokenProxy.getPublicKey(),
            cryptoTokenProxy.getX509Certificate());
        String xml = lib.generateXMLDigitalSignature(signXMLDto);
        return new SigningDataResponse<String>(xml, cryptoTokenProxy.getBase64Certificate());
    }
}
