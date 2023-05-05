package study.enums;

import study.utils.QueryUtils;

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
