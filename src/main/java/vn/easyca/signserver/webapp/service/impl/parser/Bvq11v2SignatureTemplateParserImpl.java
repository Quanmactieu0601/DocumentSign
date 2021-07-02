package vn.easyca.signserver.webapp.service.impl.parser;

import org.springframework.stereotype.Service;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.webapp.service.parser.SignatureTemplateParseService;
import vn.easyca.signserver.webapp.utils.DateTimeUtils;
import vn.easyca.signserver.webapp.utils.ParserUtils;

@Service
public class Bvq11v2SignatureTemplateParserImpl implements SignatureTemplateParseService {
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

            String htmlContent = signatureTemplate;
            htmlContent = htmlContent
                .replaceFirst("_signer_", signerName)
                .replaceFirst("_position_", T)
                .replaceFirst("_address_", address)
                .replaceFirst("_signatureImage_", signatureImage)
                .replaceFirst("_timeSign_", DateTimeUtils.getCurrentTimeStampWithFormat(DateTimeUtils.HHmmss_ddMMyyyy));
            return htmlContent;
        } catch (Exception ex) {
            throw new ApplicationException(String.format("Error when build template: subjectDN: %s -ex: %s", subjectDN, ex.getMessage()), ex);
        }
    }

    @Override
    public String getSigner(String subjectDN) throws ApplicationException {
        return ParserUtils.getElementContentNameInCertificate(subjectDN, regexCN);
    }

}
