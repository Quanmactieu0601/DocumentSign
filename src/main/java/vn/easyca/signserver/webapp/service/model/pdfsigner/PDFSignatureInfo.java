package vn.easyca.signserver.webapp.service.model.pdfsigner;

import lombok.Getter;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PDFSignatureInfo {


    private JSONObject jsonObject;

    @Getter
    private String reason;

    @Getter
    private  String location;

    @Getter
    private  String signer;

    @Getter
    private  String signDate;

    @Getter
    private int pageNum;

    public PDFSignatureInfo() {

        jsonObject = new JSONObject();
        setSigner("");
        setSignDate(new Date(),"yyyy-mm-dd hh:mm:ss");
        setPageNum(1);
    }

    public PDFSignatureInfo setReason(String reason){

        this.reason = reason;
        jsonObject.put("reason",reason);
        return this;
    }

    public PDFSignatureInfo setLocation(String location){

        this.location = location;
        jsonObject.put("location",location);
        return this;
    }

    public PDFSignatureInfo setSigner(String signer){

        this.signer = signer;
        jsonObject.put("signerLabel",signer);
        return this;
    }

    public PDFSignatureInfo setSignDate(Date date,String format){

        DateFormat dateFormat = new SimpleDateFormat(format);
        String strDate = dateFormat.format(date);
        jsonObject.put("signDateLabel",strDate);
        return this;
    }
    public PDFSignatureInfo setSignDate(Date date){

       return setSignDate(date,"yyyy-mm-dd hh:mm:ss");
    }

    public PDFSignatureInfo setPageNum(int pageNum){

        jsonObject.put("pageNum",pageNum);
        return this;
    }

    public JSONObject build(){

        return jsonObject;
    }






}
