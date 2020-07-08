package vn.easyca.signserver.webapp.utils;

import org.json.JSONObject;

public class JsonReader {

    private JSONObject jsonObject;

    public JsonReader(String data) {

        jsonObject = new JSONObject(data);
    }

    public String tryGetString(String key, String defaultVal) {

        return jsonObject.has(key) ? jsonObject.getString(key) : defaultVal;
    }

    public int tryGetInt(String key, int defaultVal) {

        return jsonObject.has(key) ? jsonObject.getInt(key) : defaultVal;
    }
}
