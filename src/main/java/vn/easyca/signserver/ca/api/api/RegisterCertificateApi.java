package vn.easyca.signserver.ca.api.api; /* created by truonglx  on 7/16/20 */

import vn.easyca.signserver.ca.api.Authenticate;
import vn.easyca.signserver.ca.api.api.dto.RegisterInputDto;
import vn.easyca.signserver.ca.api.api.dto.RegisterResultDto;
import vn.easyca.signserver.ca.api.network.PostRequester;
import vn.easyca.signserver.ca.api.network.RAUnauthorized;

import java.io.IOException;

public class RegisterCertificateApi {

    private String url ;
    private Authenticate authenticate;

    public RegisterCertificateApi(String url, Authenticate authenticate) {
        this.url = url;
        this.authenticate = authenticate;
    }

    public RegisterResultDto register(RegisterInputDto registerInputDto) throws IOException, RAUnauthorized {
        return post(0, registerInputDto);
    }

    private RegisterResultDto post(int tryCounter, RegisterInputDto dto) throws RAUnauthorized, IOException {
        if (tryCounter >= 2)
            throw new RAUnauthorized();
        String token = null;
        try {
            token = authenticate.getToken();
            PostRequester postRequester = new PostRequester(url,token);
            return postRequester.post(dto,RegisterResultDto.class);
        } catch (RAUnauthorized authenticateErr) {
            return post(tryCounter+1,dto);
        }
    }
}
