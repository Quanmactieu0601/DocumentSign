package vn.easyca.signserver.webapp.web.rest;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.easyca.signserver.webapp.service.SignService;
import vn.easyca.signserver.webapp.service.dto.request.SignPDFRequestDto;
import vn.easyca.signserver.webapp.service.dto.request.SignDataRequest;
import vn.easyca.signserver.webapp.service.dto.request.SignXMLRequest;
import vn.easyca.signserver.webapp.service.dto.response.PDFSignResponse;
import vn.easyca.signserver.webapp.service.dto.response.SignDataResponse;
import vn.easyca.signserver.webapp.web.rest.vm.BaseResponseVM;

@RestController
@RequestMapping("/api/signing")
public class SigningController {

    private SignService signService;

    public SigningController(SignService signService) {
        this.signService = signService;
    }

    @PostMapping(value = "/pdf", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity signPDF(@RequestParam MultipartFile file, SignPDFRequestDto signPDFRequestDto) {

        try {
            byte[] content = file.getBytes();
            PDFSignResponse signResponse = signService.signPDFFile(signPDFRequestDto);
            ByteArrayResource resource = new ByteArrayResource(signResponse.getContent());
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName() + ".pdf")
                .contentLength(resource.contentLength()) //
                .body(resource);
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        }

    }

    // not correct. change signService.signData by signService.signHash in next day.
    @PostMapping(value = "/hash")
    public ResponseEntity<BaseResponseVM> signHash(@RequestBody SignDataRequest signDataRequest) {
        try {
            SignDataResponse signDataResponse = signService.signHash(signDataRequest);
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(signDataResponse));
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponseVM.CreateNewErrorResponse(e.getMessage()));
        }
    }

    @PostMapping(value = "/data")
    public ResponseEntity<BaseResponseVM> signData(@RequestBody SignDataRequest signDataRequest) {

        try {
            SignDataResponse signDataResponse = signService.signData(signDataRequest);
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(signDataResponse));
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponseVM.CreateNewErrorResponse(e.getMessage()));
        }
    }

    @PostMapping(value = "/xml")
    public ResponseEntity<BaseResponseVM> signXML(SignXMLRequest request) {
        try {
            String xml = signService.signXML(request);
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(xml));
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(e.getMessage()));
        }
    }
}
