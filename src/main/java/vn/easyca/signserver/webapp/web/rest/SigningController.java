package vn.easyca.signserver.webapp.web.rest;

import io.undertow.util.BadRequestException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.easyca.signserver.webapp.service.dto.*;
import vn.easyca.signserver.webapp.service.SignService;
import vn.easyca.signserver.webapp.service.dto.request.SignPDFRequest;
import vn.easyca.signserver.webapp.service.dto.request.SignDataRequest;
import vn.easyca.signserver.webapp.service.dto.request.SignXMLRequest;
import vn.easyca.signserver.webapp.service.dto.response.PDFSignResponse;
import vn.easyca.signserver.webapp.service.dto.response.SignDataResponse;
import vn.easyca.signserver.webapp.web.rest.vm.TokenInfoVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.PDFSignFileRequestVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.SignRawDataVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.SignRequestVM;
import vn.easyca.signserver.webapp.web.rest.vm.response.BaseResponseVM;
import vn.easyca.signserver.webapp.web.rest.vm.response.SignResponseVM;

@RestController
@RequestMapping("/api/signing")
public class SigningController {

    private SignService signService;

    public SigningController(SignService signService) {
        this.signService = signService;
    }

    @PostMapping(value = "/pdf", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity signPDF(@RequestParam MultipartFile file, PDFSignFileRequestVM signRequestVM) {

        try {
            byte[] content = file.getBytes();
            TokenInfoDto tokenInfo = signRequestVM.getTokenDTO();
            SignPDFRequest request = new SignPDFRequest(tokenInfo, signRequestVM.getSigner(), content);
            PDFSignResponse signResponse = signService.signPDFFile(request);
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
    public ResponseEntity<BaseResponseVM> signHash(@RequestBody SignRequestVM<SignRawDataVM> requestVM) {
        TokenInfoDto tokenInfoDto = requestVM.getTokenDTO();
        SignDataRequest signDataRequest = new SignDataRequest(tokenInfoDto, requestVM.getData().getBase64Data(), null);
        try {
            SignDataResponse signDataResponse = signService.signHash(signDataRequest);
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(signDataResponse));
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponseVM.CreateNewErrorResponse(e.getMessage()));
        }
    }

    @PostMapping(value = "/data")
    public ResponseEntity<BaseResponseVM> signData(@RequestBody SignRequestVM<SignRawDataVM> requestVM) throws BadRequestException {

        TokenInfoDto tokenInfoDto = requestVM.getTokenDTO();
        SignDataRequest signDataRequest = new SignDataRequest(tokenInfoDto, requestVM.getData().getBase64Data(), null);
        try {
            SignDataResponse signDataResponse = signService.signData(signDataRequest);
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(signDataResponse));
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponseVM.CreateNewErrorResponse(e.getMessage()));
        }
    }

    @PostMapping(value = "/xml")
    public ResponseEntity<BaseResponseVM> signXML(@RequestBody SignRequestVM<String> request) throws BadRequestException {
        TokenInfoDto tokenInfoDto = request.getTokenDTO();
        SignXMLRequest xmlRequest = new SignXMLRequest(tokenInfoDto, request.getSigner(), request.getData());
        try {
            String xml = signService.signXML(xmlRequest, tokenInfoDto);
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(xml));
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(e.getMessage()));
        }
    }
}
