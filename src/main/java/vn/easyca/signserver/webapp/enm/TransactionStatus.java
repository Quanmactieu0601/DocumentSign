package vn.easyca.signserver.webapp.enm;

import vn.easyca.signserver.webapp.utils.QueryUtils;

public enum TransactionStatus {
    FAIL,
    SUCCESS;

    // enum is set interger in database
    public static TransactionStatus from(String status) {
        if (QueryUtils.isNullOrEmptyProperty(status)) {
            return null;
        }
        return TransactionStatus.valueOf(status);
    }
}
