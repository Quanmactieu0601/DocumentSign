package vn.easyca.signserver.webapp.service.model.xmlsigner;

import vn.easyca.signserver.core.sign.integrated.xml.SignXMLDto;
import vn.easyca.signserver.core.sign.integrated.xml.SignXMLLib;
import vn.easyca.signserver.webapp.service.dto.request.SignXMLRequest;
import vn.easyca.signserver.webapp.service.model.Signature;

public class XmlSigner {

    private Signature signature;

    public XmlSigner(Signature signature) {
        this.signature = signature;
    }

    public String sign(SignXMLRequest request) throws Exception {

        SignXMLLib lib = new SignXMLLib();
        SignXMLDto signXMLDto = new SignXMLDto(request.getXml(),
            request.getContentTag(),
            signature.getPrivateKey(),
            signature.getPublicKey());
        return lib.generateXMLDigitalSignature(signXMLDto);
    }


}
