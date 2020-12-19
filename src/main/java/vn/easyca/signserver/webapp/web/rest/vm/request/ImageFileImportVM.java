package vn.easyca.signserver.webapp.web.rest.vm.request;

import java.io.File;

public class ImageFileImportVM {
    private File fileSuccess;
    private File[] imageFiles;

    public File getFileSuccess() {
        return fileSuccess;
    }

    public void setFileSuccess(File fileSuccess) {
        this.fileSuccess = fileSuccess;
    }

    public File[] getImageFiles() {
        return imageFiles;
    }

    public void setImageFiles(File[] imageFiles) {
        this.imageFiles = imageFiles;
    }
}
