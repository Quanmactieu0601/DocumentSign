package vn.easyca.signserver.webapp.web.rest.vm.request;

import java.io.File;

public class ImageFileImportVM {
    private File successFile;
    private File[] imageFiles;

    public File getSuccessFile() {
        return successFile;
    }

    public void setSuccessFile(File successFile) {
        this.successFile = successFile;
    }

    public File[] getImageFiles() {
        return imageFiles;
    }

    public void setImageFiles(File[] imageFiles) {
        this.imageFiles = imageFiles;
    }

}
