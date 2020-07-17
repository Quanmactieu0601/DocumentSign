package vn.easyca.signserver.ca.service.api; /* created by truonglx  on 7/16/20 */

import vn.easyca.signserver.ca.service.Authenticate;
import vn.easyca.signserver.ca.service.api.dto.RegisterInputDto;
import vn.easyca.signserver.ca.service.api.dto.RegisterResultDto;
import vn.easyca.signserver.ca.service.network.PostRequester;
import vn.easyca.signserver.ca.service.network.Unauthorized;

import java.io.IOException;

public class RegisterCertificateApi {

    private String url ;
    private Authenticate authenticate;

    public RegisterCertificateApi(String url, Authenticate authenticate) {
        this.url = url;
        this.authenticate = authenticate;
    }

    public RegisterResultDto register(RegisterInputDto registerInputDto) throws IOException, Unauthorized {

        valid(registerInputDto);
        return post(0, registerInputDto);
    }

    private void valid(RegisterInputDto dto) {

    }

    private RegisterResultDto post(int tryCounter, RegisterInputDto dto) throws Unauthorized, IOException {
        if (tryCounter >= 2)
            throw new Unauthorized();
        String token = null;
        try {
            token = authenticate.getToken();
            PostRequester postRequester = new PostRequester(url,token);
            return postRequester.post(dto,RegisterResultDto.class);
        } catch (Unauthorized authenticateErr) {
            return post(tryCounter+1,dto);
        }
    }
}
