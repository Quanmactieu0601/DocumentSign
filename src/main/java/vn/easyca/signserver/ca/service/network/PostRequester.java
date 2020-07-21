package vn.easyca.signserver.ca.service.network; /* created by truonglx  on 7/16/20 */

import com.google.gson.Gson;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;

import java.io.IOException;

public class PostRequester {

    private String url;
    private String idToken;

    public PostRequester(String url, String idToken) {
        this.url = url;
        this.idToken = idToken;
    }

    public PostRequester(String url) {
        this.url = url;
    }

    public <Res> Res post(Object data, Class<Res> responseType) throws IOException, RAUnauthorized {
        String content = post(data);
        Gson gson = new Gson();
        return gson.fromJson(content, responseType);
    }

    public String post(Object data) throws IOException, RAUnauthorized {
        Gson gson = new Gson();
        String body = (data instanceof String) ? (String) data : gson.toJson(data);
        Request request = Request.Post(url)
            .useExpectContinue()
            .bodyString(body, ContentType.APPLICATION_JSON);
        if (idToken != null)
            request.addHeader("Authorization", idToken);
        Response response = request.execute();
        try {
             return response.returnContent().asString();
        } catch (HttpResponseException ex) {
            if (ex.getStatusCode() == 401)
                throw new RAUnauthorized();
            else throw ex;
        }
    }
}
