package vn.easyca.signserver.webapp.enm;

import vn.easyca.signserver.webapp.utils.QueryUtils;

public enum Extension {
    PDF, XML, OOXML, RAW, CSR, P12, P11, CERT, HASH, NONE;

    public static Extension from(String extension) {
        if (QueryUtils.isNullOrEmptyProperty(extension)) {
            return null;
        }
        return Extension.valueOf(extension);
    }
}
