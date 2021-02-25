package vn.easyca.signserver.webapp.enm;

public enum SystemConfigKey {
    /**
     * SAVE_TOKEN_PASSWORD
     * - p12: use this option to save cert pin to DB and skip check pin when signing
     * - hsm: always auto generate cert pin - dont depend on this config
     */
    SAVE_TOKEN_PASSWORD,

    /**
     * USE_OTP
     * value = 1 : use otp to authen before signing
     * value = 0 : not use otp
     */
    USE_OTP,


    /**
     * SYMMETRIC_KEY
     * key to encrypt sensitive data in database
     */
    SYMMETRIC_KEY,

    /**
     * OTP_LIFE_TIME_SECOND
     * unit: second
     * when using otp authen and use this configuration, the system will save OTP code once request and check its valid time base on the value of this config
     *
     */

    OTP_LIFE_TIME_SECOND
}
