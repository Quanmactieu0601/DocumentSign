package study.enums;

import study.utils.QueryUtils;

public enum TransactionType {
    SYSTEM,
    BUSINESS;

    public static TransactionType from(String type) {
        if (QueryUtils.isNullOrEmptyProperty(type)) {
            return null;
        }
        return TransactionType.valueOf(type);
    }
}
