package vn.easyca.signserver.webapp.web.rest;

import io.undertow.util.BadRequestException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.easyca.signserver.webapp.service.dto.PDFSignRequest;
import vn.easyca.signserver.webapp.service.dto.PDFSignResponse;
import vn.easyca.signserver.webapp.service.SignService;
import vn.easyca.signserver.webapp.service.dto.SignHashRequest;
import vn.easyca.signserver.webapp.service.dto.SignHashResponse;
import vn.easyca.signserver.webapp.web.rest.vm.request.SignHashRequestVM;
import vn.easyca.signserver.webapp.web.rest.vm.response.SignResponseVM;

import java.util.Date;

@RestController
@RequestMapping("/api/signing")
public class SigningController {


    private SignService signService;

    public SigningController(SignService pdfSignService) {
        this.signService = pdfSignService;
    }


    @PostMapping(value = "/pdf", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam("serial") String serial,
                             @RequestParam("pin") String pin) throws BadRequestException {

        try {
            byte[] content = file.getBytes();
            PDFSignRequest request = PDFSignRequest
                .builder()
                .content(content)
                .serial(serial)
                .signDate(new Date())
                .pin(pin)
                .build();
            PDFSignResponse signResponse = signService.signPDFFile(request);
            return signResponse.getContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new BadRequestException();
    }

    @PostMapping(value = "/hash")
    public SignResponseVM signHash(@RequestBody SignHashRequestVM signHashVM){

        SignHashRequest signHashRequest = SignHashRequest
            .builder()
            .base64Hash(signHashVM.getBase64Hash())
            .hashAlgorithm(signHashVM.getHashAlgorithm())
            .serial(signHashVM.getHashAlgorithm())
            .pin(signHashVM.getPin()).build();
        try {
            SignHashResponse signHashResponse = signService.signHash(signHashRequest);
            return new SignResponseVM(0,signHashResponse);
        } catch (Exception e) {
            return new SignResponseVM(-1,e.getMessage());
        }
    }
}
