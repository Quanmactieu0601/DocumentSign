package vn.easyca.signserver.webapp.service.model.pdfsigner;


import org.json.JSONObject;
import vn.easyca.signserver.webapp.utils.JSONBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PDFSignatureInfo {


    private JSONBuilder jsonBuilder;

    public PDFSignatureInfo() {

        jsonBuilder = new JSONBuilder();
        setSigner("");
        setSignDate(new Date(),"yyyy-mm-dd hh:mm:ss");
        setPageNum(1);
    }

    public PDFSignatureInfo setReason(String reason){

        jsonBuilder.put("reason",reason);
        return this;
    }

    public PDFSignatureInfo setLocation(String location){

        jsonBuilder.put("location",location);
        return this;
    }

    public PDFSignatureInfo setSigner(String signer){

        jsonBuilder.put("signerLabel",signer);
        return this;
    }

    public PDFSignatureInfo setSignDate(Date date,String format){

        DateFormat dateFormat = new SimpleDateFormat(format);
        String strDate = dateFormat.format(date);
        jsonBuilder.put("signDateLabel",strDate);
        return this;
    }
    public PDFSignatureInfo setSignDate(Date date){

       return setSignDate(date,"yyyy-mm-dd hh:mm:ss");
    }

    public PDFSignatureInfo setPageNum(int pageNum){

        jsonBuilder.put("pageNum",pageNum);
        return this;
    }

    public JSONObject build(){

        return jsonBuilder.build();
    }






}
