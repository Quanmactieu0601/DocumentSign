package vn.easyca.signserver.pki.sign.integrated.pdf.invisible;


import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.SignerInfoGeneratorBuilder;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.tsp.TSPException;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Optional;

public class Signature implements SignatureInterface {
    private PrivateKey privateKey;
    private Certificate[] certificateChain;
    private String algorithm;
    private String tsaUrl;
    private String provider;

    Signature(PrivateKey privateKey, Certificate[] certificateChain, String algorithm, String provider) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, IOException, CertificateNotYetValidException, CertificateExpiredException {
        this.certificateChain = certificateChain;
        this.privateKey = privateKey;
        this.algorithm = algorithm;
        this.provider = provider;
        Certificate certificate = this.certificateChain[0];
        if (certificate instanceof X509Certificate) {
            ((X509Certificate) certificate).checkValidity();
        }

    }

    @Override
    public byte[] sign(InputStream content) throws IOException {
        try {

            CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
            X509Certificate cert = (X509Certificate) this.certificateChain[0];
            JcaContentSignerBuilder contentSignerBuilder = new
                JcaContentSignerBuilder(algorithm);
            if (StringUtils.isNotBlank(provider))
                contentSignerBuilder.setProvider(provider);
            ContentSigner sha1Signer = contentSignerBuilder.build(this.privateKey);
            gen.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().build()).build(sha1Signer, cert));
            gen.addCertificates(new JcaCertStore(Arrays.asList(this.certificateChain)));
            CMSProcessableInputStream msg = new CMSProcessableInputStream(content);
            CMSSignedData signedData = gen.generate(msg, false);

            return signedData.getEncoded();
        } catch (GeneralSecurityException | CMSException | OperatorCreationException e) {
            //throw new IOException cause a SignatureInterface, but keep the stacktrace
            throw new IOException(e);
        }
    }

}
