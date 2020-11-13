package vn.easyca.signserver.webapp.service.error;

public class InfoFromCNToCountryNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    public InfoFromCNToCountryNotFoundException(){
        super("You must fill at least one value from Common Name to Country in excel table !");
    }
}
