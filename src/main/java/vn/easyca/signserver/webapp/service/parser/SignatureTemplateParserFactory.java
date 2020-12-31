package vn.easyca.signserver.webapp.service.parser;

import org.springframework.stereotype.Component;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.webapp.enm.SignatureTemplateParserType;
import vn.easyca.signserver.webapp.service.impl.parser.BvAnPhuocSignatureTemplateParserImpl;
import vn.easyca.signserver.webapp.service.impl.parser.Bvq11SignatureTemplateParserImpl;

@Component
public class TemplateParserFactory {
    private final Bvq11SignatureTemplateParserImpl bvq11SignatureTemplateParser;
    private final BvAnPhuocSignatureTemplateParserImpl bvAnPhuocSignatureTemplateParser;

    public TemplateParserFactory(Bvq11SignatureTemplateParserImpl bvq11SignatureTemplateParser, BvAnPhuocSignatureTemplateParserImpl bvAnPhuocSignatureTemplateParser) {
        this.bvq11SignatureTemplateParser = bvq11SignatureTemplateParser;
        this.bvAnPhuocSignatureTemplateParser = bvAnPhuocSignatureTemplateParser;
    }

    public SignatureTemplateParseService resolveTemplateParserService(SignatureTemplateParserType templateParserType) throws ApplicationException {
        switch (templateParserType) {
            case BV_Q11: return bvq11SignatureTemplateParser;
            case BV_AnPhuoc: return bvAnPhuocSignatureTemplateParser;
            default: throw new ApplicationException("Don't have any signature template parser");
        }
    }
}
