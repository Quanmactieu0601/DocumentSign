package vn.easyca.signserver.webapp.service.impl.parser;

import org.springframework.stereotype.Service;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.webapp.service.parser.SignatureTemplateParseService;
import vn.easyca.signserver.webapp.utils.DateTimeUtils;
import vn.easyca.signserver.webapp.utils.ParserUtils;

@Service
public class QRCodeSignatureTemplateParserImpl implements SignatureTemplateParseService {
    final String regexCN = "CN=\"([^\"]+)\"";
    final String qrCodeExam = "iVBORw0KGgoAAAANSUhEUgAAAMgAAADIAQAAAACFI5MzAAAA30lEQVR42u2XTQ6EIAyF64pjcNMBb8oxWJXpD0QdmHVrQmMI8m0q5T0qtH8Bm2yyyTtIBYqDx5h5Gj0Rnp418jNe/ZAMvCyc0ndIAIJXIhOHRPfyQ1kvz4ElUZVwwdf6MSQ9Sl+bfceQVEhU5FZoRzGU5/eYEz6JNCJ0m3FFkFXCWXO1Q/NFQhGJlEO04ovAZX4I0RHRGLZ3r7Y9UXcZcnnsqD3prkzaRYg4dyimRLuATCoJs7YdkNbUYzK4JJLyeb8xHBCtdiJ3+bnN7Env1pMcxkWHYkj2/9wmm7yUfAHOlFqs5bXRKAAAAABJRU5ErkJggg==" ;
    @Override
    public String buildSignatureTemplate(String subjectDN, String signatureTemplate, String signatureImage,String qrCode) throws ApplicationException {
        try {
            final String regexT = ", T=([^,]+)";
            String CN = getSigner(subjectDN);
            String T = ParserUtils.getElementContentNameInCertificate(subjectDN, regexT);
            String[] signerInfor = CN.split(",");
            String signerName = signerInfor[0];
            String address = signerInfor[1];
            String imageQRCode = "<img class=\"qrCode\" src=\"data:image/jpeg;base64,"+qrCode +"\"/>";

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
        return this.buildSignatureTemplate(subjectDN, signatureTemplate, signatureImage,qrCodeExam);
    }
}
