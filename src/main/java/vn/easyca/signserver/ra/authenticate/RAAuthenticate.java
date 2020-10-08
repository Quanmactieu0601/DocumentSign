/* created by truonglx  on 7/16/20 */
package vn.easyca.signserver.ra.authenticate;

import org.json.JSONObject;
import vn.easyca.signserver.ra.network.PostRequester;
import vn.easyca.signserver.ra.exception.RAUnAuthorized;

import java.io.IOException;

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
        if (currentToken == null)
            currentToken = getNewToken();
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
        if (jsonObject.has("id_token"))
            return jsonObject.getString("id_token");
        throw new RAUnAuthorized();
    }
}
