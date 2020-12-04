package vn.easyca.signserver.webapp.enm;

public enum Status {
    FAIL(false),
    SUCCESS(true);

    private final boolean success ;
    Status(boolean success) {
        this.success = success;
    }

    public boolean isSucess() {
        return success;
    }
}
