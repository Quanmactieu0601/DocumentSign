package vn.easyca.signserver.webapp.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.easyca.signserver.webapp.service.SigningService;
import vn.easyca.signserver.webapp.service.dto.signing.request.PDFSigningData;
import vn.easyca.signserver.webapp.service.dto.signing.request.RawSigningData;
import vn.easyca.signserver.webapp.service.dto.signing.request.SigningRequest;
import vn.easyca.signserver.webapp.service.dto.signing.request.XMLSigningData;
import vn.easyca.signserver.webapp.service.dto.signing.response.PDFSigningDataRes;
import vn.easyca.signserver.webapp.service.dto.signing.response.SigningDataResponse;
import vn.easyca.signserver.webapp.web.rest.mapper.PDFSigningDataVMMapper;
import vn.easyca.signserver.webapp.web.rest.mapper.RawSigningDataVMMapper;
import vn.easyca.signserver.webapp.web.rest.mapper.SigningVMMapper;
import vn.easyca.signserver.webapp.web.rest.mapper.XMLSigningDataVMMapper;
import vn.easyca.signserver.webapp.web.rest.vm.BaseResponseVM;
import vn.easyca.signserver.webapp.web.rest.vm.sign.PDFSigningDataVM;
import vn.easyca.signserver.webapp.web.rest.vm.sign.RawSigningDataVM;
import vn.easyca.signserver.webapp.web.rest.vm.sign.SigningVM;
import vn.easyca.signserver.webapp.web.rest.vm.sign.XMLSigningDataVM;

@RestController
@RequestMapping("/api/signing")
public class SigningController {

    @Autowired
    private SigningService signingService;

    @Autowired
    private RawSigningDataVMMapper rawSigningDataVMMapper;

    @Autowired
    private XMLSigningDataVMMapper xmlSigningDataVMMapper;

    @Autowired
    private PDFSigningDataVMMapper pdfSigningDataVMMapper;


    @PostMapping(value = "/pdf", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity signPDF(@RequestParam MultipartFile file, SigningVM<PDFSigningDataVM> signingVM) {

        try {
            byte[] content = file.getBytes();
            SigningVMMapper signingVMMapper = new SigningVMMapper(pdfSigningDataVMMapper);
            SigningRequest<PDFSigningData> signingRequest = signingVMMapper.map(signingVM);
            signingRequest.getData().setContent(content);
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
    public ResponseEntity<BaseResponseVM> signHash(@RequestBody SigningVM<RawSigningDataVM> signingVM) {
        try {
            SigningVMMapper mapper = new SigningVMMapper(rawSigningDataVMMapper);
            SigningRequest<RawSigningData> dto = mapper.map(signingVM);
            SigningDataResponse signingDataResponse = signingService.signHash(dto);
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(signingDataResponse));
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponseVM.CreateNewErrorResponse(e.getMessage()));
        }
    }

    @PostMapping(value = "/data")
    public ResponseEntity<BaseResponseVM> signData(@RequestBody SigningVM<RawSigningDataVM> signingVM) {
        try {
            SigningVMMapper mapper = new SigningVMMapper(rawSigningDataVMMapper);
            SigningRequest<RawSigningData> dto = mapper.map(signingVM);
            SigningDataResponse signingDataResponse = signingService.signData(dto);
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(signingDataResponse));
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponseVM.CreateNewErrorResponse(e.getMessage()));
        }
    }

    @PostMapping(value = "/xml")
    public ResponseEntity<BaseResponseVM> signXML(SigningVM<XMLSigningDataVM> request) {
        try {
            SigningVMMapper mapper = new SigningVMMapper(xmlSigningDataVMMapper);
            SigningRequest<XMLSigningData> dto = mapper.map(request);
            String xml = signingService.signXML(dto);
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(xml));
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(e.getMessage()));
        }
    }
}
