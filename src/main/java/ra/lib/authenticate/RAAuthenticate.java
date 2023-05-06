package ra.lib.authenticate;

import java.io.IOException;
import org.json.JSONObject;
import ra.lib.exception.RAUnAuthorized;
import ra.lib.network.PostRequester;

public class RAAuthenticate {

    public RAAuthenticate(String url, String userName, String password) {
        this.url = url;
        this.userName = userName;
        this.password = password;
    }

    private final String url;
    private final String userName;
    private final String password;
    private String currentToken;

    public String getToken() throws RAUnAuthorized, IOException {
        if (currentToken == null) currentToken = getNewToken();
        return currentToken;
    }

    public String getNewToken() throws IOException, RAUnAuthorized {
        String requestBody = createRequestBody();
        PostRequester postRequester = new PostRequester(url);
        String res = postRequester.post(requestBody);
        return "Bearer " + parse(res);
    }

    private String createRequestBody() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", userName);
        jsonObject.put("password", password);
        return jsonObject.toString();
    }

    private String parse(String response) throws RAUnAuthorized {
        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.has("id_token")) return jsonObject.getString("id_token");
        throw new RAUnAuthorized();
    }
}
