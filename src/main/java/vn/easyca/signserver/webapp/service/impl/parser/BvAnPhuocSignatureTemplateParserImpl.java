package vn.easyca.signserver.webapp.service.impl.parser;

import org.springframework.stereotype.Service;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.webapp.service.parser.SignatureTemplateParseService;
import vn.easyca.signserver.webapp.utils.DateTimeUtils;
import vn.easyca.signserver.webapp.utils.ParserUtils;

@Service
public class BvAnPhuocSignatureTemplateParserImpl implements SignatureTemplateParseService {
    String regexCN = "CN=([^\",]+)";

    @Override
    public String buildSignatureTemplate(String subjectDN, String signatureTemplate, String signatureImage,String qrCode) throws ApplicationException {
        try {
            String regexT = ", T=([^,]+)";
            String CN = getSigner(subjectDN);
            String T = ParserUtils.getElementContentNameInCertificate(subjectDN, regexT);
            String[] signerInfor = CN.split(",");
            String signerName = signerInfor[0];

            String htmlContent = signatureTemplate;
            htmlContent = htmlContent
                .replaceFirst("signer", signerName)
                .replaceFirst("position", T)
                .replaceFirst("signatureImage", signatureImage)
                .replaceFirst("timeSign", DateTimeUtils.getCurrentTimeStampWithFormat(DateTimeUtils.HHmmss_ddMMyyyy));
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
        String subjectDN = "UID=CMND:079073009568, UID=MST:0301824642, CN=Nguyễn Văn A, A101.0001 - 003848/HCM-CCHN\", T=BS CK II, O=Bệnh viện Quận 11, ST=TP Hồ Chí Minh, C=VN";
        return this.buildSignatureTemplate(subjectDN, signatureTemplate, signatureImage,null);
    }
}
