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
import vn.easyca.signserver.webapp.web.rest.vm.request.PDFSignFileRequestVM;
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
                                                     PDFSignFileRequestVM signRequestVM) throws BadRequestException {

        try {
            byte[] content = file.getBytes();
            TokenInfoDto tokenInfo = mapSignatureInfo(signRequestVM.getSignatureInfo());
            SignPDFRequest request = new SignPDFRequest(tokenInfo, signRequestVM.getSigner(), content);
            PDFSignResponse signResponse = signService.signPDFFile(request);
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

        TokenInfoDto tokenInfoDto = mapSignatureInfo(signHashVM.getTokenInfo());
        SignHashRequest signHashRequest = new SignHashRequest(
            tokenInfoDto,
            signHashVM.getBase64Hash(),
            signHashVM.getBase64Hash()
        );
        try {
            SignHashResponse signHashResponse = signService.signHash(signHashRequest);
            return new SignResponseVM<SignHashResponse>(0, signHashResponse);
        } catch (Exception e) {
            return new SignResponseVM<String>(-1, e.getMessage());
        }
    }

    @PostMapping(value = "/xml")
    public SignResponseVM signXML(@RequestBody SignXmlRequestVM request) throws BadRequestException {

        TokenInfoDto tokenInfoDto = mapSignatureInfo(request.getSignatureInfo());
        SignXMLRequest xmlRequest = new SignXMLRequest(tokenInfoDto, request.getSigner(), request.getXml());
        try {
            String xml = signService.signXML(xmlRequest, tokenInfoDto);
            return new SignResponseVM(0, xml);
        } catch (Exception e) {
            return new SignResponseVM(-1, e.getMessage());
        }

    }

    private TokenInfoDto mapSignatureInfo(TokenInfoVM tokenInfoVM) {

        return new TokenInfoDto(tokenInfoVM.getSerial(), tokenInfoVM.getPin());
    }

}
