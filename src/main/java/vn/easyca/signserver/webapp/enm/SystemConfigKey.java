package vn.easyca.signserver.webapp.enm;

public enum SystemConfigKey {
    /**
     * SAVE_TOKEN_PASSWORD
     * - p12: use this option to save cert pin to DB and skip check pin when signing
     * - hsm: always auto generate cert pin - dont depend on this config
     */
    SAVE_TOKEN_PASSWORD,
    USE_OTP,
    SYMMETRIC_KEY,
    OTP_LIFE_TIME_SECOND
}
