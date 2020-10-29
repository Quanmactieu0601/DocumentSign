package vn.easyca.signserver.webapp.web.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import vn.easyca.signserver.core.domain.Certificate;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.core.services.P12ImportService;
import vn.easyca.signserver.core.services.CertificateGenerateService;
import vn.easyca.signserver.core.services.CertificateService;
import vn.easyca.signserver.core.dto.CertificateGenerateResult;
import vn.easyca.signserver.core.dto.CertificateGenerateDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.easyca.signserver.core.dto.ImportP12FileDTO;
import vn.easyca.signserver.webapp.enm.TransactionType;
import vn.easyca.signserver.webapp.service.TransactionService;
import vn.easyca.signserver.webapp.service.dto.TransactionDTO;
import vn.easyca.signserver.webapp.web.rest.mapper.CertificateGeneratorVMMapper;
import vn.easyca.signserver.webapp.utils.MappingHelper;
import vn.easyca.signserver.webapp.web.rest.vm.request.CertificateGeneratorVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.P12ImportVM;
import vn.easyca.signserver.webapp.web.rest.vm.response.CertificateGeneratorResultVM;
import vn.easyca.signserver.webapp.web.rest.vm.response.BaseResponseVM;
@RestController
@RequestMapping("/api/certificate")
@ComponentScan("vn.easyca.signserver.core.services")
public class CertificateResource {
    String code = null;
    String message = null;
    private static final Logger log = LoggerFactory.getLogger(CertificateResource.class);

    private static final String ENTITY_NAME = "certificate";

    private final CertificateGenerateService p11GeneratorService;

    private final CertificateService certificateService;

    private final P12ImportService p12ImportService;
    private final TransactionService transactionService;

    public CertificateResource(CertificateGenerateService p11GeneratorService, CertificateService certificateService, P12ImportService p12ImportService, TransactionService transactionService) {
        this.p11GeneratorService = p11GeneratorService;
        this.certificateService = certificateService;
        this.p12ImportService = p12ImportService;
        this.transactionService = transactionService;
    }

    @PostMapping("/import/p12")
    public ResponseEntity<BaseResponseVM> importP12File(@RequestBody P12ImportVM p12ImportVM) {
        TransactionDTO transactionDTO = new TransactionDTO("/api/certificate/import/p12", TransactionType.IMPORT_CERT);

        try {
            ImportP12FileDTO serviceInput = MappingHelper.map(p12ImportVM, ImportP12FileDTO.class);
            p12ImportService.insert(serviceInput);
            code = "200";
            message = "Insert P12File successfully";
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse("OK"));
        } catch (ApplicationException e) {
            log.error(e.getMessage(), e);
            code = "400";
            message = "ApplicationException";
            return ResponseEntity.ok(BaseResponseVM.CreateNewErrorResponse(e));
        }finally {
            transactionDTO.setCode(code);
            transactionDTO.setMessage(message);
            transactionService.save(transactionDTO);
        }
    }

    @PostMapping("/gen/p11")
    public ResponseEntity<BaseResponseVM> genCertificate(@RequestBody CertificateGeneratorVM certificateGeneratorVM) {
        TransactionDTO transactionDTO = new TransactionDTO("/api/certificate/gen/p11",TransactionType.IMPORT_CERT);
        try {
            CertificateGeneratorVMMapper mapper = new CertificateGeneratorVMMapper();
            CertificateGenerateDTO dto = mapper.map(certificateGeneratorVM);
            CertificateGenerateResult result = p11GeneratorService.genCertificate(dto);
            CertificateGeneratorResultVM certificateGeneratorResultVM = new CertificateGeneratorResultVM();
            Object viewModel = MappingHelper.map(result, certificateGeneratorResultVM.getClass());
            code ="200";
            message = "Gen Certificate Successfully";
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(viewModel));
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
        }finally {
            transactionDTO.setCode(code);
            transactionDTO.setMessage(message);
            transactionService.save(transactionDTO);
        }
    }

    @GetMapping("/get-by-serial")
    public ResponseEntity<BaseResponseVM> getBase64Cert(@RequestParam String serial) {
        TransactionDTO transactionDTO = new TransactionDTO("/api/certificate/get-by-serial",TransactionType.IMPORT_CERT);
        try {
            Certificate certificate = certificateService.getBySerial(serial);
            code = "200";
            message = "Get Base64Cert Successfully";
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(certificate.getRawData()));
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
        }finally {
            transactionDTO.setCode(code);
            transactionDTO.setMessage(message);
            transactionService.save(transactionDTO);
        }
    }
}
