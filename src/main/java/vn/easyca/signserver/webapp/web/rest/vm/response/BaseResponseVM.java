package vn.easyca.signserver.webapp.web.rest.vm.response;

import vn.easyca.signserver.application.exception.ApplicationException;

public class BaseResponseVM {

    public static final int STATUS_OK = 0, STATUS_ERROR = -1;

    public static BaseResponseVM CreateNewErrorResponse(String msg) {
        return new BaseResponseVM(STATUS_ERROR, null, msg);
    }

    public static BaseResponseVM CreateNewErrorResponse(ApplicationException ex) {
        return new BaseResponseVM(ex.getCode(), null, ex.getMessage());
    }

    public static BaseResponseVM CreateNewSuccessResponse(Object data) {
        return new BaseResponseVM(STATUS_OK, data, null);
    }

    private int status = 0;

    private String msg;

    private Object data;

    public BaseResponseVM(int status, Object data, String msg) {
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
