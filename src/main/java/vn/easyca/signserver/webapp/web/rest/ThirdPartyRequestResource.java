package vn.easyca.signserver.webapp.web.rest;

import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.easyca.signserver.core.services.ThirdPartyRequestService;

@Scope("request")
@RestController
@RequestMapping("/api/thirdPartyRequest")
public class ThirdPartyRequestResource extends BaseResource {
    private final ThirdPartyRequestService thirdPartyRequestService;

    public ThirdPartyRequestResource(ThirdPartyRequestService thirdPartyRequestService) {
        this.thirdPartyRequestService = thirdPartyRequestService;
    }



}
