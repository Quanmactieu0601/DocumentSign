package vn.easyca.signserver.webapp.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileOIHelper {
    public static String readFile(String fileName)
        throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(fileName));
        return new String(encoded, StandardCharsets.UTF_8);
    }

    public static void writeFile(String content, String filePath) throws IOException {
        File fout = new File(filePath);
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        bw.write(content);
        bw.newLine();
        bw.close();
    }

    public static void writeFileLine(List<String> contents, String filePath) throws IOException {
        File fout = new File(filePath);
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        for (int i = 0; i < contents.size(); i++) {
            bw.write(contents.get(i));
            bw.newLine();
        }
        bw.close();
    }
}
