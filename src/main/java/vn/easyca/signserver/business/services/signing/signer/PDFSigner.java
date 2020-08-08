package vn.easyca.signserver.business.services.signing.signer;

import org.apache.commons.io.IOUtils;
import vn.easyca.signserver.business.services.signing.dto.request.SignElement;
import vn.easyca.signserver.pki.sign.integrated.pdf.PartyMode;
import vn.easyca.signserver.pki.sign.integrated.pdf.SignPDFDto;
import vn.easyca.signserver.pki.sign.integrated.pdf.SignPDFPlugin;
import vn.easyca.signserver.pki.sign.utils.UniqueID;
import vn.easyca.signserver.business.services.signing.dto.request.SignRequest;
import vn.easyca.signserver.business.services.signing.dto.request.content.PDFSignContent;

import java.io.File;
import java.io.FileInputStream;
import java.security.cert.Certificate;
import java.util.Map;


public class PDFSigner {

    private final CryptoTokenProxy cryptoTokenProxy;

    private final String cacheDir;

    public PDFSigner(CryptoTokenProxy cryptoTokenProxy, String cacheDir) {
        this.cryptoTokenProxy = cryptoTokenProxy;
        this.cacheDir = cacheDir;
    }

    public byte[] signPDF(SignRequest<PDFSignContent> request) throws Exception {
        initTemDir();
        String temFilePath = cacheDir + UniqueID.generate() + ".pdf";
        File file = new File(temFilePath);
        SignElement<PDFSignContent> element = request.getSignElements().get(0);
        if (element == null)
            throw  new Exception("have not element to sign");
        PDFSignContent content = element.getContent();
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
            signPDFDto.setSignField(element.getSigner());
            signPDFDto.setSigner(element.getSigner());
            signPDFDto.setSignDate(element.getSignDate());
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
