package study.config;

import static study.enums.SystemConfigType.BOOLEAN;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import study.enums.SystemConfigKey;
import study.enums.SystemConfigType;
import study.service.dto.SystemConfigDTO;
import vn.easyca.signserver.core.exception.ApplicationException;

public class SystemDbConfiguration {

    public static SystemDbConfiguration init(List<SystemConfigDTO> systemConfigDTOList) throws ApplicationException {
        if (systemConfigDTOList == null || systemConfigDTOList.isEmpty()) throw new ApplicationException(
            "System configuration doesn't have any record!"
        );
        SystemDbConfiguration configuration = new SystemDbConfiguration();
        for (SystemConfigKey key : EnumSet.allOf(SystemConfigKey.class)) {
            Optional<SystemConfigDTO> dto = systemConfigDTOList.stream().filter(s -> s.getKey().equals(key)).findFirst();
            if (dto.isPresent()) {
                switch (key) {
                    case USE_OTP:
                        configuration.useOTP = new TypeConverter<Boolean>().getValue(dto.get().getValue(), dto.get().getDataType());
                        break;
                    case SYMMETRIC_KEY:
                        configuration.symmetricKey = new TypeConverter<String>().getValue(dto.get().getValue(), dto.get().getDataType());
                        break;
                    case SAVE_TOKEN_PASSWORD:
                        configuration.saveTokenPassword =
                            new TypeConverter<Boolean>().getValue(dto.get().getValue(), dto.get().getDataType());
                        break;
                    case OTP_LIFE_TIME_SECOND:
                        configuration.otpLifeTime = new TypeConverter<Integer>().getValue(dto.get().getValue(), dto.get().getDataType());
                        break;
                }
            } else {
                // Init default config value if config key is not configured
                switch (key) {
                    case USE_OTP:
                        configuration.useOTP = false;
                        break;
                    case OTP_LIFE_TIME_SECOND:
                        configuration.otpLifeTime = 0;
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
    private Integer otpLifeTime;

    public Boolean getSaveTokenPassword() {
        return saveTokenPassword;
    }

    public String getSymmetricKey() {
        return symmetricKey;
    }

    public Boolean getUseOTP() {
        return useOTP;
    }

    public Integer getOtpLifeTime() {
        return otpLifeTime;
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
