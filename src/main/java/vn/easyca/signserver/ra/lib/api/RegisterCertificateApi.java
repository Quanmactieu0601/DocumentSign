package vn.easyca.signserver.ra.lib.api;

import vn.easyca.signserver.ra.lib.authenticate.RAAuthenticate;
import vn.easyca.signserver.ra.lib.dto.RegisterInputDto;
import vn.easyca.signserver.ra.lib.dto.RegisterResultDto;
import vn.easyca.signserver.ra.lib.network.PostRequester;
import vn.easyca.signserver.ra.lib.exception.RAUnAuthorized;

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
            ex.printStackTrace();
            throw new Exception("Has err to connect ra service");
        }
    }

    private RegisterResultDto post(int tryingCounter, RegisterInputDto dto) throws Exception {
        try {
            String token = RAAuthenticate.getToken();
            PostRequester postRequester = new PostRequester(url, token);
            return postRequester.post(dto, RegisterResultDto.class);
        } catch (RAUnAuthorized authenticateErr) {
            tryingCounter++;
            if (tryingCounter >= 2)
                throw authenticateErr;
            return post(tryingCounter, dto);
        }
    }
}
