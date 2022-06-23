package vn.easyca.signserver.webapp.service.impl.parser;

import org.springframework.stereotype.Service;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.webapp.service.parser.SignatureTemplateParseService;
import vn.easyca.signserver.webapp.utils.DateTimeUtils;
import vn.easyca.signserver.webapp.utils.ParserUtils;

@Service
public class DefaultSignatureTemplateParserImpl implements SignatureTemplateParseService {
    final String regexCN = "CN=\"([^\"]+)|CN=([^,]+)";

    @Override
    public String buildSignatureTemplate(String subjectDN, String signatureTemplate, String signatureImage, Object data) throws ApplicationException {
        try {
            String CN = ParserUtils.getElementContentNameInCertificate(subjectDN, regexCN);
            String htmlContent = signatureTemplate;
            htmlContent = htmlContent
                .replaceFirst("_signer_", CN)
                .replaceFirst("_signatureImage_", signatureImage)
                .replaceFirst("_timeSign_", DateTimeUtils.getCurrentTimeStampWithFormat(DateTimeUtils.HHmmss_ddMMyyyy))
                .replaceFirst("_shortTime_", DateTimeUtils.getCurrentTimeStampWithFormat(DateTimeUtils.DEFAULT_FORMAT))
                .replaceFirst("_vnDate_", DateTimeUtils.getCurrentTimeStampWithFormat(DateTimeUtils.ddMMyyyy))
                .replaceFirst("_hour_", DateTimeUtils.getCurrentTimeStampWithFormat(DateTimeUtils.HHmmss))
                .replaceFirst("_timeZone_",  DateTimeUtils.timeZone);
            return htmlContent;
        } catch (Exception ex) {
            throw new ApplicationException(String.format("Error when build template: subjectDN: %s - ex: %s", subjectDN, ex.getMessage()), ex);
        }
    }

    @Override
    public String getSigner(String subjectDN) throws ApplicationException {
        return ParserUtils.getElementContentNameInCertificate(subjectDN, regexCN);
    }

    @Override
    public String previewSignatureTemplate(String signatureTemplate, String signatureImage) throws ApplicationException {
        String subjectDN = "UID=MST:0500329988, CN=Nguyễn Văn A, OU=IT, O=QUỸ TÍN DỤNG NHÂN DÂN VẠN PHÚC, ST=Hà Nội, C=VN";
        return this.buildSignatureTemplate(subjectDN, signatureTemplate, signatureImage, null);
    }
}
