package vn.easyca.signserver.webapp.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import vn.easyca.signserver.webapp.enm.*;

import javax.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A Transaction.
 */
@Entity
@Table(name = "transaction_log")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "api")
    private String api;

    @Column(name = "trigger_time")
    private LocalDateTime triggerTime;

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private TransactionStatus status;

    @Column(name = "message")
    private String message;

    @Column(name = "data")
    private String data;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "host")
    private String host;

    @Column(name = "method")
    @Enumerated(EnumType.STRING)
    private Method method;

    @Column(name = "action")
    @Enumerated(EnumType.STRING)
    private Action action;

    @Column(name = "extension")
    @Enumerated(EnumType.STRING)
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

    public Transaction action(Action action) {
        this.action = action;
        return this;
    }

    public void setAction(Action action) {
        this.action = action;
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

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApi() {
        return api;
    }

    public Transaction api(String api) {
            this.api = api;
            return this;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public LocalDateTime getTriggerTime() {
        return triggerTime;
    }

    public Transaction triggerTime(LocalDateTime triggerTime) {
        this.triggerTime = triggerTime;
        return this;
    }

    public void setTriggerTime(LocalDateTime triggerTime) {
        this.triggerTime = triggerTime;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public Transaction status(TransactionStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public Transaction message(String message) {
        this.message = message;
        return this;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public Transaction data(String data) {
        this.data = data;
        return this;
    }

    public void setData(String data) {
        this.data = data;
    }

    public TransactionType getType() {
        return type;
    }

    public Transaction type(TransactionType type) {
        this.type = type;
        return this;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Transaction)) {
            return false;
        }
        return id != null && id.equals(((Transaction) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Transaction{" +
            "id=" + getId() +
            ", api='" + getApi() + "'" +
            ", triggerTime='" + getTriggerTime() + "'" +
            ", code='" + getStatus() + "'" +
            ", message='" + getMessage() + "'" +
            ", data='" + getData() + "'" +
            ", type=" + getType() + "'" +
            ", host=" + getHost() + "'" +
            ", method=" + getMethod() + "'" +
            ", createdBy=" + getCreatedBy() + "'" +
            ", action=" + getAction() + "'" +
            ", extension=" + getExtension() + "'" +
            "}";
    }

}
