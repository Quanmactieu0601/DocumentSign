package vn.easyca.signserver.webapp.service.impl.parser;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.webapp.service.parser.SignatureTemplateParseService;
import vn.easyca.signserver.webapp.utils.DateTimeUtils;
import vn.easyca.signserver.webapp.utils.ParserUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
public class QRCodeSignatureTemplateParserImpl implements SignatureTemplateParseService {
    private String data;
    final String regexCN = "CN=\"([^\"]+)\"";

    @Override
    public String buildSignatureTemplate(String subjectDN, String signatureTemplate, String signatureImage) throws ApplicationException {
        try {
            final String regexT = ", T=([^,]+)";
            String CN = getSigner(subjectDN);
            String T = ParserUtils.getElementContentNameInCertificate(subjectDN, regexT);
            String[] signerInfor = CN.split(",");
            String signerName = signerInfor[0];
            String address = signerInfor[1];
            String imageQRCode = "<img class=\"qrCode\" src=\"data:image/jpeg;base64,"+ createQrCode() +"\"/>";

            String htmlContent = signatureTemplate;
            htmlContent = htmlContent
                .replaceFirst("_signer_", signerName)
                .replaceFirst("_position_", T)
                .replaceFirst("_address_", address)
                .replaceFirst("_signatureImage_", signatureImage)
                .replaceFirst("_timeSign_", DateTimeUtils.getCurrentTimeStampWithFormat(DateTimeUtils.HHmmss_ddMMyyyy))
                .replaceFirst("<img class=\"qrCode\"[^>]*>", imageQRCode);

            return htmlContent;
        } catch (Exception ex) {
            throw new ApplicationException(String.format("Error when build template: subjectDN: %s -ex: %s", subjectDN, ex.getMessage()), ex);
        }
    }

    @Override
    public String getSigner(String subjectDN) throws ApplicationException {
        return ParserUtils.getElementContentNameInCertificate(subjectDN, regexCN);
    }

    @Override
    public String previewSignatureTemplate(String signatureTemplate, String signatureImage) throws ApplicationException {
        String subjectDN = "UID=CMND:079073009568, UID=MST:0301824642, CN=\"Nguyễn Văn A, A101.0001 - 003848/HCM-CCHN\", T=BS CK II, O=Bệnh viện Quận 11, ST=TP Hồ Chí Minh, C=VN";
        this.data = "QR Code Exam";
        return this.buildSignatureTemplate(subjectDN, signatureTemplate, signatureImage);
    }

    public String createQrCode() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try{
            BitMatrix matrix = new MultiFormatWriter().encode(this.data, BarcodeFormat.QR_CODE, 80, 80);
            MatrixToImageWriter.writeToStream(matrix, "png", bos);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriterException e) {
            e.printStackTrace();
        }
        String resource = Base64.getEncoder().encodeToString(bos.toByteArray());
        return resource;
    }
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
