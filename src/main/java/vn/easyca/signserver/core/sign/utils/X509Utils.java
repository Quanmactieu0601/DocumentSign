package vn.easyca.signserver.core.sign.utils;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.x509.extension.X509ExtensionUtil;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by chen on 7/26/17.
 */
public class X509Utils {
    private static final Logger LOGGER = Logger.getLogger(X509Utils.class.getName());
    private static final String CA_CONFIG = "CA_CONFIG";
    public static void writeChainToPem(Certificate[] chain, String outPath) throws Exception {

    }
    public static List<X509Certificate> buildChain(String xEndUser)
    {
        return buildChain(StringToX509Certificate(xEndUser));
    }

    public static List<X509Certificate> buildChain(X509Certificate xEndUser)
    {
        List<X509Certificate> chain = new ArrayList<>();
        chain.add(xEndUser);
        try
        {
            X509Certificate xIssuerCert = getIssuerCert(xEndUser);
            if(xIssuerCert == null)
            {
                return chain;
            }
            chain.add(xIssuerCert);
            X509Certificate xRootCACert = getIssuerCert(xIssuerCert);
            if(xRootCACert == null)
            {
                return chain;
            }
            chain.add(xRootCACert);
        }
        catch (Exception ex)
        {
            Logger.getLogger(X509Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return chain;
    }
    public static X509Certificate getIssuerCert(X509Certificate cert) throws Exception
    {
        try
        {
            String caConfig = System.getenv(CA_CONFIG);
            // get from file
            if (caConfig != null) {
                LOGGER.log(Level.INFO, "Get issuer cert from config file");
                String[] issuerName = cert.getIssuerDN().getName().split(",");
                String name = null;
                for (String ob : issuerName) {
                    if (ob.contains("CN=")) {
                        name = ob.trim().substring(ob.trim().lastIndexOf("CN=") + 3);
                        break;
                    }
                }
                if (name == null)
                {
                    return null;
                }
                PropertiesConfiguration cerConfig = null;
                Path path = Paths.get(caConfig, "config.properties");
                File f = new File(path.toString());
                if(f.exists() && !f.isDirectory()) {
                    cerConfig = new PropertiesConfiguration(path.toString());
                }
                else
                {
                    LOGGER.log(Level.SEVERE, "Not found ca config properties file");
                    return null;
                }

                if (cerConfig != null)
                {
                    String certIsser = cerConfig.getString(name.replace(" ", "_"));

                    if (certIsser != null) {
                        path = Paths.get(caConfig, certIsser);
                        File fCertIssuer = new File(path.toString());
                        if (fCertIssuer.exists() && !fCertIssuer.isDirectory()) {
                            X509Certificate xIssuerCert = getCerFromCerFile(path.toString());
                            return xIssuerCert;
                        }
                    }
                }
            }
            else
            {
                LOGGER.log(Level.SEVERE, "Not found ca config variable environment");
            }
        }
        catch (Exception ex)
        {
            Logger.getLogger(X509Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public static X509Certificate getCerFromCerFile(String filename) {
        FileInputStream is = null;
        try {
            is = new FileInputStream(filename);
            CertificateFactory cf = CertificateFactory.getInstance("X509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(is);
            return cert;
        } catch (CertificateException ex) {
            Logger.getLogger(X509Utils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(X509Utils.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                Logger.getLogger(X509Utils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
    public static X509Certificate StringToX509Certificate(String cer) {
        X509Certificate certificate = null;
        try {
            byte[] cerbytes = Base64Utils.base64Decode(cer);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            certificate = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(cerbytes));
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage());
        }
        return certificate;
    }

    public static String getOcspUrl(X509Certificate certificate) {
        String urlOcsp = null;
        byte[] value1 = certificate.getExtensionValue(X509Extensions.AuthorityInfoAccess.getId());
        if (value1 != null) {
            if (value1.length > 0) {
                try {
                    byte[] extensionValue = certificate.getExtensionValue(X509Extensions.AuthorityInfoAccess.getId());
                    ASN1Primitive fromExtensionValue = X509ExtensionUtil
                            .fromExtensionValue(extensionValue);
                    ASN1Sequence asn1Seq = (ASN1Sequence) X509ExtensionUtil.fromExtensionValue(extensionValue); // AuthorityInfoAccessSyntax
                    Enumeration<?> objects = asn1Seq.getObjects();

                    while (objects.hasMoreElements()) {
                        ASN1Sequence obj = (ASN1Sequence) objects.nextElement(); // AccessDescription
                        DERObjectIdentifier oid = (DERObjectIdentifier) obj.getObjectAt(0); // accessMethod
                        DERTaggedObject location = (DERTaggedObject) obj.getObjectAt(1); // accessLocation

                        if (location.getTagNo() == GeneralName.uniformResourceIdentifier) {
                            DEROctetString uri = (DEROctetString) location.getObject();
                            String str = new String(uri.getOctets());
                            if (oid.equals(X509ObjectIdentifiers.id_ad_ocsp)) {
                                urlOcsp = str;
                            }
                        }
                    }
                } catch (Exception e) {
                    urlOcsp = null;
                }
            }
        }
        return urlOcsp;
    }

    private static ASN1Primitive getExtensionValue(X509Certificate certificate, String oid) throws IOException {
        byte[] bytes = certificate.getExtensionValue(oid);
        if (bytes == null) {
            return null;
        }
        ASN1InputStream aIn = new ASN1InputStream(new ByteArrayInputStream(bytes));
        ASN1OctetString octs = (ASN1OctetString) aIn.readObject();
        aIn = new ASN1InputStream(new ByteArrayInputStream(octs.getOctets()));
        return aIn.readObject();
    }

    public static String getCRLURL(X509Certificate certificate) throws CertificateParsingException {
        ASN1Primitive obj;
        try {
            obj = getExtensionValue(certificate, Extension.cRLDistributionPoints.getId());
        } catch (IOException e) {
            obj = null;
        }
        if (obj == null) {
            return null;
        }
        CRLDistPoint dist = CRLDistPoint.getInstance(obj);
        DistributionPoint[] dists = dist.getDistributionPoints();
        for (DistributionPoint p : dists) {
            DistributionPointName distributionPointName = p.getDistributionPoint();
            if (DistributionPointName.FULL_NAME != distributionPointName.getType()) {
                continue;
            }
            GeneralNames generalNames = (GeneralNames) distributionPointName.getName();
            GeneralName[] names = generalNames.getNames();
            for (GeneralName name : names) {
                if (name.getTagNo() != GeneralName.uniformResourceIdentifier) {
                    continue;
                }
                DERIA5String derStr = DERIA5String.getInstance((ASN1TaggedObject) name.toASN1Primitive(), false);
                return derStr.getString();
            }
        }
        return null;
    }

    public static boolean checkValidTime(X509Certificate cert) throws Exception {
        if (cert == null) {
            throw new Exception("Can not get certificate info");
        }
        Date dateTime = new Date();
        return dateTime.after(cert.getNotBefore()) && dateTime.before(cert.getNotAfter());
    }

    public static boolean checkValidTime(String certBase64) throws Exception {
        X509Certificate cert = StringToX509Certificate(certBase64);
        return checkValidTime(cert);
    }
}
