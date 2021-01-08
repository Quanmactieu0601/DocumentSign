package vn.easyca.signserver.webapp.web.rest.vm.response;

import vn.easyca.signserver.core.exception.ApplicationException;

public class BaseResponseVM {

    public static final int STATUS_OK = 0, STATUS_ERROR = -1;

    public static BaseResponseVM createNewErrorResponse(String msg) {
        return new BaseResponseVM(STATUS_ERROR, null, msg);
    }

    public static BaseResponseVM createNewErrorResponse(ApplicationException ex) {
        return new BaseResponseVM(ex.getCode(), null, ex.getMessage());
    }

    public static BaseResponseVM createNewSuccessResponse(Object data) {
        return new BaseResponseVM(STATUS_OK, data, null);
    }

    public static BaseResponseVM createNewSuccessResponse() {
        return new BaseResponseVM(STATUS_OK, null, null);
    }

    public static BaseResponseVM createNewSuccessResponseWithMsg(String msg) {
        return new BaseResponseVM(STATUS_OK, null, msg);
    }

    private int status;

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
