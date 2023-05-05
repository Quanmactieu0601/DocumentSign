package study.enums;

import study.utils.QueryUtils;

public enum Action {
    SIGN,
    HASH,
    VERIFY,
    CREATE,
    MODIFY,
    DELETE,
    GET_INFO,
    LOGIN;

    public static Action from(String action) {
        if (QueryUtils.isNullOrEmptyProperty(action)) {
            return null;
        }
        return Action.valueOf(action);
    }
}
