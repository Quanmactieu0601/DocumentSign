package vn.easyca.signserver.ca.api.api;

import vn.easyca.signserver.ca.api.Authenticate;
import vn.easyca.signserver.ca.api.api.dto.RegisterInputDto;
import vn.easyca.signserver.ca.api.api.dto.RegisterResultDto;
import vn.easyca.signserver.ca.api.network.PostRequester;
import vn.easyca.signserver.ca.api.network.RAUnauthorized;

public class RegisterCertificateApi {

    private final String url;
    private final Authenticate authenticate;

    public RegisterCertificateApi(String url, Authenticate authenticate) {
        this.url = url;
        this.authenticate = authenticate;
    }

    public RegisterResultDto register(RegisterInputDto registerInputDto) throws Exception {
        try {
            return post(0, registerInputDto);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Has err to connect ra service");
        }
    }

    private RegisterResultDto post(int tryCounter, RegisterInputDto dto) throws Exception {
        if (tryCounter >= 2)
            throw new RAUnauthorized();
        String token = null;
        try {
            token = authenticate.getToken();
            PostRequester postRequester = new PostRequester(url, token);
            return postRequester.post(dto, RegisterResultDto.class);
        } catch (RAUnauthorized authenticateErr) {
            return post(tryCounter + 1, dto);
        }
    }
}
