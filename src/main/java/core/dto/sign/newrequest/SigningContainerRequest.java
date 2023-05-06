package core.dto.sign.newrequest;

public class SigningContainerRequest<T1, T2> {

    private T1 request;
    private T2 type;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public T1 getRequest() {
        return request;
    }

    public void setRequest(T1 request) {
        this.request = request;
    }

    public T2 getType() {
        return type;
    }

    public void setType(T2 type) {
        this.type = type;
    }
}
