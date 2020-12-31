package vn.easyca.signserver.webapp.service.parser;

public interface SignatureTemplateParseService {
    String buildSignatureTemplate(String subjectDN, String signatureTemplate, String signatureImage);
}
