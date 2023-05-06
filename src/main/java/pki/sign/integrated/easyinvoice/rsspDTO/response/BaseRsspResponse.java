package pki.sign.integrated.easyinvoice.rsspDTO.response;

public class BaseRsspResponse<T> {

    public static BaseRsspResponse createNewErrorResponse(String msg) {
        String[] error = msg.split(",");
        String code = error[0];
        String message = error[1];
        return new BaseRsspResponse(Integer.parseInt(code), message, null);
    }

    public static BaseRsspResponse createNewSuccessResponse(Object data) {
        return new BaseRsspResponse(0, null, data);
    }

    private int status;

    private String msg;

    private T data;

    public BaseRsspResponse(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
