package vn.easyca.signserver.webapp.service.impl.parser;

import org.springframework.stereotype.Service;
import vn.easyca.signserver.webapp.service.parser.SignatureTemplateParseService;
import vn.easyca.signserver.webapp.utils.DateTimeUtils;
import vn.easyca.signserver.webapp.utils.ParserUtils;

@Service
public class BvAnPhuocSignatureTemplateParserImpl implements SignatureTemplateParseService {

    @Override
    public String buildSignatureTemplate(String subjectDN, String signatureTemplate, String signatureImage) {
        final String regexCN = "CN=([^\",]+)";
        final String regexT = ", T=([^,]+)";
        String CN = ParserUtils.getElementContentNameInCertificate(subjectDN, regexCN);
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
    }
}
