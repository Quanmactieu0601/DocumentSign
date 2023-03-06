package vn.easyca.signserver.core.dto.sign.easyinvoice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateBeanConfig {

    @Bean
    public RestTemplate RestTemplateBeanConfig(){
        return new RestTemplate();
    }

}
