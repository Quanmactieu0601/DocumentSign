package vn.easyca.signserver.webapp.service.error;

public class InvalidCountryColumnLength extends RuntimeException {

    private static final long serialVersionUID = 1L;
    public InvalidCountryColumnLength(){
        super("Length of column Country in excel must be 2 character !");
    }
}
