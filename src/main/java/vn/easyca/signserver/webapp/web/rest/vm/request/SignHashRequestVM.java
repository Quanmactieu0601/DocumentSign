package vn.easyca.signserver.webapp.web.rest.vm.request;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class SignHashRequestVM {


    private String base64Hash;

    private String serial;

    private String pin;

    private String hashAlgorithm;


}
