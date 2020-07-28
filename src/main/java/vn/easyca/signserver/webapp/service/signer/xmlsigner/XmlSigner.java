package vn.easyca.signserver.webapp.service.signer.xmlsigner;

import vn.easyca.signserver.core.sign.integrated.xml.SignXMLDto;
import vn.easyca.signserver.core.sign.integrated.xml.SignXMLLib;
import vn.easyca.signserver.webapp.service.dto.signing.request.SigningRequest;
import vn.easyca.signserver.webapp.service.dto.signing.request.XMLSigningData;
import vn.easyca.signserver.webapp.service.signer.CryptoTokenProxy;

public class XmlSigner {

    private CryptoTokenProxy cryptoTokenProxy;

    public XmlSigner(CryptoTokenProxy cryptoTokenProxy) {
        this.cryptoTokenProxy = cryptoTokenProxy;
    }

    public String sign(SigningRequest<XMLSigningData> request) throws Exception {

        SignXMLLib lib = new SignXMLLib();
        SignXMLDto signXMLDto = new SignXMLDto(request.getData().getXml(),
            cryptoTokenProxy.getPrivateKey(),
            cryptoTokenProxy.getPublicKey(),
            cryptoTokenProxy.getX509Certificate());
        return lib.generateXMLDigitalSignature(signXMLDto);
    }


}
