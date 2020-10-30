package vn.easyca.signserver.webapp.web.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.core.services.SigningService;
import vn.easyca.signserver.core.dto.sign.request.content.PDFSignContent;
import vn.easyca.signserver.core.dto.sign.request.SignRequest;
import vn.easyca.signserver.core.dto.sign.response.PDFSigningDataRes;
import vn.easyca.signserver.core.dto.sign.response.SignDataResponse;
import vn.easyca.signserver.core.dto.sign.response.SignResultElement;
import vn.easyca.signserver.webapp.enm.TransactionType;
import vn.easyca.signserver.webapp.service.TransactionService;
import vn.easyca.signserver.webapp.service.dto.TransactionDTO;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.*;
import vn.easyca.signserver.webapp.web.rest.vm.response.BaseResponseVM;

import java.util.List;

@RestController
@RequestMapping("/api/sign")
public class SignController {
    String code = null;
    String message = null;

    private final SigningService signService;
    private static final Logger log = LoggerFactory.getLogger(SignatureVerificationController.class);
    private final TransactionService transactionService;

    public SignController(SigningService signService, TransactionService transactionService) {
        this.signService = signService;
        this.transactionService = transactionService;
    }

    @PostMapping(value = "/pdf", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Object> signPDF(@RequestParam MultipartFile file, SigningVM<PDFSigningContentVM> signingVM) {
        TransactionDTO transactionDTO = new TransactionDTO("/api/sign/pdf", TransactionType.SIGNING);
        try {
            byte[] fileData = file.getBytes();
            SignRequest<PDFSignContent> signRequest = signingVM.getDTO(PDFSignContent.class);
            signRequest.getSignElements().get(0).getContent().setFileData(fileData);
            PDFSigningDataRes signResponse = signService.signPDFFile(signRequest);
            ByteArrayResource resource = new ByteArrayResource(signResponse.getContent());
            code = "200";
            message = "Sign PDF Successfully";
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName() + ".pdf")
                .contentLength(resource.contentLength()) //
                .body(resource);
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            code = "400";
            message = "ApplicationException";
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            code = "400";
            message = "Exception";
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        } finally {
            transactionDTO.setCode(code);
            transactionDTO.setMessage(message);
            transactionService.save(transactionDTO);
        }
    }

    @PostMapping(value = "/hash")
    public ResponseEntity<BaseResponseVM> signHash(@RequestBody SigningVM<String> signingVM) {
        TransactionDTO transactionDTO = new TransactionDTO("/api/sign/hash", TransactionType.SIGNING);
        try {
            SignRequest<String> request = signingVM.getDTO(String.class);
            Object signingDataResponse = signService.signHash(request);
            code = "200";
            message = "Sign Hash Successfully";
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(signingDataResponse));
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            code = "400";
            message = "ApplicationException";
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            code = "400";
            message = "Exception";
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        } finally {
            transactionDTO.setCode(code);
            transactionDTO.setMessage(message);
            transactionService.save(transactionDTO);
        }
    }

    @PostMapping(value = "/raw")
    public ResponseEntity<BaseResponseVM> signRaw(@RequestBody SigningVM<String> signingVM) {
        TransactionDTO transactionDTO = new TransactionDTO("/api/sign/raw", TransactionType.SIGNING);
        try {
            SignRequest<String> request = signingVM.getDTO(String.class);
            SignDataResponse<List<SignResultElement>> signResponse = signService.signRaw(request);
            code = "200";
            message = "Sign Raw Successfully";
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(signResponse));
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            code = "400";
            message = "ApplicationException";
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            code = "400";
            message = "Exception";
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        } finally {
            transactionDTO.setCode(code);
            transactionDTO.setMessage(message);
            transactionService.save(transactionDTO);
        }
    }

}
