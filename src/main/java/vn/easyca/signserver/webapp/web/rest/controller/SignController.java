package vn.easyca.signserver.webapp.web.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.easyca.signserver.business.services.SignatureVerificationService;
import vn.easyca.signserver.business.services.dto.SignatureVerificationRequest;
import vn.easyca.signserver.business.services.dto.SignatureVerificationResponse;
import vn.easyca.signserver.business.services.sign.SignService;
import vn.easyca.signserver.business.services.sign.dto.request.content.PDFSignContent;
import vn.easyca.signserver.business.services.sign.dto.request.SignRequest;
import vn.easyca.signserver.business.services.sign.dto.response.PDFSigningDataRes;
import vn.easyca.signserver.business.services.sign.dto.response.SignDataResponse;
import vn.easyca.signserver.business.services.sign.dto.response.SignResultElement;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.*;
import vn.easyca.signserver.webapp.web.rest.vm.response.BaseResponseVM;

import java.util.List;

@RestController
@RequestMapping("/api/sign")
public class SignController {

    @Autowired
    private SignService signService;

    @Autowired
    private SignatureVerificationService verificationService;

    @PostMapping(value = "/pdf", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Object> signPDF(@RequestParam MultipartFile file, SigningVM<PDFSigningContentVM> signingVM) {
        try {
            byte[] fileData = file.getBytes();
            SignRequest<PDFSignContent> signRequest = signingVM.getDTO(PDFSignContent.class);
            signRequest.getSignElements().get(0).getContent().setFileData(fileData);
            PDFSigningDataRes signResponse = signService.signPDFFile(signRequest);
            ByteArrayResource resource = new ByteArrayResource(signResponse.getContent());
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName() + ".pdf")
                .contentLength(resource.contentLength()) //
                .body(resource);
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        }
    }

    @PostMapping(value = "/hash")
    public ResponseEntity<BaseResponseVM> signHash(@RequestBody SigningVM<String> signingVM) {
        try {
            SignRequest<String> request = signingVM.getDTO(String.class);
            Object signingDataResponse = signService.signHash(request);
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(signingDataResponse));
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponseVM.CreateNewErrorResponse(e.getMessage()));
        }
    }

    @PostMapping(value = "/raw")
    public ResponseEntity<BaseResponseVM> signRaw(@RequestBody SigningVM<String> signingVM) {
        try {
            SignRequest<String> request = signingVM.getDTO(String.class);
            SignDataResponse<List<SignResultElement>> signResponse = signService.signRaw(request);
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(signResponse));
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponseVM.CreateNewErrorResponse(e.getMessage()));
        }
    }

}
