package vn.easyca.signserver.ra.lib.network;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import vn.easyca.signserver.ra.lib.exception.RAUnAuthorized;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class PostRequester {

    private final String url;
    private String idToken;

    public PostRequester(String url, String idToken) {
        this.url = url;
        this.idToken = idToken;
    }

    public PostRequester(String url) {
        this.url = url;
    }

    public <T> T post(Object data, Class<T> responseType) throws IOException, RAUnAuthorized {
        String content = post(data);
        Gson gson = new Gson();
        return gson.fromJson(content, responseType);
    }

    public <T> List<T> postToGetListData(Object data, Class<T> responseType) throws JsonSyntaxException, IOException, RAUnAuthorized {
        String content = post(data);
        ObjectMapper mapper = new ObjectMapper();
        List<T> myObjects = mapper.readValue(content, mapper.getTypeFactory().constructCollectionType(List.class, responseType));
        return myObjects;
    }

    public String post(Object data) throws IOException, RAUnAuthorized {
        Gson gson = new Gson();
        String body = (data instanceof String) ? (String) data : gson.toJson(data);
        Request request = Request.Post(url)
            .useExpectContinue()
            .bodyString(body, ContentType.APPLICATION_JSON);
        if (idToken != null)
            request.addHeader("Authorization", idToken);
        Response response = request.execute();
        try {
            return response.returnContent().asString(StandardCharsets.UTF_8);
        } catch (HttpResponseException ex) {
            if (ex.getStatusCode() == 401)
                throw new RAUnAuthorized();
            else throw ex;
        }
    }
}
