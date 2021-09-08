package vn.easyca.signserver.core.dto.sign.newrequest;

public class SigningContainerRequest<T1, T2> {
    private T1 request;
    private T2 type;
    public T1 getRequest() { return request; }

    public void setRequest(T1 request) { this.request = request; }

    public T2 getType() { return type; }

    public void setType(T2 type) { this.type = type; }

}
