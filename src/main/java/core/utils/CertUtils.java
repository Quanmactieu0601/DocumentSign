package core.utils;

import core.exception.ApplicationException;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

public class CertUtils {

    public static String encodeBase64X509(X509Certificate certificate) throws CertificateEncodingException {
        return Base64.getEncoder().encodeToString(certificate.getEncoded());
    }

    public static X509Certificate decodeBase64X509(String base64) throws ApplicationException {
        try {
            base64 = base64.replace("\n", "");
            byte[] encodedCert = Base64.getDecoder().decode(base64);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(encodedCert);
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            return (X509Certificate) certFactory.generateCertificate(inputStream);
        } catch (CertificateException e) {
            throw new ApplicationException("Could not init X509 Certificate from certificate base64 data", e);
        }
    }

    public static byte[] decodeBase64(String source) {
        return Base64.getDecoder().decode(source);
    }
    //    public static void main(String[] args) throws  Exception {
    //
    //        String cert = "MIIE4zCCA8ugAwIBAgIQVAEQABZvy3YY0CDUFWrNQjANBgkqhkiG9w0BAQsFADBq\n" +
    //            "MQswCQYDVQQGEwJWTjE5MDcGA1UECgwwU09GVERSRUFNUyBURUNITk9MT0dZIElO\n" +
    //            "VkVTVE1FTlQgQU5EIFRSQURJTkcgSlNDMQ8wDQYDVQQLDAZFQVNZQ0ExDzANBgNV\n" +
    //            "BAMMBkVBU1lDQTAeFw0yMDA4MDcwOTAyMzZaFw0yMTA4MDcwOTAyMzZaMHcxCzAJ\n" +
    //            "BgNVBAYTAlZOMRUwEwYDVQQIDAxI4bqjaSBQaMOybmcxETAPBgNVBAoMCHRydW9u\n" +
    //            "Z2x4MQswCQYDVQQLDAJJVDERMA8GA1UEAwwIdHJ1b25nbHgxHjAcBgoJkiaJk/Is\n" +
    //            "ZAEBDA5NU1Q6MTIxMjEyMTEyMTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoC\n" +
    //            "ggEBAMMGVx0RhGtqkIrp0QYo3KlwiDO3C6WSzRIaWWF8JIQd3Phqmit9WCh2i8V0\n" +
    //            "AHwNGBTcTv9VdXOsMoMNTCToVbpnM6X12ItIIu1Fp2fJEzrnxdyBy/9PGrZiE37p\n" +
    //            "+ks6AUtYyfz8pwS2nZYiCjwZ/Wi+EkiduIDL1BULtko9V6yMiuE+2BiV2WoGmuR+\n" +
    //            "VCQ6/BUh64WV9SlzGUyr5BSjF8wsFSw7/WA+UESZ1Sp4YaWTFmbeGK66Wd7aPCUb\n" +
    //            "frFSBt0wL11eq2celywpZI2UTLZB6Er8+b85XlzxM2Ixd1FWrhKetdGNmcBB5qeL\n" +
    //            "KgBB9gbvFOWJu2TWhuxZNUIvP3MCAwEAAaOCAXYwggFyMAwGA1UdEwEB/wQCMAAw\n" +
    //            "HwYDVR0jBBgwFoAUX7fnUhBQFmIy323Owq14SxHQEzYwNgYIKwYBBQUHAQEEKjAo\n" +
    //            "MCYGCCsGAQUFBzABhhpodHRwOi8vb2NzcC5lYXN5Y2Eudm4vb2NzcDA0BgNVHSUE\n" +
    //            "LTArBggrBgEFBQcDAgYIKwYBBQUHAwQGCisGAQQBgjcKAwwGCSqGSIb3LwEBBTCB\n" +
    //            "owYDVR0fBIGbMIGYMIGVoCOgIYYfaHR0cDovL2NybC5lYXN5Y2Eudm4vZWFzeWNh\n" +
    //            "LmNybKJupGwwajEPMA0GA1UEAwwGRUFTWUNBMQ8wDQYDVQQLDAZFQVNZQ0ExOTA3\n" +
    //            "BgNVBAoMMFNPRlREUkVBTVMgVEVDSE5PTE9HWSBJTlZFU1RNRU5UIEFORCBUUkFE\n" +
    //            "SU5HIEpTQzELMAkGA1UEBhMCVk4wHQYDVR0OBBYEFCOXb9m1En/eKYxiAE/S3g6u\n" +
    //            "vuHoMA4GA1UdDwEB/wQEAwIE8DANBgkqhkiG9w0BAQsFAAOCAQEAWaUbXaGW0b4B\n" +
    //            "UpfONR6NnHGBu243NfBfGYmrYNnme0WNMSKnW9/j8aoG7+4vgywVUU/R79TktNz7\n" +
    //            "Qeem9zGXBcdDtPoTURrrjL0z0c5AWkSjgoijdfbAVdDP7bHr4G/OKDPC+8qmcFbA\n" +
    //            "3GIj21Hx/NHLqfTokKkkhtcNF2X/Pq0adzWaJkTi+3Cpnpny0/QKG9EZUvk29lNl\n" +
    //            "XVtTrzwDJAwIkEHGEk710vpl/jxSeKxuZ7xTmB0gBr2jKkR4i9a/4c16TwRlJ8sV\n" +
    //            "AcgFnjB8L4xKfHOk7ZAX3Eijxv8+xsQHRJn+2XY1FbC2eHdd5EJXvQcQuo0KQqwp\n" +
    //            "9ihVarIBCw==";
    //
    //        cert = cert.replace("\n","");
    //    }
}
