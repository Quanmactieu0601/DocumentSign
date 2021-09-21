package vn.easyca.signserver.webapp.service.parser;

import liquibase.pro.packaged.T;
import vn.easyca.signserver.core.exception.ApplicationException;

public interface SignatureTemplateParseService {
    String buildSignatureTemplate(String subjectDN, String signatureTemplate, String signatureImage, Object data) throws ApplicationException;
    String getSigner(String subjectDN) throws ApplicationException;
    String previewSignatureTemplate(String signatureTemplate, String signatureImage) throws ApplicationException;
}
