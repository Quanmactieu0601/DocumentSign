package vn.easyca.signserver.business.services.signing.signer;

import org.apache.commons.io.IOUtils;
import vn.easyca.signserver.pki.sign.integrated.pdf.PartyMode;
import vn.easyca.signserver.pki.sign.integrated.pdf.SignPDFDto;
import vn.easyca.signserver.pki.sign.integrated.pdf.SignPDFPlugin;
import vn.easyca.signserver.pki.sign.utils.UniqueID;
import vn.easyca.signserver.business.services.signing.dto.request.SigningRequest;
import vn.easyca.signserver.business.services.signing.dto.request.content.PDFSigningContent;

import java.io.File;
import java.io.FileInputStream;
import java.security.cert.Certificate;


public class PDFSigner {

    private final CryptoTokenProxy cryptoTokenProxy;

    private final String cacheDir;

    public PDFSigner(CryptoTokenProxy cryptoTokenProxy, String cacheDir) {
        this.cryptoTokenProxy = cryptoTokenProxy;
        this.cacheDir = cacheDir;
    }

    public byte[] signPDF(SigningRequest<PDFSigningContent> request) throws Exception {
        initTemDir();
        String temFilePath = cacheDir + UniqueID.generate() + ".pdf";
        File file = new File(temFilePath);
        PDFSigningContent content = request.getContent();
        file.createNewFile();
        try {
            SignPDFPlugin signPDFPlugin = new SignPDFPlugin();
            SignPDFDto signPDFDto = SignPDFDto.build(
                PartyMode.SIGN_SERVER,
                content.getFileData(),
                cryptoTokenProxy.getPrivateKey(),
                new Certificate[]{cryptoTokenProxy.getX509Certificate()},
                temFilePath
            );
            signPDFDto.setSignField(request.getSigner());
            signPDFDto.setSigner(request.getSigner());
            signPDFDto.setSignDate(request.getSignDate());
            signPDFDto.setLocation(content.getInfo().getLocation());
            signPDFDto.setVisibleWidth(content.getVisible().getVisibleWidth());
            signPDFDto.setVisibleHeight(content.getVisible().getVisibleHeight());
            signPDFDto.setVisibleX(content.getVisible().getVisibleX());
            signPDFDto.setVisibleY(content.getVisible().getVisibleY());
            signPDFPlugin.sign(signPDFDto);
            byte[] res = IOUtils.toByteArray(new FileInputStream(temFilePath));
            file.delete();
            return res;
        } catch (Exception ex) {
            if (file != null && file.exists())
                file.delete();
            throw ex;
        }
    }

    private void initTemDir() {
        File file = new File(cacheDir);
        if (!file.exists())
            file.mkdir();
    }
}
