package vn.easyca.signserver.application.exception;

public class ApplicationException extends Exception {

    protected static final int SERVER_INTERNAL_ERROR_CODE = 5;
    protected static final int CRYPTO_TOKEN_ERROR_CODE = 6;
    protected static final int GEN_CSR_ERROR_CODE = 7;
    protected static final int BAD_INPUT_ERROR_CODE = 8;
    protected static final int CERTIFICATE_ERROR_CODE = 9;
    protected static final int SIGN_ERROR_CODE = 10;
    protected static final int CERTIFICATE_NOT_FOUND = 11;
    protected static final int VERIFIED_EXCEPTION = 12;


    private int code;

    public ApplicationException(int code) {
        this.code = code;
    }

    public ApplicationException(int code, String message, Throwable e) {
        super(message, e);
        this.code = code;
    }

    public ApplicationException(int code, String msg) {
        super(msg);
    }

    public int getCode() {
        return code;
    }


    public static ApplicationException throwServerInternalError(String msg, Throwable e) throws ApplicationException {
        throw new ApplicationException(SERVER_INTERNAL_ERROR_CODE, "Server internal error", e);
    }

    public static ApplicationException throwCryptoTokenError(Throwable e) throws ApplicationException {
        throw new ApplicationException(CRYPTO_TOKEN_ERROR_CODE, "CryptoToken has error! Please check token info such as serial ,pin", e);
    }

    public static ApplicationException throwGenCSRError(Throwable e) throws ApplicationException {
        throw new ApplicationException(GEN_CSR_ERROR_CODE, "Can not create csr! Please recheck info to create certificate", e);
    }

    public static ApplicationException throwBadServiceInputError(String msg, Throwable e) throws ApplicationException {
        throw new ApplicationException(BAD_INPUT_ERROR_CODE, msg, e);
    }


}
