package core.dto.verification;

public enum CertStatus {
    // The certificate was valid at the checking time.
    VALID,

    // The certificate was expired at the checking time
    EXPIRED,

    //The certificate wasn't valid yet at the checking time
    INVALID,
}
