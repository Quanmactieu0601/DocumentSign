package study.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A OtpHistory.
 */
@Entity
@Table(name = "otp_history")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class OtpHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "com_id")
    private Long comId;

    @Column(name = "secret_key")
    private String secretKey;

    @Column(name = "otp")
    private String otp;

    @Column(name = "action_time")
    private LocalDateTime actionTime;

    @Column(name = "expire_time")
    private LocalDateTime expireTime;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public OtpHistory userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getComId() {
        return comId;
    }

    public OtpHistory comId(Long comId) {
        this.comId = comId;
        return this;
    }

    public void setComId(Long comId) {
        this.comId = comId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public OtpHistory secretKey(String secretKey) {
        this.secretKey = secretKey;
        return this;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getOtp() {
        return otp;
    }

    public OtpHistory otp(String otp) {
        this.otp = otp;
        return this;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public LocalDateTime getActionTime() {
        return actionTime;
    }

    public OtpHistory actionTime(LocalDateTime actionTime) {
        this.actionTime = actionTime;
        return this;
    }

    public void setActionTime(LocalDateTime actionTime) {
        this.actionTime = actionTime;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public OtpHistory expireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
        return this;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OtpHistory)) {
            return false;
        }
        return id != null && id.equals(((OtpHistory) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OtpHistory{" +
            "id=" + getId() +
            ", userId=" + getUserId() +
            ", comId=" + getComId() +
            ", secretKey='" + getSecretKey() + "'" +
            ", otp='" + getOtp() + "'" +
            ", actionTime='" + getActionTime() + "'" +
            ", expireTime='" + getExpireTime() + "'" +
            "}";
    }
}
