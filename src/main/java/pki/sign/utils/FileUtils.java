package pki.sign.utils;

import java.io.File;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;

/**
 * Created by chen on 7/21/17.
 */
public class FileUtils {

    public static byte[] getFileAsBytes(String filePath) throws Exception {
        return org.apache.commons.io.FileUtils.readFileToByteArray(new File(filePath));
    }

    public static void deleteFile(String filePath) throws Exception {
        org.apache.commons.io.FileUtils.forceDelete(new File(filePath));
    }

    public static InputStream getFileFromResource(String fileName) throws Exception {
        ClassLoader classLoader = FileUtils.class.getClassLoader();
        return classLoader.getResourceAsStream(fileName);
    }

    public static byte[] getFileFromResourceInBytes(String fileName) throws Exception {
        return IOUtils.toByteArray(getFileFromResource(fileName));
    }

    public static void writeBytesToFile(byte[] data, String fileName) throws Exception {
        org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(fileName), data);
    }
}
