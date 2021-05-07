package vn.easyca.signserver.webapp.service.impl.parser;

import org.springframework.stereotype.Service;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.webapp.service.parser.SignatureTemplateParseService;
import vn.easyca.signserver.webapp.utils.DateTimeUtils;
import vn.easyca.signserver.webapp.utils.ParserUtils;

@Service
public class BvBenhNhietDoiSignatureTemplateParserImpl implements SignatureTemplateParseService {
    String regexCN = "CN=\"?([^,]+,?\\s.*)\"";

    @Override
    public String buildSignatureTemplate(String subjectDN, String signatureTemplate, String signatureImage) throws ApplicationException {
        try {
            String regexT = ", T=([^,]+)";
            String CN = getSigner(subjectDN);
            String T = ParserUtils.getElementContentNameInCertificate(subjectDN, regexT);
            String[] signerInfor = CN.split(",");
            String signerName = signerInfor[0];
            String cchn = "";

            if (signerInfor[1] != null) {
                cchn = signerInfor[1];
            } else {
                signatureTemplate.replaceFirst("cchn", "");
            }

            String htmlContent = signatureTemplate;
            htmlContent = htmlContent
                .replaceFirst("signer", signerName)
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

}
