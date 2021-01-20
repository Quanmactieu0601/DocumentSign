package vn.easyca.signserver.webapp.web.rest.errors;

public class CurrentPasswordNotMatchException extends BadRequestAlertException {
    public CurrentPasswordNotMatchException(){
        super(ErrorConstants.CURRENTPASS_NOT_MATCH_TYPE, "Current pass does not match!", "userManagement", "currentPassNotMatch");
    }
}
