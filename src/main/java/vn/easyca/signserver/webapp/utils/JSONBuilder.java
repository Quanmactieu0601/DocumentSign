package vn.easyca.signserver.webapp.utils;

import org.json.JSONObject;

public class JSONBuilder {

    private JSONObject jsonObject;

    public JSONBuilder() {
        jsonObject = new JSONObject();
    }
    public JSONBuilder put(String key,Object obj ){

        if(jsonObject.has(key))
            jsonObject.remove(key);
        jsonObject.put(key,obj);
        return this;
    }
    public JSONObject build(){

        return jsonObject;
    }
}
