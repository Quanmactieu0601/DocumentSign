package study.domain;

import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import study.enums.SystemConfigKey;
import study.enums.SystemConfigType;

/**
 * A SystemConfig.
 */
@Entity
@Table(name = "system_config")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SystemConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "com_id")
    private Long comId;

    @Column(name = "config_key")
    @Enumerated(value = EnumType.STRING)
    private SystemConfigKey key;

    @Column(name = "config_value")
    private String value;

    @Column(name = "description")
    private String description;

    @Column(name = "data_type")
    @Enumerated(value = EnumType.STRING)
    private SystemConfigType dataType;

    @Column(name = "activated")
    private Boolean activated;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getComId() {
        return comId;
    }

    public SystemConfig comId(Long comId) {
        this.comId = comId;
        return this;
    }

    public void setComId(Long comId) {
        this.comId = comId;
    }

    public SystemConfigKey getKey() {
        return key;
    }

    public SystemConfig key(SystemConfigKey key) {
        this.key = key;
        return this;
    }

    public void setKey(SystemConfigKey key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public SystemConfig value(String value) {
        this.value = value;
        return this;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public SystemConfig description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SystemConfigType getDataType() {
        return dataType;
    }

    public SystemConfig dataType(SystemConfigType dataType) {
        this.dataType = dataType;
        return this;
    }

    public void setDataType(SystemConfigType dataType) {
        this.dataType = dataType;
    }

    public Boolean isActivated() {
        return activated;
    }

    public SystemConfig activated(Boolean activated) {
        this.activated = activated;
        return this;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SystemConfig)) {
            return false;
        }
        return id != null && id.equals(((SystemConfig) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SystemConfig{" +
            "id=" + getId() +
            ", comId=" + getComId() +
            ", key='" + getKey() + "'" +
            ", value='" + getValue() + "'" +
            ", description='" + getDescription() + "'" +
            ", dataType='" + getDataType() + "'" +
            ", activated='" + isActivated() + "'" +
            "}";
    }
}
