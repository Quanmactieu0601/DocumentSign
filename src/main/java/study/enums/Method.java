package study.enums;

import study.utils.QueryUtils;

public enum Method {
    POST,
    GET,
    DELETE,
    PUT;

    public static Method from(String method) {
        if (QueryUtils.isNullOrEmptyProperty(method)) {
            return null;
        }
        return Method.valueOf(method);
    }
}
