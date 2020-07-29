package vn.easyca.signserver.sign.core.sign.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * Created by chen on 7/21/17.
 */
public class JsonUtils {
    public static String writeObjectToJsonString(Object object) throws Exception {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(object);
    }
}
