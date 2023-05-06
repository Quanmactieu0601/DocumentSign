package core.dto.verification;

import java.util.List;

public class VerificationResponseDTO {

    private List<SignatureVfDTO> signatureVfDTOs;

    public List<SignatureVfDTO> getSignatureVfDTOs() {
        return signatureVfDTOs;
    }

    public void setSignatureVfDTOs(List<SignatureVfDTO> signatureVfDTOs) {
        this.signatureVfDTOs = signatureVfDTOs;
    }
}
