package vn.easyca.signserver.webapp.service.dto;

import org.apache.commons.lang3.StringUtils;

public class CertRequestInfoDTO {
    private String taxCode; //mst;
    private String companyName; // tenDoanhNghiep;
    private String organization; // toChuc;
    private String organizationUnit; //donViToChuc;
    private String title; // chucVu;
    private String personalId; //cmnd;
    private String personalName; //tenCaNhan;
    private String email;
    private String phoneNumber; // sdt;
    private String locality; // diaChi;
    private String state; // tinh/thanhPho;
    private String country; // quocGia;

    private String alias;
    private String csrValue;

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getOrganizationUnit() {
        return organizationUnit;
    }

    public void setOrganizationUnit(String organizationUnit) {
        this.organizationUnit = organizationUnit;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPersonalId() {
        return personalId;
    }

    public void setPersonalId(String personalId) {
        this.personalId = personalId;
    }

    public String getPersonalName() {
        return personalName;
    }

    public void setPersonalName(String personalName) {
        this.personalName = personalName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getCsrValue() {
        return csrValue;
    }

    public void setCsrValue(String csrValue) {
        this.csrValue = csrValue;
    }

    public String getSubjectDN() {
        StringBuilder builder = new StringBuilder();
        String commonName = "";

        if(StringUtils.isNotEmpty(personalName)) {
            commonName = personalName;
        } else {
            commonName = companyName;
        }
        if (commonName.contains(","))
            commonName = String.format("\"%s\"", commonName);
        builder.append("CN=").append(commonName);

        if(StringUtils.isNotEmpty(title)) {
            builder.append(",T=").append(title);
        }
        if(StringUtils.isNotEmpty(email)) {
            builder.append(",E=").append(email);
        }
        if(StringUtils.isNotEmpty(organizationUnit)) {
            builder.append(",OU=").append(organizationUnit);
        }
        if(StringUtils.isNotEmpty(organization)) {
            builder.append(",O=").append(organization);
        }
        if(StringUtils.isNotEmpty(locality)) {
            builder.append(",L=").append(locality);
        }
        if(StringUtils.isNotEmpty(state)) {
            builder.append(",ST=").append(state);
        }
        if(StringUtils.isNotEmpty(country)) {
            builder.append(",C=").append(country);
        }
        return builder.toString();
    }
}
