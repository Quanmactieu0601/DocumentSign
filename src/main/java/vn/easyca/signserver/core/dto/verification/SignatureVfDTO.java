package vn.easyca.signserver.core.dto.verification;

import java.util.List;

public class SignatureVfDTO {
    private boolean isIntegrity;
    private boolean coverWholeDocument;
    private int revision;
    private int totalRevision;
    private String signTime;

    private List<CertificateVfDTO> certificateVfDTOs;

    public boolean isIntegrity() {
        return isIntegrity;
    }

    public void setIntegrity(boolean integrity) {
        isIntegrity = integrity;
    }

    public boolean isCoverWholeDocument() {
        return coverWholeDocument;
    }

    public void setCoverWholeDocument(boolean coverWholeDocument) {
        this.coverWholeDocument = coverWholeDocument;
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    public int getTotalRevision() {
        return totalRevision;
    }

    public void setTotalRevision(int totalRevision) {
        this.totalRevision = totalRevision;
    }

    public List<CertificateVfDTO> getCertificateVfDTOs() {
        return certificateVfDTOs;
    }

    public String getSignTime() {
        return signTime;
    }

    public void setSignTime(String signTime) {
        this.signTime = signTime;
    }

    public void setCertificateVfDTOs(List<CertificateVfDTO> certificateVfDTOs) {
        this.certificateVfDTOs = certificateVfDTOs;
    }

    public SignatureVfDTO(boolean isIntegrity, boolean coverWholeDocument, int revision, int totalRevision) {
        this.isIntegrity = isIntegrity;
        this.coverWholeDocument = coverWholeDocument;
        this.revision = revision;
        this.totalRevision = totalRevision;
    }

    public SignatureVfDTO(boolean isIntegrity, boolean coverWholeDocument, int revision, int totalRevision, String signTime) {
        this.isIntegrity = isIntegrity;
        this.coverWholeDocument = coverWholeDocument;
        this.revision = revision;
        this.totalRevision = totalRevision;
        this.signTime = signTime;
    }

    public SignatureVfDTO(boolean isIntegrity, boolean coverWholeDocument, int revision, int totalRevision, List<CertificateVfDTO> certificateVfDTOs) {
        this.isIntegrity = isIntegrity;
        this.coverWholeDocument = coverWholeDocument;
        this.revision = revision;
        this.totalRevision = totalRevision;
        this.certificateVfDTOs = certificateVfDTOs;
    }

    public SignatureVfDTO() {
    }
}
