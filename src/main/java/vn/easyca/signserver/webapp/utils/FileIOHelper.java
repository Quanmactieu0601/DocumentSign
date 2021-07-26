package vn.easyca.signserver.webapp.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileIOHelper {
    public static String readFile(String fileName)
        throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(fileName));
        return new String(encoded, StandardCharsets.UTF_8);
    }

    public static void writeFile(String content, String filePath) throws IOException {
        File fout = new File(filePath);
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        try {
            bw.write(content);
            bw.newLine();
        } finally {
            bw.close();
            fos.close();
        }
    }

    public static void writeFileLine(List<String> contents, String filePath) throws IOException {
        File fout = new File(filePath);
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        try {
            for (int i = 0; i < contents.size(); i++) {
                bw.write(contents.get(i));
                bw.newLine();
            }
        } finally {
            bw.close();
            fos.close();
        }
    }

    public static String getBase64EncodedImage(String imageURL) throws IOException {
        java.net.URL url = new java.net.URL(imageURL);
        InputStream is = url.openStream();
        try {
            byte[] bytes = org.apache.commons.io.IOUtils.toByteArray(is);
            return Base64.encodeBase64String(bytes);
        } finally {
            is.close();
        }
    }

    public static void createDirectory(String directory) {
        File dir = new File(directory);
        if (!dir.exists()) dir.mkdirs();
    }
}
