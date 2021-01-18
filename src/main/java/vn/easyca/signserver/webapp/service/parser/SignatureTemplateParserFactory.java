package vn.easyca.signserver.webapp.service.parser;

import org.springframework.stereotype.Component;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.webapp.enm.SignatureTemplateParserType;
import vn.easyca.signserver.webapp.service.impl.parser.BvAnPhuocSignatureTemplateParserImpl;
import vn.easyca.signserver.webapp.service.impl.parser.Bvq11SignatureTemplateParserImpl;
import vn.easyca.signserver.webapp.service.impl.parser.DefaultSignatureTemplateParserImpl;

@Component
public class SignatureTemplateParserFactory {
    private final Bvq11SignatureTemplateParserImpl bvq11SignatureTemplateParser;
    private final BvAnPhuocSignatureTemplateParserImpl bvAnPhuocSignatureTemplateParser;
    private final DefaultSignatureTemplateParserImpl defaultSignatureTemplateParser;

    public SignatureTemplateParserFactory(Bvq11SignatureTemplateParserImpl bvq11SignatureTemplateParser, BvAnPhuocSignatureTemplateParserImpl bvAnPhuocSignatureTemplateParser, DefaultSignatureTemplateParserImpl defaultSignatureTemplateParser) {
        this.bvq11SignatureTemplateParser = bvq11SignatureTemplateParser;
        this.bvAnPhuocSignatureTemplateParser = bvAnPhuocSignatureTemplateParser;
        this.defaultSignatureTemplateParser = defaultSignatureTemplateParser;
    }

    public SignatureTemplateParseService resolve(SignatureTemplateParserType templateParserType) throws ApplicationException {
        switch (templateParserType) {
            case BV_Q11: return bvq11SignatureTemplateParser;
            case BV_AnPhuoc: return bvAnPhuocSignatureTemplateParser;
            default: return defaultSignatureTemplateParser;
        }
    }
}
