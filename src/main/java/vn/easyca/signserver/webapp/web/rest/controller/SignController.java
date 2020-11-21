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
import vn.easyca.signserver.core.domain.Certificate;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.core.exception.CertificateAppException;
import vn.easyca.signserver.core.model.CryptoTokenProxy;
import vn.easyca.signserver.core.model.CryptoTokenProxyException;
import vn.easyca.signserver.core.model.CryptoTokenProxyFactory;
import vn.easyca.signserver.core.services.CertificateService;
import vn.easyca.signserver.core.services.SigningService;
import vn.easyca.signserver.core.dto.sign.request.content.PDFSignContent;
import vn.easyca.signserver.core.dto.sign.request.SignRequest;
import vn.easyca.signserver.core.dto.sign.response.PDFSigningDataRes;
import vn.easyca.signserver.core.dto.sign.response.SignDataResponse;
import vn.easyca.signserver.core.dto.sign.response.SignResultElement;
import vn.easyca.signserver.core.utils.HtmlImageGeneratorCustom;
import vn.easyca.signserver.webapp.enm.TransactionType;
import vn.easyca.signserver.webapp.service.TransactionService;
import vn.easyca.signserver.webapp.service.dto.TransactionDTO;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.*;
import vn.easyca.signserver.webapp.web.rest.vm.response.BaseResponseVM;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RestController
@RequestMapping("/api/sign")
public class SignController {
    String code = null;
    String message = null;

    private final SigningService signService;
    private static final Logger log = LoggerFactory.getLogger(SignatureVerificationController.class);
    private final TransactionService transactionService;
    private final CertificateService certificateService;
    private final CryptoTokenProxyFactory cryptoTokenProxyFactory;
    public SignController(SigningService signService, TransactionService transactionService, CertificateService certificateService) {
        this.signService = signService;
        this.transactionService = transactionService;
        this.certificateService = certificateService;
        this.cryptoTokenProxyFactory = new CryptoTokenProxyFactory();
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
            message = applicationException.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            code = "400";
            message = e.getMessage();
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
            message = applicationException.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            code = "400";
            message = e.getMessage();
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
            message = applicationException.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            code = "400";
            message = e.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        } finally {
            transactionDTO.setCode(code);
            transactionDTO.setMessage(message);
            transactionService.save(transactionDTO);
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
    public String getImageBase64(@RequestParam(required = false, name = "serial") String serial, @RequestParam(required = false, name = "pin") String pin ) {
        try {

            Certificate certificate = certificateService.getBySerial(serial);
            CryptoTokenProxy cryptoTokenProxy = null;
            try {
                cryptoTokenProxy = cryptoTokenProxyFactory.resolveCryptoTokenProxy(certificate, pin);
            } catch (CryptoTokenProxyException e) {
                throw new CertificateAppException("Certificate has error", e);
            }

            String contentInformation = cryptoTokenProxy.getX509Certificate().getSubjectDN().getName();
            //todo: hiện tại chỉ đang lấy pattern theo khách hàng Quốc Dũng như này còn khách hàng khác xử lý sau
            final String regex = "CN=\"([^\"]+)\"";
            final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
            final Matcher matcher = pattern.matcher(contentInformation);

            String CN = null;
            while (matcher.find()) {
                System.out.println("Full match: " + matcher.group(0));
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    CN = matcher.group(i);
                }
            }

            String[] signerAndAddress = CN.split(",");
            InputStream inputStream = new ClassPathResource("templates/signature/signature.html").getInputStream();
            HtmlImageGeneratorCustom imageGenerator = new HtmlImageGeneratorCustom();
            String htmlContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss, dd/MM/yyyy", Locale.getDefault());
            Calendar cal = Calendar.getInstance();

            htmlContent = htmlContent
                .replaceFirst("signer", signerAndAddress[0])
                .replaceFirst("address", signerAndAddress[1])
                .replaceFirst("timeSign", dateFormat.format(cal.getTime()));

            //Read it using Utf-8 - Based on encoding, change the encoding name if you know it
            InputStream htmlStream = new ByteArrayInputStream(htmlContent.getBytes("UTF-8"));
            Tidy tidy = new Tidy();
            org.w3c.dom.Document doc = tidy.parseDOM(new InputStreamReader(htmlStream,"UTF-8"), null);

            Java2DRenderer renderer = new Java2DRenderer(doc, 400, 150);
            BufferedImage img = renderer.getImage();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(img, "png", os);
            return Base64.getEncoder().encodeToString(os.toByteArray());
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

}


