package vn.easyca.signserver.webapp.service.dto;

import vn.easyca.signserver.webapp.domain.Transaction;
import vn.easyca.signserver.webapp.enm.*;

import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * A DTO for the {@link vn.easyca.signserver.webapp.domain.Transaction} entity.
 */
public class TransactionDTO implements Serializable {
    private Long id;

    private String api;

    private LocalDateTime triggerTime;

    private TransactionStatus status;

    private String message;

    private String data;

    private TransactionType type;

    private String createdBy;

    private String host;

    private Method method;

    private String fullName;

    private Action action;

    private Extension extension;

    public Extension getExtension() {
        return extension;
    }

    public void setExtension(Extension extension) {
        this.extension = extension;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getHost() {
        return host;
    }

    public Method getMethod() {
        return method;
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

    public LocalDateTime getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(LocalDateTime triggerTime) {
        this.triggerTime = triggerTime;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
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

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public TransactionDTO(Transaction transaction) {
        this.id = transaction.getId();
        this.api = transaction.getApi();
        this.status = transaction.getStatus();
        this.message = transaction.getMessage();
        this.data = transaction.getData();
        this.type = transaction.getType();
        this.triggerTime = transaction.getTriggerTime();
        this.createdBy = transaction.getCreatedBy();
        this.host = transaction.getHost();
        this.method = transaction.getMethod();
    }

    public TransactionDTO() {
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


    public TransactionDTO(Long id, String api, LocalDateTime triggerTime, TransactionStatus status, String message,
                          String data, TransactionType type,
                          Method method, String host, Action action, Extension extension, String fullName) {
        this.id = id;
        this.api = api;
        this.triggerTime = triggerTime;
        this.status = status;
        this.message = message;
        this.data = data;
        this.type = type;
        this.host = host;
        this.method = method;
        this.fullName = fullName;
        this.action = action;
        this.extension = extension;
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
            ", status='" + getStatus() + "'" +
            ", message='" + getMessage() + "'" +
            ", data='" + getData() + "'" +
            ", type=" + getType() + "'" +
            ", host=" + getHost() + "'" +
            ", method=" + getMethod() + "'" +
            ", createdBy=" + getCreatedBy() +
            ", action=" + getAction() +
            ", extension=" + getExtension() +
            "}";
    }
}
