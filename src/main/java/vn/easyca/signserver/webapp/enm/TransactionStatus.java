package vn.easyca.signserver.webapp.enm;

import vn.easyca.signserver.webapp.utils.QueryUtils;

public enum TransactionStatus {
    FAIL(false),
    SUCCESS(true);

    private final boolean success;

    TransactionStatus(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public static TransactionStatus from(String status) {
        if (QueryUtils.isNullOrEmptyProperty(status)) {
            return null;
        }
        return TransactionStatus.valueOf(status);
    }
}
