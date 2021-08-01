package vn.easyca.signserver.webapp.service.impl.parser;

import org.springframework.stereotype.Service;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.webapp.service.parser.SignatureTemplateParseService;
import vn.easyca.signserver.webapp.utils.DateTimeUtils;
import vn.easyca.signserver.webapp.utils.ParserUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BvBenhNhietDoiSignatureTemplateParserImpl implements SignatureTemplateParseService {
    String regexCN = "CN=\"?([^,]+,?\\s.*)\"";

    @Override
    public String buildSignatureTemplate(String subjectDN, String signatureTemplate, String signatureImage) throws ApplicationException {
        try {
            // cho trường hợp xem trước mẫu
            if (subjectDN == null | subjectDN.equals("")) {
                subjectDN = "UID=CMND:079073009568, UID=MST:0301824642, CN=\"Nguyễn Văn A, A101.0001 - 003848/HCM-CCHN\", T=BS CK II, O=Bệnh viện Quận 11, ST=TP Hồ Chí Minh, C=VN";
            }

            String regexT = ", T=([^,]+)";
            String CN = getSigner(subjectDN);
            String T = ParserUtils.getElementContentNameInCertificate(subjectDN, regexT);
            String[] signerInfor = new String[5];
            signerInfor = getSignerInfor(CN, "([^,-]+), ([^,-]+)-(.+)");

            String signer = signerInfor[1];
            String mnv = signerInfor[2];
            String cchn = signerInfor[3];

            String htmlContent = signatureTemplate;
            htmlContent = htmlContent
                .replaceFirst("signer", signer)
                .replaceFirst("position", T)
                .replaceFirst("cchn", cchn)
                .replaceFirst("signatureImage", signatureImage)
                .replaceFirst("timeSign", DateTimeUtils.getCurrentTimeStampWithFormat(DateTimeUtils.ddMMyyyy_HHmmss));
            return htmlContent;
        } catch (Exception ex) {
            throw new ApplicationException(String.format("Error when build template: subjectDN: %s - ex: %s", subjectDN, ex.getMessage()), ex);
        }
    }

    @Override
    public String getSigner(String subjectDN) throws ApplicationException {
        return ParserUtils.getElementContentNameInCertificate(subjectDN, regexCN);
    }


    private String[] getSignerInfor(String CN, String regex) throws ApplicationException {
        try {
            final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
            final Matcher matcher = pattern.matcher(CN);
            String[] signerInfo = new String[4];
            if (matcher.matches()) {
                for (int i = 0; i <= matcher.groupCount(); i++) {
                    signerInfo[i] = matcher.group(i);
                }
            }
            return signerInfo;
        } catch (Exception ex) {
            throw new ApplicationException(String.format("Parse CN has error: [content: %s, regex: %s]", CN, regex), ex);
        }
    }
}
