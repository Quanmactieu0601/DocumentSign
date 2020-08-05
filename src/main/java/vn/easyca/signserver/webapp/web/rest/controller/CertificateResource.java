package vn.easyca.signserver.webapp.web.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import vn.easyca.signserver.business.domain.Certificate;
import vn.easyca.signserver.business.services.P12ImportService;
import vn.easyca.signserver.business.services.CertGenService;
import vn.easyca.signserver.business.services.CertificateService;
import vn.easyca.signserver.business.services.dto.CertificateGeneratedResult;
import vn.easyca.signserver.business.services.dto.CertificateGeneratorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.easyca.signserver.business.services.dto.ImportP12FileDTO;
import vn.easyca.signserver.webapp.web.rest.mapper.CertificateGeneratorVMMapper;
import vn.easyca.signserver.webapp.utils.MappingHelper;
import vn.easyca.signserver.webapp.web.rest.vm.request.CertificateGeneratorVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.P12ImportVM;
import vn.easyca.signserver.webapp.web.rest.vm.response.CertificateGeneratorResultVM;
import vn.easyca.signserver.webapp.web.rest.vm.response.BaseResponseVM;


@RestController
@RequestMapping("/api/certificate")
@ComponentScan("vn.easyca.signserver.business.services")
public class CertificateResource {

    private final Logger log = LoggerFactory.getLogger(CertificateResource.class);

    private static final String ENTITY_NAME = "certificate";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    @Autowired
    private CertGenService p11GeneratorService;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private P12ImportService p12ImportService;



    @PostMapping("/import/p12")
    public ResponseEntity<BaseResponseVM> importP12File(@RequestBody P12ImportVM p12ImportVM)  {
        try {
            ImportP12FileDTO serviceInput = MappingHelper.map(p12ImportVM, ImportP12FileDTO.class);
            p12ImportService.insert(serviceInput);
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse("OK"));
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponseVM.CreateNewErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/gen/p11")
    public ResponseEntity<BaseResponseVM> genCertificate(@RequestBody CertificateGeneratorVM certificateGeneratorVM) {
        try {
            CertificateGeneratorVMMapper mapper = new CertificateGeneratorVMMapper();
            CertificateGeneratorDto dto = mapper.map(certificateGeneratorVM);
            CertificateGeneratedResult result = p11GeneratorService.genCertificate(dto);
            CertificateGeneratorResultVM certificateGeneratorResultVM = new CertificateGeneratorResultVM();
            Object viewModel =  MappingHelper.map(result,certificateGeneratorResultVM.getClass());
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(viewModel));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(e.getMessage()));
        }
    }

    @GetMapping("/get-by-serial")
    public ResponseEntity<BaseResponseVM> getBase64Cert(@RequestParam String serial) {
        Certificate certificate = certificateService.getBySerial(serial);
        if (certificate != null)
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(certificate.getRawData()));
        else
            return ResponseEntity.ok(BaseResponseVM.CreateNewErrorResponse("Cert is not found"));
    }
}
