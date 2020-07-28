package vn.easyca.signserver.webapp.web.rest.vm;

public class BaseResponseVM {

    public static final int STATUS_OK = 0,STATUS_ERROR=-1;

    public static  BaseResponseVM CreateNewErrorResponse(String msg){
        BaseResponseVM baseResponseVM = new BaseResponseVM(STATUS_ERROR,null,msg);
        return baseResponseVM;
    }
    public static BaseResponseVM CreateNewSuccessResponse(Object data){
        BaseResponseVM baseResponseVM = new BaseResponseVM(STATUS_OK,data,null);
        return baseResponseVM;
    }

    private int status = 0;

    private String msg;

    private Object data;

    public BaseResponseVM(int status,Object data,String msg) {
        this.status = status;
        this.data = data;
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
