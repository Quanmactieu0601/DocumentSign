package vn.easyca.signserver.webapp.service.impl.parser;

import org.springframework.stereotype.Service;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.webapp.service.parser.SignatureTemplateParseService;
import vn.easyca.signserver.webapp.utils.DateTimeUtils;
import vn.easyca.signserver.webapp.utils.ParserUtils;

@Service
public class FullInfoSignatureTemplateParserImpl implements SignatureTemplateParseService {

    final String regexCN = "CN=\"([^\"]+)|CN=([^,]+)";
    final String regexTitle = ", ?T=([^,]+)";
    final String regexEmail = ", ?(?:E|Email|EmailAddress)\\s*=\\s*[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+";
    final String regexOrg = ", ?O= ?([^,]+)";
    final String regexOrgU = ", ?OU= ?([^,]+)";
    final String regexAddress = ", ?ST= ?([^,]+)";
    final String regexPhoneNumber = ", ?TelephoneNumber= ?([^,]+)";

    final
    @Override
    public String buildSignatureTemplate(String subjectDN, String signatureTemplate, String signatureImage) throws ApplicationException {
        try {
            String CN = getSigner(subjectDN);
            String T = ParserUtils.getElementContentInCertificate(subjectDN, regexTitle);
            String E = ParserUtils.getElementContentInCertificate(subjectDN, regexEmail);
            String O = ParserUtils.getElementContentInCertificate(subjectDN, regexOrg);
            String OU = ParserUtils.getElementContentInCertificate(subjectDN, regexOrgU);
            String add = ParserUtils.getElementContentInCertificate(subjectDN, regexAddress);
            String phoneNumber = ParserUtils.getElementContentInCertificate(subjectDN, regexPhoneNumber);
            String[] signerInfor = CN.split(",");
            String signerName = signerInfor[0];
            String htmlContent = signatureTemplate;
            htmlContent = htmlContent
                .replaceFirst("_signer_", signerName == null ? "" : signerName)
                .replaceFirst("_position_", T == null ? "" : T)
                .replaceFirst("_email_", E == null ? "" : E)
                .replaceFirst("_address_", add == null ? "" : add)
                .replaceFirst("_org_", O == null ? "" : O)
                .replaceFirst("_orgU_", OU == null ? "" : OU)
                .replaceFirst("_phoneNumber_", phoneNumber == null ? "" : phoneNumber)
                .replaceFirst("_signatureImage_", signatureImage == null ? "" : signatureImage)
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

    @Override
    public String previewSignatureTemplate(String signatureTemplate, String signatureImage) throws ApplicationException {
        String subjectDN = "UID=MST:0500329988, CN=Nguyễn Văn A, OU=IT, TelephoneNumber=1234567890, T=GD., O=QUỸ TÍN DỤNG NHÂN DÂN VẠN PHÚC, ST=Hà Nội, C=VN, E=lechitainkv@gmail.com";
        return this.buildSignatureTemplate(subjectDN, signatureTemplate, signatureImage);
    }
}
