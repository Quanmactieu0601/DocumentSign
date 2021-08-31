package vn.easyca.signserver.webapp.web.rest.vm.request.sign;

import vn.easyca.signserver.core.dto.CertificateGenerateDTO;

public class QuickSignVM extends CertificateGenerateDTO {
    private byte[] data;
    private String type;

    public byte[] getData() { return data; }

    public void setData(byte[] data) { this.data = data; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }
}
