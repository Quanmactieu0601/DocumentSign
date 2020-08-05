package vn.easyca.signserver.webapp.web.rest.controlller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.easyca.signserver.business.services.signing.SigningService;
import vn.easyca.signserver.business.services.signing.dto.request.content.PDFSigningContent;
import vn.easyca.signserver.business.services.signing.dto.request.content.RawSigningContent;
import vn.easyca.signserver.business.services.signing.dto.request.SigningRequest;
import vn.easyca.signserver.business.services.signing.dto.request.content.XMLSigningContent;
import vn.easyca.signserver.business.services.signing.dto.response.PDFSigningDataRes;
import vn.easyca.signserver.business.services.signing.dto.response.SigningDataResponse;
import vn.easyca.signserver.webapp.web.rest.vm.response.BaseResponseVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.PDFSigningContentVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.RawSigningContentVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.SigningVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.XMLSigningContentVM;

@RestController
@RequestMapping("/api/signing")
public class SigningController {

    @Autowired
    private SigningService signingService;

    @PostMapping(value = "/pdf", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity signPDF(@RequestParam MultipartFile file, SigningVM<PDFSigningContentVM> signingVM) {

        try {
            byte[] fileData = file.getBytes();
            SigningRequest<PDFSigningContent> signingRequest = signingVM.getSigningRequest(PDFSigningContent.class);
            if (signingRequest.getContent() == null)
                signingRequest.setContent(new PDFSigningContent());
            signingRequest.getContent().setFileData(fileData);
            PDFSigningDataRes signResponse = signingService.signPDFFile(signingRequest);
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
    public ResponseEntity<BaseResponseVM> signHash(@RequestBody SigningVM<RawSigningContentVM> signingVM) {
        try {
            SigningRequest<RawSigningContent> request = signingVM.getSigningRequest(RawSigningContent.class);
            SigningDataResponse<String> signingDataResponse = signingService.signHash(request);
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(signingDataResponse));
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponseVM.CreateNewErrorResponse(e.getMessage()));
        }
    }

    @PostMapping(value = "/data")
    public ResponseEntity<BaseResponseVM> signData(@RequestBody SigningVM<RawSigningContentVM> signingVM) {
        try {
            SigningRequest<RawSigningContent> signingRequest = signingVM.getSigningRequest(RawSigningContent.class);
            SigningDataResponse<String> signingDataResponse = signingService.signData(signingRequest);
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(signingDataResponse));
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponseVM.CreateNewErrorResponse(e.getMessage()));
        }
    }

    @PostMapping(value = "/xml")
    public ResponseEntity<BaseResponseVM> signXML(SigningVM<XMLSigningContentVM> signingVM) {
        try {
            SigningRequest<XMLSigningContent> request = signingVM.getSigningRequest(XMLSigningContent.class);
            SigningDataResponse<String> response = signingService.signXML(request);
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(response));
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(e.getMessage()));
        }
    }


}
