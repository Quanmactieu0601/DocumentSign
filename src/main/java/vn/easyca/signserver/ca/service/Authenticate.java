/* created by truonglx  on 7/16/20 */
package vn.easyca.signserver.ca.service;

import org.json.JSONObject;
import vn.easyca.signserver.ca.service.network.PostRequester;
import vn.easyca.signserver.ca.service.network.RAUnauthorized;

import java.io.IOException;

public class Authenticate {

    public Authenticate(String url, String userName, String password) {
        this.url = url;
        this.userName = userName;
        this.password = password;
    }

    private String url;
    private String userName;
    private String password;
    private String currentToken;

    public String getToken() throws RAUnauthorized, IOException {
        if (currentToken == null)
            currentToken = getNewToken();
        return currentToken;
    }

    public String getNewToken() throws IOException, RAUnauthorized {
        String body = getBody();
        PostRequester postRequester = new PostRequester(url);
        String res = postRequester.post(body);
        return "Bearer " + parse(res);
    }

    private String getBody() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", userName);
        jsonObject.put("password", password);
        return jsonObject.toString();
    }

    private String parse(String response) throws RAUnauthorized {
        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.has("id_token"))
            return jsonObject.getString("id_token");
        throw new RAUnauthorized();
    }

    public static void main(String[] args) throws IOException, RAUnauthorized {
        String token = new Authenticate("http://172.16.10.66:8787/api/authenticate", "admin1", "admin").getToken();
        System.out.println(token);
    }
}
