package vn.easyca.signserver.webapp.web.rest;

import vn.easyca.signserver.webapp.enm.TransactionStatus;

public class BaseResource {
    protected String message;
    protected TransactionStatus status = TransactionStatus.FAIL;
}
