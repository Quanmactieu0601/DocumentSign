package vn.easyca.signserver.webapp.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;

@Configuration
@PropertySource(value = "classpath:hsm-config.properties")
public class HsmConfig {

    @Value("${hsm-type}")
    private String type;

    @Value("${name}")
    private String name;

    @Value("${library}")
    private String library;

    @Value("${slot}")
    private String slot;

    @Value("${attributes}")
    private String attributes;

    @Value("${pin}")
    private String modulePin;

    private String pkcs11Config;

    @PostConstruct
    public void init(){
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotEmpty(name))
            builder.append("name = ").append(this.name).append("\n");
        if (StringUtils.isNotEmpty(library))
            builder.append("library = ").append(this.library).append("\n");
        if (StringUtils.isNotEmpty(slot))
            builder.append("slot = ").append(this.slot).append("\n");
        if (StringUtils.isNotEmpty(attributes))
            builder.append("attributes = ").append(this.attributes).append("\n");
        this.pkcs11Config = builder.toString();
    }

    public String getPkcs11Config() {
        return pkcs11Config;
    }

    public String getAttributes() {
        return attributes;
    }

    public String getModulePin() {
        return modulePin;
    }

    public String getLibrary() {
        return library;
    }

    public String getSlot() {
        return slot;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
