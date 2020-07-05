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
import vn.easyca.signserver.webapp.service.dto.request.SignHashRequest;
import vn.easyca.signserver.webapp.service.dto.request.SignXMLRequest;
import vn.easyca.signserver.webapp.service.dto.response.PDFSignResponse;
import vn.easyca.signserver.webapp.service.dto.response.SignHashResponse;
import vn.easyca.signserver.webapp.web.rest.vm.SignatureInfoVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.SignHashRequestVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.SignXmlRequestVM;
import vn.easyca.signserver.webapp.web.rest.vm.response.SignResponseVM;

@RestController
@RequestMapping("/api/signing")
public class SigningController {


    private SignService signService;

    public SigningController(SignService pdfSignService) {
        this.signService = pdfSignService;
    }


    @PostMapping(value = "/pdf", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<ByteArrayResource> signPDF(@RequestParam("file") MultipartFile file,
                                                     @RequestParam("serial") String serial,
                                                     @RequestParam("pin") String pin,
                                                     @RequestParam("signer") String signer) throws BadRequestException {

        try {
            byte[] content = file.getBytes();
            SignPDFRequest request = new SignPDFRequest(content);
            PDFSignResponse signResponse = signService.signPDFFile(request, new SignatureInfoDto(serial, pin));
            ByteArrayResource resource = new ByteArrayResource(signResponse.getContent());
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName() + ".pdf")
                .contentLength(resource.contentLength()) //
                .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new BadRequestException();
    }

    @PostMapping(value = "/hash")
    public SignResponseVM signHash(@RequestBody SignHashRequestVM signHashVM) throws BadRequestException {

        SignHashRequest signHashRequest = new SignHashRequest(signHashVM.getBase64Hash(),signHashVM.getBase64Hash());
        try {
            SignHashResponse signHashResponse = signService.signHash(signHashRequest,mapSignatureInfo(signHashVM.getSignatureInfo()));
            return new SignResponseVM(0, signHashResponse);
        } catch (Exception e) {
            throw new BadRequestException();
        }
    }

    @PostMapping(value = "/xml")
    public SignResponseVM signXML(@RequestBody SignXmlRequestVM request) throws BadRequestException {

        SignXMLRequest xmlRequest=  new SignXMLRequest(request.getXml(),request.getContentTag());
        SignatureInfoDto signatureInfoDto = mapSignatureInfo(request.getSignatureInfo());

        try {
            String xml = signService.signXML(xmlRequest,signatureInfoDto);
            return new SignResponseVM(0,xml);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException();
        }

    }

    private SignatureInfoDto mapSignatureInfo(SignatureInfoVM signatureInfoVM ){

        return new SignatureInfoDto(signatureInfoVM.getSerial(),signatureInfoVM.getPin());
    }

}
