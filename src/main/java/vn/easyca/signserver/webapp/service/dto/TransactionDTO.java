package vn.easyca.signserver.webapp.service.dto;

import vn.easyca.signserver.webapp.domain.Transaction;

import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * A DTO for the {@link vn.easyca.signserver.webapp.domain.Transaction} entity.
 */
public class TransactionDTO implements Serializable {
    private Long id;

    private String api;

    private LocalDateTime triggerTime;

    private boolean status;

    private String message;

    private String data;

    private String type;

    private String createdBy;

    private String host;

    private String method;

    private String fullName;

    private String action;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
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

    public void setMethod(String method) {
        this.method = method;
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
        this.status = transaction.getStatus();
        this.message = transaction.getMessage();
        this.data = transaction.getData();
        this.type = transaction.getType();
        this.triggerTime = transaction.getTriggerTime();
        this.createdBy = transaction.getCreatedBy();
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

    public LocalDateTime getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(LocalDateTime triggerTime) {
        this.triggerTime = triggerTime;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
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
            ", code='" + getStatus() + "'" +
            ", message='" + getMessage() + "'" +
            ", data='" + getData() + "'" +
            ", type=" + getType() + "'" +
            ", host=" + getHost() + "'" +
            ", method=" + getMethod() + "'" +
            ", createdBy=" + getCreatedBy() +
            ", createdBy=" + getAction() +
            "}";
    }

}
