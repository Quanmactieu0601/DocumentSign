package ra.lib.api;

import java.util.List;
import ra.lib.authenticate.RAAuthenticate;
import ra.lib.dto.RegisterInputDto;
import ra.lib.dto.RegisterResultDto;
import ra.lib.exception.RAUnAuthorized;
import ra.lib.network.PostRequester;

public class RegisterCertificateApi {

    private final String url;
    private final RAAuthenticate RAAuthenticate;

    public RegisterCertificateApi(String url, RAAuthenticate RAAuthenticate) {
        this.url = url;
        this.RAAuthenticate = RAAuthenticate;
    }

    public RegisterResultDto register(RegisterInputDto registerInputDto) throws Exception {
        try {
            return post(0, registerInputDto);
        } catch (Exception ex) {
            throw new Exception("Has err to connect ra service", ex);
        }
    }

    public List<RegisterResultDto> registerMultipleCertificates(List<RegisterInputDto> registerInputDtos) throws Exception {
        try {
            List<RegisterResultDto> result = postToGetList(0, registerInputDtos);
            return result;
        } catch (Exception ex) {
            throw new Exception("Has err to connect ra service", ex);
        }
    }

    private RegisterResultDto post(int tryingCounter, RegisterInputDto dto) throws Exception {
        try {
            String token = RAAuthenticate.getToken();
            PostRequester postRequester = new PostRequester(url, token);
            return postRequester.post(dto, RegisterResultDto.class);
        } catch (RAUnAuthorized authenticateErr) {
            tryingCounter++;
            if (tryingCounter >= 2) throw authenticateErr;
            return post(tryingCounter, dto);
        }
    }

    private List<RegisterResultDto> postToGetList(int tryingCounter, Object dto) throws Exception {
        try {
            String token = RAAuthenticate.getToken();
            PostRequester postRequester = new PostRequester(url, token);
            return (List<RegisterResultDto>) postRequester.postToGetListData(dto, RegisterResultDto.class);
        } catch (RAUnAuthorized authenticateErr) {
            tryingCounter++;
            if (tryingCounter >= 2) throw authenticateErr;
            return postToGetList(tryingCounter, dto);
        }
    }
}
