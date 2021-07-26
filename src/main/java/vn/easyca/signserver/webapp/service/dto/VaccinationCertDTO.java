package vn.easyca.signserver.webapp.service.dto;

import com.itextpdf.text.BadElementException;
import jdk.nashorn.internal.ir.ReturnNode;
import org.apache.commons.io.IOUtils;
import org.postgresql.util.StreamWrapper;
import org.springframework.core.env.Environment;
import vn.easyca.signserver.core.domain.TokenInfo;
import vn.easyca.signserver.core.dto.sign.TokenInfoDTO;
import vn.easyca.signserver.core.dto.sign.newrequest.ExtraInfo;
import vn.easyca.signserver.core.dto.sign.newrequest.Location;
import vn.easyca.signserver.core.dto.sign.newrequest.SigningRequest;
import vn.easyca.signserver.core.dto.sign.newrequest.VisibleRequestContent;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.webapp.service.FileResourceService;
import vn.easyca.signserver.webapp.utils.ParserUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

public class VaccinationCertDTO {
    private byte[] confirmContentFile;
    private String serial;
    private String pin;

    /**
     * 0: VACCINE CERT FILE
     * 1: PRC TEST FILE
     */
    private int fileType;

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public byte[] getConfirmContentFile() {
        return confirmContentFile;
    }

    public void setConfirmContentFile(byte[] contentFile) {
        confirmContentFile = contentFile;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public SigningRequest<VisibleRequestContent> createSigningRequest(FileResourceService fileResourceService, Environment env) throws ApplicationException {
        SigningRequest<VisibleRequestContent> signingRequest = new SigningRequest<>();
        List<VisibleRequestContent> visibleRequestContentList = new ArrayList<VisibleRequestContent>();
        VisibleRequestContent visibleRequestContent = new VisibleRequestContent();

        String htmlContentImage = getSignatureImage(fileResourceService);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String fullDate = formatter.format(date) + " +07'00'";
        htmlContentImage = htmlContentImage.replace("Time", fullDate);


        Location location = new Location();
        if (fileType == 0) {
            String contentImage = ParserUtils.convertHtmlContentToImageByProversion(htmlContentImage, 703, 278, false, env);
            visibleRequestContent.setImageSignature(contentImage);
            location.setVisibleX(230);
            location.setVisibleY(105);
            location.setVisibleHeight(130);
            location.setVisibleWidth(305);
        } else if (fileType == 1) {
            String contentImage = ParserUtils.convertHtmlContentToImageByProversion(htmlContentImage, 1200, 410, false, env);
            visibleRequestContent.setImageSignature(contentImage);
            location.setVisibleX(180);
            location.setVisibleY(70);
            location.setVisibleWidth(395);
            location.setVisibleHeight(150);
        }

        visibleRequestContent.setLocation(location);
        visibleRequestContent.setExtraInfo(new ExtraInfo());
        visibleRequestContent.setDocumentName("Verified_File");
        visibleRequestContent.setData(confirmContentFile);
        visibleRequestContentList.add(visibleRequestContent);

        signingRequest.setSigningRequestContents(visibleRequestContentList);
        TokenInfoDTO hospitalToken = new TokenInfoDTO();
        hospitalToken.setSerial(serial);
        hospitalToken.setPin(pin);
        signingRequest.setTokenInfo(hospitalToken);

        return signingRequest;
    }

    private String getSignatureImage(FileResourceService fileResourceService) throws ApplicationException {
        String signatureImagePath = fileType == 0 ? "/templates/signature/vaccinationCertImageHtml.html" : "/templates/signature/prctest-vaccinationCertImage.html";
        try {
            InputStream inputStream = fileResourceService.getTemplateFile(signatureImagePath);
            byte[] fileContent = IOUtils.toByteArray(inputStream);
            inputStream.close();
            return new String(fileContent);
        } catch (IOException ex) {
            throw new ApplicationException("Read File vaccinationCertImageHtml Error" + ex.getMessage());
        }
    }


}
