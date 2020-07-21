package vn.easyca.signserver.webapp.web.rest.vm.response;

public class BaseResponseVM<T> {

    private int status = 0;

    private T data;

    public BaseResponseVM(int status, T data) {
        this.status = status;
        this.data = data;
    }

    public BaseResponseVM(T data) {
        this.data = data;
    }

    public BaseResponseVM() {
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

}
