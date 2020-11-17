package vn.easyca.signserver.webapp.utils;

import org.modelmapper.ModelMapper;

public class MappingHelper {

    public static <S, D> D map(S s, Class<D> classType) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(s, classType);
    }
}
