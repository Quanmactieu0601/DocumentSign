package vn.easyca.signserver.webapp.service.model.pdfsigner;

import netscape.javascript.JSObject;
import org.json.JSONObject;
import vn.easyca.signserver.webapp.utils.JSONBuilder;

public class PDFSignatureVisibility {

    private JSONBuilder jsonBuilder ;

    public PDFSignatureVisibility() {

        jsonBuilder= new JSONBuilder();
        setPageNum(1);
    }

    public PDFSignatureVisibility setVisibleX(int visibleX){
        jsonBuilder.put("visibleX",visibleX);
        return this;
    }

    public PDFSignatureVisibility setVisibleY(int setVisibleY){
        jsonBuilder.put("setVisibleY",setVisibleY);
        return this;
    }

    public PDFSignatureVisibility visibleWidth(int visibleWidth){
        jsonBuilder.put("visibleWidth",visibleWidth);
        return this;
    }

    public PDFSignatureVisibility setVisibleHeight(int visibleHeight){
        jsonBuilder.put("visibleHeight",visibleHeight);
        return this;
    }

    public PDFSignatureVisibility setPageNum(int pageNum){
        jsonBuilder.put("pageNum",pageNum);
        return this;
    }

    public JSONObject build(){

        return jsonBuilder.build();
    }
}
