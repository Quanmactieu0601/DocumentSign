package vn.easyca.signserver.webapp.service.model.pdfsigner;


import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import vn.easyca.signserver.core.sign.integrated.pdf.SignPDFLib;
import vn.easyca.signserver.core.sign.integrated.pdf.SignPDFPlugin;
import vn.easyca.signserver.core.sign.utils.UniqueID;
import vn.easyca.signserver.webapp.service.dto.PDFSignRequest;
import vn.easyca.signserver.webapp.service.dto.SignatureInfo;
import vn.easyca.signserver.webapp.service.model.Signature;

import javax.security.cert.Certificate;
import java.io.File;
import java.io.FileInputStream;
import java.util.Date;

@RequiredArgsConstructor
public class PDFSigner {

    @NonNull
    private Signature signature;

    @NonNull
    private String cacheDir;

    private PDFSignatureInfo signatureInfo = new PDFSignatureInfo();

    private PDFSignatureVisibility visibility = new PDFSignatureVisibility();

    public byte[] signPDF(PDFSignRequest request) throws Exception {

        String temFilePath = cacheDir + UniqueID.generate() + ".pdf";
        File file = new File(temFilePath);
        if (!file.exists())
            file.createNewFile();
        SignPDFPlugin signPDFPlugin = new SignPDFPlugin();
        signPDFPlugin.sign(request.getContent(),
            signature.getPrivateKey(),
            signature.getX509Certificates(),
            signatureInfo.build(),
            new PDFSignatureVisibility().build(),
            "",
            signature.getHashAlgorithm(),
            request.getSignDate(),
            temFilePath);
        return IOUtils.toByteArray(new FileInputStream(temFilePath));
    }

    public PDFSignatureInfo getSignatureInfo() {
        return signatureInfo;
    }

    public PDFSignatureVisibility getVisibility() {
        return visibility;
    }

}
