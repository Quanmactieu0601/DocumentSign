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
import vn.easyca.signserver.webapp.web.rest.vm.TokenInfoVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.BaseSignRequestVM;
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
    public ResponseEntity<ByteArrayResource> signPDF(@RequestParam MultipartFile file,
                                                     BaseSignRequestVM signRequestVM) throws BadRequestException {

        try {
            byte[] content = file.getBytes();
            SignPDFRequest request = new SignPDFRequest(mapSignatureInfo(signRequestVM), signRequestVM.getSigner(), content);
            PDFSignResponse signResponse = signService.signPDFFile(request, new TokenInfoDto(signRequestVM.getSignatureInfo().getSerial(), signRequestVM.getSignatureInfo().getPin()));
            ByteArrayResource resource = new ByteArrayResource(signResponse.getContent());
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName() + ".pdf")
                .contentLength(resource.contentLength()) //
                .body(resource);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }

    }

    @PostMapping(value = "/hash")
    public SignResponseVM signHash(@RequestBody SignHashRequestVM signHashVM) throws BadRequestException {

        SignHashRequest signHashRequest = new SignHashRequest(
            mapSignatureInfo(signHashVM),
            signHashVM.getSigner(),
            signHashVM.getBase64Hash(),
            signHashVM.getBase64Hash()
        );
        try {
            SignHashResponse signHashResponse = signService.signHash(signHashRequest, mapSignatureInfo(signHashVM));
            return new SignResponseVM(0, signHashResponse);
        } catch (Exception e) {
            return new SignResponseVM(-1,e.getMessage());
        }
    }

    @PostMapping(value = "/xml")
    public SignResponseVM signXML(@RequestBody SignXmlRequestVM request) throws BadRequestException {

        SignXMLRequest xmlRequest = new SignXMLRequest(mapSignatureInfo(request), request.getSigner(), request.getXml());
        TokenInfoDto tokenInfoDto = mapSignatureInfo(request);
        try {
            String xml = signService.signXML(xmlRequest, tokenInfoDto);
            return new SignResponseVM(0, xml);
        } catch (Exception e) {
           return new SignResponseVM(-1,e.getMessage());
        }

    }

    private TokenInfoDto mapSignatureInfo(BaseSignRequestVM requestVM) {

        TokenInfoVM tokenInfoVM = requestVM.getSignatureInfo();
        return new TokenInfoDto(tokenInfoVM.getSerial(), tokenInfoVM.getPin());
    }

}
