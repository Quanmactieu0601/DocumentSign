package vn.easyca.signserver.webapp.config;

import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.webapp.enm.SystemConfigKey;
import vn.easyca.signserver.webapp.enm.SystemConfigType;
import vn.easyca.signserver.webapp.service.dto.SystemConfigDTO;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static vn.easyca.signserver.webapp.enm.SystemConfigType.BOOLEAN;

public class SystemDbConfiguration {
//    private static SystemDbConfiguration configuration;
//
//    public SystemDbConfiguration() {
//    }
//
//    public static SystemDbConfiguration getInstance() throws ApplicationException {
//        if (configuration != null)
//            return configuration;
//        throw new ApplicationException("System configuration has not configured!");
//    }

    public static SystemDbConfiguration init(List<SystemConfigDTO> systemConfigDTOList) throws ApplicationException {
        if (systemConfigDTOList == null || systemConfigDTOList.isEmpty())
            throw new ApplicationException("System configuration doesn't have any record!");
        SystemDbConfiguration configuration = new SystemDbConfiguration();
        for (SystemConfigKey key : EnumSet.allOf(SystemConfigKey.class)) {
            Optional<SystemConfigDTO> dto = systemConfigDTOList.stream().
                filter(s -> s.getKey().equals(key)).
                findFirst();
            if (dto.isPresent()) {
                switch (key) {
                    case USE_OTP:
                        configuration.useOTP = new TypeConverter<Boolean>().getValue(dto.get().getValue(), dto.get().getDataType());
                        break;
                    case SYMMETRIC_KEY:
                        configuration.symmetricKey = new TypeConverter<String>().getValue(dto.get().getValue(), dto.get().getDataType());
                        break;
                    case SAVE_TOKEN_PASSWORD:
                        configuration.saveTokenPassword = new TypeConverter<Boolean>().getValue(dto.get().getValue(), dto.get().getDataType());
                        break;
                }
            } else {
                switch (key) {
                    case USE_OTP:
                        configuration.useOTP = false;
                        break;
                    case SAVE_TOKEN_PASSWORD:
                    case SYMMETRIC_KEY:
                        throw new ApplicationException(String.format("System configuration: %s is missing", key));
                }
            }
        }
        return configuration;
    }

    /**
     * - use this option to save p12 cert pin to DB and skip check pin when signing
     * - hsm always auto generate cert pin - dont depend on this config
     */
    private Boolean saveTokenPassword;

    private String symmetricKey;
    private Boolean useOTP;

    public Boolean getSaveTokenPassword() {
        return saveTokenPassword;
    }

    public void setSaveTokenPassword(Boolean saveTokenPassword) {
        this.saveTokenPassword = saveTokenPassword;
    }

    public String getSymmetricKey() {
        return symmetricKey;
    }

    public void setSymmetricKey(String symmetricKey) {
        this.symmetricKey = symmetricKey;
    }

    public Boolean getUseOTP() {
        return useOTP;
    }

    public void setUseOTP(Boolean useOTP) {
        this.useOTP = useOTP;
    }
}

class TypeConverter<T> {
    public T getValue(String value, SystemConfigType type) throws ApplicationException {
        switch (type) {
            case BOOLEAN:
                return (T) (Boolean) value.trim().equals("1");
            case NUMERIC:
                return (T) Integer.valueOf(value.trim());
            case STRING:
                return (T) value.trim();
            default:
                throw new ApplicationException("System configuration type is not support: " + type);
        }
    }
}
