package vn.easyca.signserver.webapp.web.rest.controller;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.swing.Java2DRenderer;
import vn.easyca.signserver.core.domain.CertificateDTO;
import vn.easyca.signserver.core.dto.sign.newrequest.SigningRequest;
import vn.easyca.signserver.core.dto.sign.newresponse.SigningResponse;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.core.exception.CertificateNotFoundAppException;
import vn.easyca.signserver.core.services.OfficeSigningService;
import vn.easyca.signserver.core.exception.CertificateAppException;
import vn.easyca.signserver.core.model.CryptoTokenProxy;
import vn.easyca.signserver.core.model.CryptoTokenProxyException;
import vn.easyca.signserver.core.model.CryptoTokenProxyFactory;
import vn.easyca.signserver.core.services.SigningService;
import vn.easyca.signserver.core.dto.sign.request.content.PDFSignContent;
import vn.easyca.signserver.core.dto.sign.request.SignRequest;
import vn.easyca.signserver.core.dto.sign.response.PDFSigningDataRes;
import vn.easyca.signserver.core.dto.sign.response.SignDataResponse;
import vn.easyca.signserver.core.dto.sign.response.SignResultElement;
import vn.easyca.signserver.core.utils.HtmlImageGeneratorCustom;
import vn.easyca.signserver.webapp.domain.UserEntity;
import vn.easyca.signserver.webapp.enm.TransactionType;
import vn.easyca.signserver.webapp.service.*;
import vn.easyca.signserver.webapp.utils.AccountUntils;
import vn.easyca.signserver.webapp.enm.Method;
import vn.easyca.signserver.webapp.utils.AccountUtils;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.*;
import vn.easyca.signserver.webapp.web.rest.vm.response.BaseResponseVM;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RestController
@RequestMapping("/api/sign")
public class SigningResource {
    private final SigningService signService;
    private static final Logger log = LoggerFactory.getLogger(SignatureVerificationResource.class);
    private final TransactionService transactionService;
    private final OfficeSigningService officeSigningService;
    private final CertificateService certificateService;
    private final UserApplicationService userApplicationService;
    private final CryptoTokenProxyFactory cryptoTokenProxyFactory;
    private final SignatureTemplateService signatureTemplateService;
    private final AsyncTransactionService asyncTransactionService;

    public SigningResource(SigningService signService, TransactionService transactionService, CertificateService certificateService, UserApplicationService userApplicationService,
                           SignatureTemplateService signatureTemplateService, OfficeSigningService officeSigningService, AsyncTransactionService asyncTransactionService,
                           CryptoTokenProxyFactory cryptoTokenProxyFactory) {
        this.signService = signService;
        this.transactionService = transactionService;
        this.certificateService = certificateService;
        this.cryptoTokenProxyFactory = cryptoTokenProxyFactory;
        this.userApplicationService = userApplicationService;
        this.signatureTemplateService = signatureTemplateService;
        this.officeSigningService = officeSigningService;
        this.asyncTransactionService = asyncTransactionService;
    }

    @PostMapping(value = "/pdf", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Object> signPDF(@RequestParam MultipartFile file, SigningVM<PDFSigningContentVM> signingVM) {
        try {
            byte[] fileData = file.getBytes();
            SignRequest<PDFSignContent> signRequest = signingVM.getDTO(PDFSignContent.class);
            signRequest.getSignElements().get(0).getContent().setFileData(fileData);
            PDFSigningDataRes signResponse = signService.signPDFFile(signRequest);
            ByteArrayResource resource = new ByteArrayResource(signResponse.getContent());
            asyncTransactionService.newThread("/api/sign/pdf", TransactionType.SIGNING, Method.POST,
                "200", "OK", AccountUtils.getLoggedAccount());
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName() + ".pdf")
                .contentLength(resource.contentLength()) //
                .body(resource);
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            asyncTransactionService.newThread("/api/sign/pdf", TransactionType.SIGNING, Method.POST,
                "400", applicationException.getMessage(), AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            asyncTransactionService.newThread("/api/sign/pdf", TransactionType.SIGNING, Method.POST,
                "400", e.getMessage(), AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        }
    }

    @PostMapping(value = "/hash")
    public ResponseEntity<BaseResponseVM> signHash(@RequestBody SigningVM<String> signingVM) {
        try {
            SignRequest<String> request = signingVM.getDTO(String.class);
            Object signingDataResponse = signService.signHash(request);
            asyncTransactionService.newThread("/api/sign/hash", TransactionType.SIGNING, Method.POST,
                "200", "OK", AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(signingDataResponse));
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            asyncTransactionService.newThread("/api/sign/hash", TransactionType.SIGNING, Method.POST,
                "400", applicationException.getMessage(), AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            asyncTransactionService.newThread("/api/sign/hash", TransactionType.SIGNING, Method.POST,
                "400", e.getMessage(), AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        }
    }

    @PostMapping(value = "/raw")
    public ResponseEntity<BaseResponseVM> signRaw(@RequestBody SigningVM<String> signingVM) {
        try {
            SignRequest<String> request = signingVM.getDTO(String.class);
            SignDataResponse<List<SignResultElement>> signResponse = signService.signRaw(request);
            asyncTransactionService.newThread("/api/sign/raw", TransactionType.SIGNING, Method.POST,
                "200", "OK", AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(signResponse));
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            asyncTransactionService.newThread("/api/sign/raw", TransactionType.SIGNING, Method.POST,
                "400", applicationException.getMessage(), AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            asyncTransactionService.newThread("/api/sign/raw", TransactionType.SIGNING, Method.POST,
                "400", e.getMessage(), AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        }
    }

    @PostMapping(path = "/getImage")
    public byte[] getImage(@RequestParam(required = false, name = "serial") String serial) {
        try {
            InputStream inputStream = new ClassPathResource("templates/signature/signature.html").getInputStream();
            HtmlImageGeneratorCustom imageGenerator = new HtmlImageGeneratorCustom();
            String htmlContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss Z", Locale.getDefault());
            Calendar cal = Calendar.getInstance();

//            String signer = "BV Nhi Đồng 1";
//            String address = "Quận 10, Thành phố Hồ Chí Minh";
//            String organization = "BV Nhi Đồng 1";
            htmlContent = htmlContent
//                .replaceFirst("signer", signer)
//                .replaceFirst("address", address)
//                .replaceFirst("organization", organization)
                .replaceFirst("timeSign", dateFormat.format(cal.getTime()));

            imageGenerator.loadHtml(htmlContent);
            // convert Image to byte
            BufferedImage originalImage = imageGenerator.getBufferedImage();
            ByteArrayOutputStream imageBytes = new ByteArrayOutputStream();

            ImageIO.write(originalImage, "png", imageBytes);
            imageBytes.flush();

            byte[] imageContentByte = imageBytes.toByteArray();
            imageBytes.close();
            return imageContentByte;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    @PostMapping(path = "/getImageBase64")
    public String getImageBase64(@RequestBody TokenVM tokenVM) {
        try {
            String CN = getSignInforBasedOnSerialAndPin(tokenVM.getSerial(), tokenVM.getPin());
            String htmlContent = putSignInformationToHTMLTemplate(CN);
            return convertHtmlContentToBase64(htmlContent);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }


    private String getSignInforBasedOnSerialAndPin(String serial, String pin) throws CertificateNotFoundAppException, CertificateAppException, CertificateException {
        CertificateDTO certificate = certificateService.getBySerial(serial);

        CryptoTokenProxy cryptoTokenProxy = null;
        try {
            cryptoTokenProxy = cryptoTokenProxyFactory.resolveCryptoTokenProxy(certificate, pin);
        } catch (CryptoTokenProxyException e) {
            throw new CertificateAppException("Certificate has error", e);
        } catch (ApplicationException e) {
            e.printStackTrace();
        }

        String contentInformation = cryptoTokenProxy.getX509Certificate().getSubjectDN().getName();
        //todo: hiện tại chỉ đang lấy pattern theo khách hàng Quốc Dũng như này còn khách hàng khác xử lý sau
        final String regex = "CN=\"([^\"]+)\"";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(contentInformation);
        String CN = null;
        while (matcher.find()) {
                CN = matcher.group(1);
        }
        return CN;
    }


    private String putSignInformationToHTMLTemplate(String CN) throws IOException {
        String[] signerAndAddress = CN.split(",");
        InputStream inputStream = new ClassPathResource("templates/signature/signature.html").getInputStream();
        HtmlImageGeneratorCustom imageGenerator = new HtmlImageGeneratorCustom();
        String htmlContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss, dd/MM/yyyy", Locale.getDefault());
        Calendar cal = Calendar.getInstance();

        Optional<UserEntity> userEntity = userApplicationService.getUserWithAuthoritiesByLogin(AccountUntils.getLoggedAccount());
        Long userId = userEntity.get().getId();
        Optional<String> signImage = signatureTemplateService.findOneWithUserId(userId).map(sign -> sign.getSignatureImage());

        htmlContent = htmlContent
            .replaceFirst("signer", signerAndAddress[0])
            .replaceFirst("address", signerAndAddress[1])
            .replaceFirst("signatureImage", signImage.orElse(""))
            .replaceFirst("timeSign", dateFormat.format(cal.getTime()));
        return htmlContent;
    }

    private String convertHtmlContentToBase64(String htmlContent) throws IOException {
        //Read it using Utf-8 - Based on encoding, change the encoding name if you know it
        InputStream htmlStream = new ByteArrayInputStream(htmlContent.getBytes("UTF-8"));
        Tidy tidy = new Tidy();
        org.w3c.dom.Document doc = tidy.parseDOM(new InputStreamReader(htmlStream, "UTF-8"), null);

        Java2DRenderer renderer = new Java2DRenderer(doc, 400, 150);
        BufferedImage img = renderer.getImage();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(img, "png", os);
        return Base64.getEncoder().encodeToString(os.toByteArray());
    }

    @PostMapping(value = "/office")
    public ResponseEntity<BaseResponseVM> signOffice(@RequestBody SigningRequest signingRequest) {
        try {
            SigningResponse signingDataResponse = officeSigningService.sign(signingRequest);
            asyncTransactionService.newThread("/api/sign/office", TransactionType.SIGNING, Method.POST,
                "200", "OK", AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(signingDataResponse));
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            asyncTransactionService.newThread("/api/sign/office", TransactionType.SIGNING, Method.POST,
                "400", applicationException.getMessage(), AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            asyncTransactionService.newThread("/api/sign/office", TransactionType.SIGNING, Method.POST,
                "400", e.getMessage(), AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        }
    }

}
