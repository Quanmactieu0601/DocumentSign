package vn.easyca.signserver.webapp.service.model.pdfsigner;


import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import vn.easyca.signserver.core.sign.integrated.pdf.SignPDFPlugin;
import vn.easyca.signserver.core.sign.utils.UniqueID;
import vn.easyca.signserver.webapp.service.dto.request.SignPDFRequest;
import vn.easyca.signserver.webapp.service.model.Signature;

import java.io.File;
import java.io.FileInputStream;

@RequiredArgsConstructor
public class PDFSigner {

    @NonNull
    private Signature signature;

    @NonNull
    private String cacheDir;

    private PDFSignatureInfo signatureInfo = new PDFSignatureInfo();

    private PDFSignatureVisibility visibility = new PDFSignatureVisibility();

    public byte[] signPDF(SignPDFRequest request) throws Exception {

        initTemDir();
        String temFilePath = cacheDir + UniqueID.generate() + ".pdf";
        File file = new File(temFilePath);
        if (!file.exists())
            file.createNewFile();
        SignPDFPlugin signPDFPlugin = new SignPDFPlugin();
        signPDFPlugin.sign(request.getContent(),
            signature.getPrivateKey(),
            signature.getX509Certificates(),
            signatureInfo.build(),
            visibility.build(),
            request.getSigner(),
            signature.getHashAlgorithm(),
            request.getSignDate(),
            temFilePath);
        byte[] res = IOUtils.toByteArray(new FileInputStream(temFilePath));
        file.delete();
        return res;
    }

    private void initTemDir(){

        File file = new File(cacheDir);
        if(!file.exists())
            file.mkdir();
    }

    public PDFSignatureInfo getSignatureInfo() {
        return signatureInfo;
    }

    public PDFSignatureVisibility getVisibility() {
        return visibility;
    }

}
