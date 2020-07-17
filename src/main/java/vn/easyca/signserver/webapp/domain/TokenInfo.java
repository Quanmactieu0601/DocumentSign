package vn.easyca.signserver.webapp.domain;
import vn.easyca.signserver.webapp.utils.JSONBuilder;
import vn.easyca.signserver.webapp.utils.JsonReader;

public class TokenInfo {

    private  String name;

    private String library;

    private  int slot;

    private  String password;

    private  String data;

    private String p11Attrs;

    public static TokenInfo createInstance(String data){

        JsonReader jsonReader = new JsonReader(data);
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setName(jsonReader.tryGetString("name",""));
        tokenInfo.setLibrary(jsonReader.tryGetString("library",""));
        tokenInfo.setSlot(jsonReader.tryGetInt("slot",0));
        tokenInfo.setData(jsonReader.tryGetString("data",""));
        tokenInfo.setPassword(jsonReader.tryGetString("password",""));
        tokenInfo.setP11Attrs(jsonReader.tryGetString("p11Attrs",""));
        return tokenInfo;
    }

    public String toString(){

        JSONBuilder jsonBuilder= new JSONBuilder();
        jsonBuilder.put("name",name);
        jsonBuilder.put("library",library);
        jsonBuilder.put("slot",slot);
        jsonBuilder.put("data",data);
        jsonBuilder.put("password",password);
        jsonBuilder.put("p11Attrs",p11Attrs);
        return jsonBuilder.build().toString();
    }
    public String getName() {
        return name;
    }

    public TokenInfo setName(String name) {
        this.name = name;
        return this;
    }

    public String getLibrary() {
        return library;
    }

    public TokenInfo setLibrary(String library) {
        this.library = library;
        return this;
    }

    public int getSlot() {
        return slot;
    }

    public TokenInfo setSlot(int slot) {
        this.slot = slot;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public TokenInfo setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getData() {
        return data;
    }

    public TokenInfo setData(String data) {
        this.data = data;
        return this;
    }

    public String getP11Attrs() {
        return p11Attrs;
    }

    public TokenInfo setP11Attrs(String p11Attrs) {
        this.p11Attrs = p11Attrs;
        return this;
    }
}
