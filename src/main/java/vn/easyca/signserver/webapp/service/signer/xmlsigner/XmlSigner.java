package vn.easyca.signserver.webapp.service.signer.xmlsigner;

import vn.easyca.signserver.core.sign.integrated.xml.SignXMLDto;
import vn.easyca.signserver.core.sign.integrated.xml.SignXMLLib;
import vn.easyca.signserver.webapp.service.dto.request.SignXMLRequest;
import vn.easyca.signserver.webapp.service.signer.CryptoTokenProxy;

public class XmlSigner {

    private CryptoTokenProxy cryptoTokenProxy;

    public XmlSigner(CryptoTokenProxy cryptoTokenProxy) {
        this.cryptoTokenProxy = cryptoTokenProxy;
    }

    public String sign(SignXMLRequest request) throws Exception {

        SignXMLLib lib = new SignXMLLib();
        SignXMLDto signXMLDto = new SignXMLDto(request.getXml(),
            cryptoTokenProxy.getPrivateKey(),
            cryptoTokenProxy.getPublicKey(),
            cryptoTokenProxy.getX509Certificate());
        return lib.generateXMLDigitalSignature(signXMLDto);
    }


}
