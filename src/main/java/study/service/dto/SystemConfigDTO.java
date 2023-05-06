package study.service.dto;

import java.io.Serializable;
import study.enums.SystemConfigKey;
import study.enums.SystemConfigType;

/**
 * A DTO for the {@link study.domain.SystemConfig} entity.
 */
public class SystemConfigDTO implements Serializable {

    private Long id;

    private Long comId;

    private SystemConfigKey key;

    private String value;

    private String description;

    private SystemConfigType dataType;

    private Boolean activated;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getComId() {
        return comId;
    }

    public void setComId(Long comId) {
        this.comId = comId;
    }

    public SystemConfigKey getKey() {
        return key;
    }

    public void setKey(SystemConfigKey key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SystemConfigType getDataType() {
        return dataType;
    }

    public void setDataType(SystemConfigType dataType) {
        this.dataType = dataType;
    }

    public Boolean isActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SystemConfigDTO)) {
            return false;
        }

        return id != null && id.equals(((SystemConfigDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SystemConfigDTO{" +
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
