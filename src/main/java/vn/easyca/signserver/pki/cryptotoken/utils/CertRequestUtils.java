package vn.easyca.signserver.pki.cryptotoken.utils;

import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.asn1.pkcs.CertificationRequestInfo;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.X500NameStyle;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509NameTokenizer;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.BufferingContentSigner;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import javax.xml.bind.DatatypeConverter;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class CertRequestUtils {
    private final String BEGIN_CERTIFICATE_REQUEST = "-----BEGIN CERTIFICATE REQUEST-----";
    private final String END_CERTIFICATE_REQUEST = "-----END CERTIFICATE REQUEST-----";

    public String genCsr(String subjectDn, String providerName, PrivateKey privateKey, PublicKey publicKey, String signatureAlg, Boolean withHeaderAndFooter, Boolean ldapOder) throws Exception {
        if (signatureAlg == null || signatureAlg.isEmpty()) {
            signatureAlg = "SHA1withRSA";
        }
        X500Name x500Subject = stringToBcX500Name(subjectDn, ldapOder);
        if (x500Subject == null)
            throw new Exception("subjectDn string is not in right format");
        if (providerName == null || providerName.isEmpty()) {
            Security.addProvider(new BouncyCastleProvider());
            providerName = BouncyCastleProvider.PROVIDER_NAME;
        }
        SubjectPublicKeyInfo pkInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
        CertificationRequestInfo reqInfo = new CertificationRequestInfo(x500Subject, pkInfo, new DERSet());
        ContentSigner signer = new BufferingContentSigner(new JcaContentSignerBuilder(signatureAlg).setProvider(providerName).build(privateKey), 20480);
        signer.getOutputStream().write(reqInfo.getEncoded(ASN1Encoding.DER));
        signer.getOutputStream().flush();

        byte[] sig = signer.getSignature();
        DERBitString sigBits = new DERBitString(sig);
        CertificationRequest req = new CertificationRequest(reqInfo, signer.getAlgorithmIdentifier(), sigBits);
        PKCS10CertificationRequest pkcs10Req = new PKCS10CertificationRequest(req);
        String certReq = DatatypeConverter.printBase64Binary(pkcs10Req.getEncoded());
        final StringBuilder sb = new StringBuilder();
        if (withHeaderAndFooter) {
            sb.append(BEGIN_CERTIFICATE_REQUEST);
            sb.append("\n");
            sb.append(certReq);
            sb.append("\n");
            sb.append(END_CERTIFICATE_REQUEST);
            sb.append("\n");
        } else {
            sb.append(certReq);
        }
        return sb.toString();
    }

    private X500Name stringToBcX500Name(String dn, final boolean ldapOder) throws Exception {
        final X500NameStyle nameStyle = NameStyle.INSTANCE;
        final String[] order = null;
        final boolean applyLdapToCustomOrder = true;
        final X500Name x500Name = stringToUnorderedX500Name(dn, nameStyle);
        if (x500Name == null) {
            return null;
        }
        // -- Reorder fields
        return getOrderedX500Name(x500Name, ldapOder, order, applyLdapToCustomOrder, nameStyle);
    }

    private X500Name stringToUnorderedX500Name(String dn, final X500NameStyle nameStyle) throws Exception {
        if (dn == null) {
            return null;
        }
        // If the entire DN is quoted (which is strange but legacy), we just remove these quotes and carry on
        if (dn.length() > 2 && dn.charAt(0) == '"' && dn.charAt(dn.length() - 1) == '"') {
            dn = dn.substring(1, dn.length() - 1);
        }
        final X500NameBuilder nameBuilder = new X500NameBuilder(nameStyle);
        boolean quoted = false;
        boolean escapeNext = false;
        int currentStartPosition = -1;
        String currentPartName = null;
        for (int i = 0; i < dn.length(); i++) {
            final char current = dn.charAt(i);
            // Toggle quoting for every non-escaped "-char
            if (!escapeNext && current == '"') {
                quoted = !quoted;
            }
            // If there is an unescaped and unquoted =-char the proceeding chars is a part name
            if (currentStartPosition == -1 && !quoted && !escapeNext && current == '=' && 1 <= i) {
                // Trim spaces (e.g. "O =value")
                int endIndexOfPartName = i;
                while (endIndexOfPartName > 0 && dn.charAt(endIndexOfPartName - 1) == ' ') {
                    endIndexOfPartName--;
                }
                int startIndexOfPartName = endIndexOfPartName - 1;
                final String endOfPartNameSearchChars = ", +";
                while (startIndexOfPartName > 0 && (endOfPartNameSearchChars.indexOf(dn.charAt(startIndexOfPartName - 1)) == -1)) {
                    startIndexOfPartName--;
                }
                currentPartName = dn.substring(startIndexOfPartName, endIndexOfPartName);
                currentStartPosition = i + 1;
            }
            // When we have found a start marker, we need to be on the lookout for the ending marker
            if (currentStartPosition != -1 && ((!quoted && !escapeNext && (current == ',' || current == '+')) || i == dn.length() - 1)) {
                int endPosition = (i == dn.length() - 1) ? dn.length() - 1 : i - 1;
                // Remove white spaces from the end of the value
                while (endPosition > currentStartPosition && dn.charAt(endPosition) == ' ') {
                    endPosition--;
                }
                // Remove white spaces from the beginning of the value
                while (endPosition > currentStartPosition && dn.charAt(currentStartPosition) == ' ') {
                    currentStartPosition++;
                }
                // Only return the inner value if the part is quoted
                if (currentStartPosition < dn.length() && dn.charAt(currentStartPosition) == '"' && dn.charAt(endPosition) == '"') {
                    currentStartPosition++;
                    endPosition--;
                }
                String currentValue = dn.substring(currentStartPosition, endPosition + 1);
                // Unescape value (except escaped #) since the nameBuilder will double each escape
                currentValue = unescapeValue(new StringBuilder(currentValue)).toString();
                // -- First search the OID by name in declared OID's
                ASN1ObjectIdentifier oid = DnComponents.getOid(currentPartName);
                // -- If isn't declared, we try to create it
                if (oid == null) {
                    oid = new ASN1ObjectIdentifier(currentPartName);
                }
                nameBuilder.addRDN(oid, currentValue);

                // Reset markers
                currentStartPosition = -1;
                currentPartName = null;
            }
            if (escapeNext) {
                // This character was escaped, so don't escape the next one
                escapeNext = false;
            } else {
                if (!quoted && current == '\\') {
                    // This escape character is not escaped itself, so the next one should be
                    escapeNext = true;
                }
            }
        }
        return nameBuilder.build();
    }

    private X500Name getOrderedX500Name(final X500Name x500Name, boolean ldaporder, String[] order, final boolean applyLdapToCustomOrder, final X500NameStyle nameStyle) {
        // -- New order for the X509 Fields
        final List<ASN1ObjectIdentifier> newOrdering = new ArrayList<>();
        final List<ASN1Encodable> newValues = new ArrayList<ASN1Encodable>();
        // -- Add ordered fields
        final ASN1ObjectIdentifier[] allOids = x500Name.getAttributeTypes();

        // Guess order of the input name
        final boolean isLdapOrder = !isDNReversed(x500Name.toString());
        // If we think the DN is in LDAP order, first order it as a LDAP DN, if we don't think it's LDAP order
        // order it as a X.500 DN. If we haven't specified our own ordering
        final List<ASN1ObjectIdentifier> ordering;
        final boolean useCustomOrder = (order != null) && (order.length > 0);
        if (useCustomOrder) {
            ordering = getX509FieldOrder(order);
        } else {
            ordering = getX509FieldOrder(isLdapOrder);
        }

        final HashSet<ASN1ObjectIdentifier> hs = new HashSet<ASN1ObjectIdentifier>(allOids.length + ordering.size());
        for (final ASN1ObjectIdentifier oid : ordering) {
            if (!hs.contains(oid)) {
                hs.add(oid);
                final RDN[] valueList = x500Name.getRDNs(oid);
                // -- Only add the OID if has not null value
                for (final RDN value : valueList) {
                    newOrdering.add(oid);
                    newValues.add(value.getFirst().getValue());
                }
            }
        }
        // -- Add unexpected fields to the end
        for (final ASN1ObjectIdentifier oid : allOids) {
            if (!hs.contains(oid)) {
                hs.add(oid);
                final RDN[] valueList = x500Name.getRDNs(oid);
                // -- Only add the OID if has not null value
                for (final RDN value : valueList) {
                    newOrdering.add(oid);
                    newValues.add(value.getFirst().getValue());
                }
            }
        }
        // If the requested ordering was the reverse of the ordering the input string was in (by our guess in the beginning)
        // we have to reverse the vectors.
        // Unless we have specified a custom order, and choose to not apply LDAP Order to this custom order, in which case we will not change the order from the custom
        if ((useCustomOrder && applyLdapToCustomOrder) || !useCustomOrder) {
            if (ldaporder != isLdapOrder) {
                Collections.reverse(newOrdering);
                Collections.reverse(newValues);
            }
        }

        X500NameBuilder nameBuilder = new X500NameBuilder(nameStyle);
        for (int i = 0; i < newOrdering.size(); i++) {
            nameBuilder.addRDN(newOrdering.get(i), newValues.get(i));
        }
        // -- Return X500Name with the ordered fields
        return nameBuilder.build();
    }

    private boolean isDNReversed(String dn) {
        boolean ret = false;
        if (dn != null) {
            String first = null;
            String last = null;
            X509NameTokenizer xt = new X509NameTokenizer(dn);
            if (xt.hasMoreTokens()) {
                first = xt.nextToken().trim();
            }
            while (xt.hasMoreTokens()) {
                last = xt.nextToken().trim();
            }
            String[] dNObjects = DnComponents.getDnObjects(true);
            if ((first != null) && (last != null)) {
                // Be careful for bad input, that may not have any = sign in it
                final int fi = first.indexOf('=');
                first = first.substring(0, (fi != -1 ? fi : (first.length() - 1)));
                final int li = last.indexOf('=');
                last = last.substring(0, (li != -1 ? li : (last.length() - 1)));
                int firsti = 0, lasti = 0;
                for (int i = 0; i < dNObjects.length; i++) {
                    if (first.equalsIgnoreCase(dNObjects[i])) {
                        firsti = i;
                    }
                    if (last.equalsIgnoreCase(dNObjects[i])) {
                        lasti = i;
                    }
                }
                if (lasti < firsti) {
                    ret = true;
                }

            }
        }
        return ret;
    }

    private List<ASN1ObjectIdentifier> getX509FieldOrder(String[] order) {
        List<ASN1ObjectIdentifier> fieldOrder = new ArrayList<ASN1ObjectIdentifier>();
        for (final String dNObject : order) {
            fieldOrder.add(DnComponents.getOid(dNObject));
        }
        return fieldOrder;
    }

    private List<ASN1ObjectIdentifier> getX509FieldOrder(boolean ldaporder) {
        return getX509FieldOrder(DnComponents.getDnObjects(ldaporder));
    }

    private StringBuilder unescapeValue(final StringBuilder sb) {
        boolean esq = false;
        int index = 0;
        while (index < (sb.length() - 1)) {
            if (!esq && sb.charAt(index) == '\\' && sb.charAt(index + 1) != '#') {
                esq = true;
                sb.deleteCharAt(index);
            } else {
                esq = false;
                index++;
            }
        }
        return sb;
    }
}
