package vn.easyca.signserver.webapp.service.error;

public class RequiredColumnNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public RequiredColumnNotFoundException(){
        super("Check required column (*) tag in template File !");
    }
}
