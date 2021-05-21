package vn.easyca.signserver.webapp.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import vn.easyca.signserver.core.exception.ApplicationException;

import java.io.InputStream;

@Component
public class FileResourceService {
    public static String ROOT_CA = "caroot.cer";
    public static String EASY_CA = "easyca.cer";

    private final ResourceLoader resourceLoader;

    public FileResourceService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public Resource getFile(String filePath) {
        return resourceLoader.getResource(
            "classpath:" + filePath);
    }

    public InputStream getRootCer(String cerName) throws ApplicationException {
        try {
            String filePath = "root-cer/" + cerName;
            return getFile(filePath).getInputStream();
        } catch (Exception ex) {
            throw new ApplicationException("Cannot read Root cer: " +  cerName, ex);
        }
    }

    public InputStream getTemplateFile(String filePath) throws ApplicationException {
        try {
            return getFile(filePath).getInputStream();
        } catch (Exception ex) {
            throw new ApplicationException("Cannot read Root cer: " +  filePath, ex);
        }
    }

}
