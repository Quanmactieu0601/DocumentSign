package core.dto.sign.newresponse;

public class SigningResponseContent {

    private String documentName;
    private byte[] signatureOnly;
    private byte[] signedDocument;

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public byte[] getSignatureOnly() {
        return signatureOnly;
    }

    public void setSignatureOnly(byte[] signatureOnly) {
        this.signatureOnly = signatureOnly;
    }

    public byte[] getSignedDocument() {
        return signedDocument;
    }

    public void setSignedDocument(byte[] signedDocument) {
        this.signedDocument = signedDocument;
    }

    public SigningResponseContent(String documentName, byte[] signatureOnly, byte[] signedDocument) {
        this.documentName = documentName;
        this.signatureOnly = signatureOnly;
        this.signedDocument = signedDocument;
    }

    public SigningResponseContent() {}
}
