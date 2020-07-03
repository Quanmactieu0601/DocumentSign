package vn.easyca.signserver.webapp.service.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CertificateInfo {

    private String serial;

    private Date validFrom;

    private Date validTo;

    private String issuer;

    private String subject;

}
