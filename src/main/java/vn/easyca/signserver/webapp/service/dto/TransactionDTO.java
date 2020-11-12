package vn.easyca.signserver.webapp.service.dto;

import vn.easyca.signserver.webapp.domain.Transaction;
import vn.easyca.signserver.webapp.enm.TransactionMethod;
import vn.easyca.signserver.webapp.enm.TransactionType;

import java.time.Instant;
import java.io.Serializable;

/**
 * A DTO for the {@link vn.easyca.signserver.webapp.domain.Transaction} entity.
 */
public class TransactionDTO implements Serializable {

    private Long id;

    private String api;

    private Instant triggerTime;

    private String code;

    private String message;

    private String data;

    private String type;

    private Long userID;

    private String host;

    private String method;

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Long getUserID() {
        return userID;
    }

    public String getHost() {
        return host;
    }

    public String getMethod() {
        return method;
    }

    public TransactionDTO() {
    }

    public TransactionDTO(Transaction transaction) {
        this.id = transaction.getId();
        this.api = transaction.getApi();
        this.code = transaction.getCode();
        this.message = transaction.getMessage();
        this.data = transaction.getData();
        this.type = transaction.getType();
        this.triggerTime = transaction.getTriggerTime();
        this.userID = transaction.getUserID();
        this.host = transaction.getHost();
        this.method = transaction.getMethod();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public Instant getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(Instant triggerTime) {
        this.triggerTime = triggerTime;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransactionDTO)) {
            return false;
        }

        return id != null && id.equals(((TransactionDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionDTO{" +
            "id=" + getId() +
            ", api='" + getApi() + "'" +
            ", triggerTime='" + getTriggerTime() + "'" +
            ", code='" + getCode() + "'" +
            ", message='" + getMessage() + "'" +
            ", data='" + getData() + "'" +
            ", type=" + getType() + "'" +
            ", host=" + getHost() + "'" +
            ", method=" + getMethod() + "'" +
            ", userID=" + getUserID() +
            "}";
    }

    public TransactionDTO(String api, TransactionType type , TransactionMethod method) {
        this.api = api;
        this.type = type.toString();
        this.triggerTime = Instant.now();
        this.method = method.toString();
    }

    public TransactionDTO(Long id, String api, String code, String message, String data,
                          String type, Long userID , String host, String method) {
        this.id = id;
        this.api = api;
        this.code = code;
        this.message = message;
        this.data = data;
        this.type = type;
        this.triggerTime = Instant.now();
        this.userID = userID;
        this.method = method;
        this.host = host;
    }


}
