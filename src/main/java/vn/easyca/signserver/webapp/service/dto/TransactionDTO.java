package vn.easyca.signserver.webapp.service.dto;

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

    private Integer type;

    
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
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
            ", type=" + getType() +
            "}";
    }
}
