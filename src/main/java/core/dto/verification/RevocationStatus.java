package core.dto.verification;

public enum RevocationStatus {
    // The certificate is root cer | no need to check
    UNCHECKED,

    // Can not check revoked status (maybe for CRL or OCSP not work)
    CANT_VERIFY,

    // The certificate was revoked
    REVOKED,

    //The certificate is valid
    GOOD,
}
