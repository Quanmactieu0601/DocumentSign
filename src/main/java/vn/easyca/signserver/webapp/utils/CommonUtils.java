package vn.easyca.signserver.webapp.utils;

import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.core.utils.CertUtils;

import java.security.cert.X509Certificate;
import java.text.Normalizer;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

public class CommonUtils {
    public static String genRandomAlias() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String genRandomHsmCertPin() {
        int min = 100000;
        int max = 999999;
        Random random = new Random();
        int randomNumber =  random.nextInt((max - min) +  1) + min;
        return String.valueOf(randomNumber);
    }

    public static boolean isExpired(X509Certificate x509Certificate, Date signDate) throws ApplicationException {
        X509Certificate cert = null;
        cert = x509Certificate;
        Date notAfter = cert.getNotAfter();
        Date notBefore = cert.getNotBefore();
        if (signDate == null) signDate = new Date();
        return notAfter.before(signDate) || notBefore.after(signDate);
    }

    public static String getCN(X509Certificate cert) throws Exception {
        X500Name x500name = new JcaX509CertificateHolder(cert).getSubject();
        RDN cn = x500name.getRDNs(BCStyle.CN)[0];

        return IETFUtils.valueToString(cn.getFirst().getValue());
    }
}
