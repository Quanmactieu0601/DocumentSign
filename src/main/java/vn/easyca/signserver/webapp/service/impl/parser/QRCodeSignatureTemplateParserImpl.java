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
    final String qrCodeExam = "QR Code Exam";
    final String regexCN = "CN=\"([^\"]+)\"|CN=\"([^\"]+)|CN=([^,]+)";

    @Override
    public String buildSignatureTemplate(String subjectDN, String signatureTemplate, String signatureImage, Object data) throws ApplicationException {
        try {
            if(data == null) data = this.qrCodeExam;
            String qrCodeContent =  data.toString();

            final String regexT = ", T=([^,]+)";
            String CN = getSigner(subjectDN);
            String T = ParserUtils.getElementContentNameInCertificate(subjectDN, regexT);
            String[] signerInfor = CN.split(",");
            String signerName = signerInfor[0];
            String address = signerInfor.length > 1 ? signerInfor[1] : null;
            String imageQRCode = "<img class=\"qrCode\" src=\"data:image/jpeg;base64,"+ createQrCode(qrCodeContent) +"\"/>";

            String htmlContent = signatureTemplate;
            if(T == null){
                htmlContent = htmlContent.replaceFirst("<span name=\"position\">","<span name=\"position\" hidden>");
            }
            if(address == null){
                htmlContent = htmlContent.replaceFirst("<div name=\"address\">","<div name=\"address\" hidden >");
            }
            htmlContent = htmlContent
                .replaceFirst("_signer_", signerName)
                .replaceFirst("_position_", T != null? T : "")
                .replaceFirst("_address_", address != null? address : "")
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
        return this.buildSignatureTemplate(subjectDN, signatureTemplate, signatureImage, this.qrCodeExam);
    }

    public String createQrCode(String qrCodeContent) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try{
            BitMatrix matrix = new MultiFormatWriter().encode(qrCodeContent, BarcodeFormat.QR_CODE, 80, 80);
            MatrixToImageWriter.writeToStream(matrix, "png", bos);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriterException e) {
            e.printStackTrace();
        }
        String resource = Base64.getEncoder().encodeToString(bos.toByteArray());
        return resource;
    }

}
