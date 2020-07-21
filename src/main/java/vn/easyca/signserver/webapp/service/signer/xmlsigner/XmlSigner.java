package vn.easyca.signserver.webapp.service.signer.xmlsigner;

import vn.easyca.signserver.core.sign.integrated.xml.SignXMLDto;
import vn.easyca.signserver.core.sign.integrated.xml.SignXMLLib;
import vn.easyca.signserver.webapp.service.dto.request.SignXMLRequest;
import vn.easyca.signserver.webapp.service.signer.CryptoTokenProxy;

import java.security.cert.X509Certificate;

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
            (X509Certificate) cryptoTokenProxy.getX509Certificates()[0]);
        return lib.generateXMLDigitalSignature(signXMLDto);
    }


}
