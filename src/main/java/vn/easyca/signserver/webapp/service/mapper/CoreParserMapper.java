package vn.easyca.signserver.webapp.service.mapper;


import vn.easyca.signserver.webapp.domain.*;
import vn.easyca.signserver.webapp.service.dto.CoreParserDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link CoreParser} and its DTO {@link CoreParserDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface CoreParserMapper extends EntityMapper<CoreParserDTO, CoreParser> {



    default CoreParser fromId(Long id) {
        if (id == null) {
            return null;
        }
        CoreParser coreParser = new CoreParser();
        coreParser.setId(id);
        return coreParser;
    }
}
