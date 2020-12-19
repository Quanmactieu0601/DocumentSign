package vn.easyca.signserver.webapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import vn.easyca.signserver.core.exception.ApplicationException;

import java.io.IOException;
import java.io.InputStream;

@Component
public class FileResourceService {
    @Autowired
    ResourceLoader resourceLoader;

    public Resource getFile(String filePath) {
        return resourceLoader.getResource(
            "classpath:" + filePath);
    }

    public InputStream getRootCer(String cerName) throws ApplicationException {
        try {
            String filePath = "root-cer/" + cerName + ".cer";
            return getFile(filePath).getInputStream();
        } catch (Exception ex) {
            throw new ApplicationException("Cannot read Root cer: " +  cerName, ex);
        }
    }

}
