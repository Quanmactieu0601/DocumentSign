package vn.easyca.signserver.webapp.service.parser;

import org.springframework.stereotype.Component;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.webapp.enm.SignatureTemplateParserType;
import vn.easyca.signserver.webapp.service.impl.parser.*;

@Component
public class SignatureTemplateParserFactory {
    private final Bvq11SignatureTemplateParserImpl bvq11SignatureTemplateParser;
    private final Bvq11v2SignatureTemplateParserImpl bvq11v2SignatureTemplateParser;
    private final BvAnPhuocSignatureTemplateParserImpl bvAnPhuocSignatureTemplateParser;
    private final BvBenhNhietDoiSignatureTemplateParserImpl bvBenhNhietDoiSignatureTemplateParser;
    private final DefaultSignatureTemplateParserImpl defaultSignatureTemplateParser;
    private final FullInfoSignatureTemplateParserImpl fullInfoSignatureTemplateParser;

    public SignatureTemplateParserFactory(Bvq11SignatureTemplateParserImpl bvq11SignatureTemplateParser, Bvq11v2SignatureTemplateParserImpl bvq11v2SignatureTemplateParser , BvAnPhuocSignatureTemplateParserImpl bvAnPhuocSignatureTemplateParser, BvBenhNhietDoiSignatureTemplateParserImpl bvNhietDoiSignatureTemplateParser, DefaultSignatureTemplateParserImpl defaultSignatureTemplateParser, FullInfoSignatureTemplateParserImpl fullInfoSignatureTemplate) {
        this.bvq11SignatureTemplateParser = bvq11SignatureTemplateParser;
        this.bvq11v2SignatureTemplateParser = bvq11v2SignatureTemplateParser;
        this.bvAnPhuocSignatureTemplateParser = bvAnPhuocSignatureTemplateParser;
        this.bvBenhNhietDoiSignatureTemplateParser = bvNhietDoiSignatureTemplateParser;
        this.defaultSignatureTemplateParser = defaultSignatureTemplateParser;
        this.fullInfoSignatureTemplateParser = fullInfoSignatureTemplate;
    }

    public SignatureTemplateParseService resolve(SignatureTemplateParserType templateParserType) throws ApplicationException {
        switch (templateParserType) {
            case BV_Q11: return bvq11SignatureTemplateParser;
            case BV_Q11_V2: return bvq11v2SignatureTemplateParser;
            case BV_AnPhuoc: return bvAnPhuocSignatureTemplateParser;
            case BV_BenhNhietDoi: return bvBenhNhietDoiSignatureTemplateParser;
            case Full_Info_Version: return fullInfoSignatureTemplateParser;
            default: return defaultSignatureTemplateParser;
        }
    }
}
