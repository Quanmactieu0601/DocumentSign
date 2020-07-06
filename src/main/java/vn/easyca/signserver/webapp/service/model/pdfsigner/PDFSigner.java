package vn.easyca.signserver.webapp.service.model.pdfsigner;
import org.apache.commons.io.IOUtils;
import vn.easyca.signserver.core.sign.integrated.pdf.PartyMode;
import vn.easyca.signserver.core.sign.integrated.pdf.SignPDFDto;
import vn.easyca.signserver.core.sign.integrated.pdf.SignPDFPlugin;
import vn.easyca.signserver.core.sign.utils.UniqueID;
import vn.easyca.signserver.webapp.service.dto.request.SignPDFRequest;
import vn.easyca.signserver.webapp.service.model.CryptoTokenProxy;

import java.io.File;
import java.io.FileInputStream;


public class PDFSigner {

    private CryptoTokenProxy cryptoTokenProxy;

    private String cacheDir;

    public PDFSigner(CryptoTokenProxy cryptoTokenProxy, String cacheDir) {
        this.cryptoTokenProxy = cryptoTokenProxy;
        this.cacheDir = cacheDir;
    }

    public byte[] signPDF(SignPDFRequest request) throws Exception {

        initTemDir();
        String temFilePath = cacheDir + UniqueID.generate() + ".pdf";
        File file = new File(temFilePath);
        if (!file.exists())
            file.createNewFile();
        SignPDFPlugin signPDFPlugin = new SignPDFPlugin();
        SignPDFDto signPDFDto = SignPDFDto.build(
            PartyMode.SIGN_SERVER,
            request.getContent(),
            cryptoTokenProxy.getPrivateKey(),
            cryptoTokenProxy.getX509Certificates(),
            temFilePath
        );
        signPDFDto.setSignField(request.getSigner());
        signPDFDto.setSigner(request.getSigner());
        signPDFDto.setSignDate(request.getSignDate());
        signPDFDto.setLocation(request.getInfo().getLocation());
        signPDFDto.setVisibleWidth(request.getVisible().getVisibleWidth());
        signPDFDto.setVisibleHeight(request.getVisible().getVisibleHeight());
        signPDFDto.setVisibleX(request.getVisible().getVisibleX());
        signPDFDto.setVisibleY(request.getVisible().getVisibleY());
        signPDFPlugin.sign(signPDFDto);
        byte[] res = IOUtils.toByteArray(new FileInputStream(temFilePath));
        file.delete();
        return res;
    }

    private void initTemDir() {

        File file = new File(cacheDir);
        if (!file.exists())
            file.mkdir();
    }
}
