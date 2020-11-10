package vn.easyca.signserver.webapp.service.dto;

public class TransactionReportDTO {
    private int totalsuccess;

    private int totalfail;

    public int getTotalsuccess() {
        return totalsuccess;
    }

    public void setTotalsuccess(int totalsuccess) {
        this.totalsuccess = totalsuccess;
    }


    public int getTotalfail() {
        return totalfail;
    }

    public void setTotalfail(int totalfail) {
        this.totalfail = totalfail;
    }
}
