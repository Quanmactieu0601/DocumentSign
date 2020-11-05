package vn.easyca.signserver.webapp.web.rest.controller;

import gui.ava.html.image.generator.HtmlImageGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
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
import vn.easyca.signserver.webapp.utils.DateTimeUtils;
import vn.easyca.signserver.webapp.utils.FileOIHelper;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.*;
import vn.easyca.signserver.webapp.web.rest.vm.response.BaseResponseVM;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ImageView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

@RestController
@RequestMapping("/api/sign")
public class SignController {

    private final SigningService signService;
    private static final Logger log = LoggerFactory.getLogger(SignatureVerificationController.class);

    public SignController(SigningService signService) {
        this.signService = signService;
    }

    @PostMapping(value = "/pdf", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Object> signPDF(@RequestParam MultipartFile file, SigningVM<PDFSigningContentVM> signingVM) {
        try {
            byte[] fileData = file.getBytes();
            SignRequest<PDFSignContent> signRequest = signingVM.getDTO(PDFSignContent.class);
            signRequest.getSignElements().get(0).getContent().setFileData(fileData);
            PDFSigningDataRes signResponse = signService.signPDFFile(signRequest);
            ByteArrayResource resource = new ByteArrayResource(signResponse.getContent());
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName() + ".pdf")
                .contentLength(resource.contentLength()) //
                .body(resource);
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        }
    }

    @PostMapping(value = "/hash")
    public ResponseEntity<BaseResponseVM> signHash(@RequestBody SigningVM<String> signingVM) {
        try {
            SignRequest<String> request = signingVM.getDTO(String.class);
            Object signingDataResponse = signService.signHash(request);
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(signingDataResponse));
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        }
    }

    @PostMapping(value = "/raw")
    public ResponseEntity<BaseResponseVM> signRaw(@RequestBody SigningVM<String> signingVM) {
        try {
            SignRequest<String> request = signingVM.getDTO(String.class);
            SignDataResponse<List<SignResultElement>> signResponse = signService.signRaw(request);
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(signResponse));
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        }
    }

    @PostMapping(path = "/getImage")
    public ResponseEntity<Resource> getImage(@RequestParam(required = false, name = "signer") String signer, @RequestParam(required = false, name = "emailAddress") String emailAddress, @RequestParam(required = false, name = "organization") String organization) {
        String filename = "EasyCA-CSR-Export" + DateTimeUtils.getCurrentTimeStamp() + ".xlsx";
        try {
            HtmlImageGeneratorCustom imageGenerator = new HtmlImageGeneratorCustom();
            final String templatePath = System.getProperty("user.dir") + "/src/main/resources/templates/signature/signature.html";
            String htmlContent = FileOIHelper.readFile(templatePath);

            DateFormat dateFormat = new SimpleDateFormat("dd/MM/dd HH:mm:ss Z", Locale.getDefault());
            Calendar cal = Calendar.getInstance();

            htmlContent = htmlContent.replaceFirst("signer", signer)
                .replaceFirst("emailAddress", emailAddress)
                .replaceFirst("organization", organization)
                .replaceFirst("emailAddress", emailAddress)
                .replaceFirst("timeSign", dateFormat.format(cal.getTime()));

            imageGenerator.loadHtml(htmlContent);
//            imageGenerator.saveAsImage("D:\\hello-world.png");
            // convert Image to byte
            BufferedImage originalImage = imageGenerator.getBufferedImage();
            ByteArrayOutputStream imageBytes = new ByteArrayOutputStream();

            ImageIO.write(originalImage, "jpg", imageBytes);
            imageBytes.flush();

            byte[] imageContentByte = imageBytes.toByteArray();
            imageBytes.close();

            InputStreamResource file = new InputStreamResource(new ByteArrayInputStream(imageContentByte));
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }
}

class CustomToolKit extends HTMLEditorKit {

    private static HTMLFactory factory = null;

    @Override
    public ViewFactory getViewFactory() {
        if (factory == null) {
            factory = new HTMLFactory() {

                @Override
                public View create(Element elem) {
                    AttributeSet attrs = elem.getAttributes();
                    Object elementName = attrs.getAttribute(AbstractDocument.ElementNameAttribute);
                    Object o = (elementName != null) ? null : attrs.getAttribute(StyleConstants.NameAttribute);
                    if (o instanceof HTML.Tag) {
                        HTML.Tag kind = (HTML.Tag) o;
                        if (kind == HTML.Tag.IMG) {
                            return new BASE64ImageView(elem);
                        }
                    }
                    return super.create(elem);
                }
            };
        }
        return factory;
    }

}

class BASE64ImageView extends ImageView {

    private URL url;

    public BASE64ImageView(Element elmnt) {
        super(elmnt);
        populateImage();
    }

    private void populateImage() {
        Dictionary<URL, Image> cache = (Dictionary<URL, Image>) getDocument()
            .getProperty("imageCache");
        if (cache == null) {
            cache = new Hashtable<>();
            getDocument().putProperty("imageCache", cache);
        }

        URL src = getImageURL();
        cache.put(src, loadImage());

    }

    private Image loadImage() {
        String b64 = getBASE64Image();
        BufferedImage newImage = null;
        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(
                Base64.getDecoder().decode(b64.getBytes()));
            newImage = ImageIO.read(bais);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return newImage;
    }

    @Override
    public URL getImageURL() {
        String src = (String) getElement().getAttributes()
            .getAttribute(HTML.Attribute.SRC);
        if (isBase64Encoded(src)) {

            this.url = BASE64ImageView.class.getProtectionDomain()
                .getCodeSource().getLocation();

            return this.url;
        }
        return super.getImageURL();
    }

    private boolean isBase64Encoded(String src) {
        return src != null && src.contains("base64,");
    }

    private String getBASE64Image() {
        String src = (String) getElement().getAttributes()
            .getAttribute(HTML.Attribute.SRC);
        if (!isBase64Encoded(src)) {
            return null;
        }
        return src.substring(src.indexOf("base64,") + 7, src.length() - 1);
    }

}

class HtmlImageGeneratorCustom extends HtmlImageGenerator {
    @Override
    protected JEditorPane createJEditorPane() {
        JEditorPane edit = super.createJEditorPane();
        edit.setEditable(false);
        CustomToolKit toolKit = new CustomToolKit();
        edit.setEditorKit(toolKit);
        return edit;
    }
}
