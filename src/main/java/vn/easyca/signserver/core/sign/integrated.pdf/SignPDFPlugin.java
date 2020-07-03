package vn.easyca.signserver.core.sign.integrated.pdf;
import com.google.common.io.Files;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.easyca.signserver.core.sign.rawsign.DigestCreator;
import vn.easyca.signserver.core.sign.rawsign.RawSigner;
import vn.easyca.signserver.core.sign.utils.Base64Utils;
import vn.easyca.signserver.core.sign.utils.FileUtils;
import vn.easyca.signserver.core.sign.utils.UniqueID;


import java.io.*;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by chen on 7/25/17.
 */
public class SignPDFPlugin {
    //    private final Logger LOGGER = LoggerFactory.getLogger(SignPDFPlugin.class);
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void sign(byte[] content,
                     PrivateKey key,
                     Certificate[] chain,
                     JSONObject signatureInfo,
                     JSONObject signatureVisible,
                     String signField,
                     String hashAlg,
                     Date signDate,
                     String outPath) throws Exception {
        SignPDFRequest request = createHash(content, chain, signatureInfo, signatureVisible, signField, hashAlg, signDate);
        String sessionKey = request.getRequestId();
        String base64Hash = request.getB64Hash();
        byte[] hash = Base64Utils.base64Decode(base64Hash);
        //add info to hash
        hash = new DigestCreator().digestWithSHA1Info(hash);
        RawSigner rawSigner = new RawSigner();
        byte[] sig = rawSigner.signHash(hash, key);
        insertSignature(sessionKey, sig, signField, outPath);
    }

    public SignPDFRequest createHash(byte[] content, Certificate[] chain,
                                     JSONObject signature_info, JSONObject visible_signature, String signField, String hashAlg) throws Exception {
        // Ngày ký
        Date signDate = new Date();
        return createHash(content, chain, signature_info, visible_signature, signField, hashAlg, signDate);
    }

    public SignPDFRequest createHash(byte[] content, Certificate[] chain,
                                     JSONObject signature_info, JSONObject visible_signature, String signField, String hashAlg, Date signDate) throws Exception {
        // Tạo thư mục tạm
        Files.createTempDir();

        SignPDFLib signPDFLib = new SignPDFLib();
        signPDFLib.setHashAlg(hashAlg);

        // File pdf chứa chữ ký trống
        String key = UniqueID.generate();
        String tmpFilePath = getTempFolder() + key + ".pdf";

        List<byte[]> hashList = signPDFLib.createHash(content, tmpFilePath, chain, signature_info, visible_signature, signDate, signField);
        byte[] hash = (byte[])hashList.get(1);

        // Chứng thư ký
        byte[] certificate_chain = convertToBytes(chain);
        String base64Certificate = Base64.getEncoder().encodeToString(certificate_chain);

        String tmpJSONPath = getTempFolder() + key + ".json";
        String base64Hash = Base64.getEncoder().encodeToString(hash);
        String signDateString = formatter.format(signDate);
        String jsonString = new JSONObject()
                .put("base64Hash", base64Hash)
                .put("base64Certificate", base64Certificate)
                .put("signDateString", signDateString).toString();
        try (PrintWriter out = new PrintWriter(tmpJSONPath)) {
            out.println(jsonString);
        }
        return new SignPDFRequest(Base64Utils.base64Encode(hashList.get(0)), key);
    }

    public void insertSignature(String key, byte[] externalSig, String signField, String outFile) throws Exception {
        String tmpFilePath = getTempFolder() + key + ".pdf";

        // Lấy hash và ngày ký từ file tạm
        String tempJSONPath = getTempFolder() + key + ".json";
        JSONObject jsonObj;
        FileInputStream inputStream = new FileInputStream(tempJSONPath);
        try {
            String content = IOUtils.toString(inputStream);
            jsonObj = new JSONObject(content);
        } finally {
            inputStream.close();
        }
        byte[] hash = Base64.getDecoder().decode(jsonObj.getString("base64Hash"));
        Date signDate = formatter.parse(jsonObj.getString("signDateString"));

        // Lấy chứng thư số
        byte[] certificate_data = Base64.getDecoder().decode(jsonObj.getString("base64Certificate"));
        Certificate[] chain = (Certificate[]) convertFromBytes(certificate_data);

        SignPDFLib signPDFLib = new SignPDFLib();
        signPDFLib.insertSignature(tmpFilePath, outFile, hash, externalSig, chain, signDate, signField);

        // Xoá file tạm
        removeAllTemplateFile(key);
    }

    private String getTempFolder() {
        String tempDir = System.getProperty("java.io.tmpdir");
        return tempDir;
    }

    private void removeAllTemplateFile(String key) {
        try {
            FileUtils.deleteFile(getTempFolder() + key + ".pdf");
            FileUtils.deleteFile(getTempFolder() + key + ".json");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private byte[] convertToBytes(Object object) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            return bos.toByteArray();
        }
    }

    private Object convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        }
    }
}
