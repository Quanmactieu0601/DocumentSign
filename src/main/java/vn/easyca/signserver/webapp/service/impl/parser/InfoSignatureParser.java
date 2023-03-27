package vn.easyca.signserver.webapp.service.impl.parser;

import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.pki.sign.utils.StringUtils;
import vn.easyca.signserver.webapp.service.dto.InfoSignatureDTO;
import vn.easyca.signserver.webapp.utils.ParserUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InfoSignatureParser {
    final String regexCN = "CN=\"([^\"]+)|CN=([^,]+)";
    final String regexTitle = ", ?T=([^,]+)";
    final String regexEmail = ", ?(?:E|Email|EmailAddress)\\s*=\\s*[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+";
    final String regexOrg = ", ?O= ?([^,]+)";
    final String regexOrgU = ", ?OU= ?([^,]+)";
    final String regexState = ", ?ST= ?([^,]+)";
    final String regexLocation = ", ?L= ?([^,]+)";
    final String regexPhoneNumber = ", ?TelephoneNumber= ?([^,]+)";
    final String regexUID = "UID= ?([^,]+)";

    public InfoSignatureDTO getInfoSignature(String subjectDN) throws ApplicationException {
        try {
            if (StringUtils.isNullOrEmpty(subjectDN)) {
                return null;
            }
            String cn = getElementContent(subjectDN, regexCN)[0];
            String t = getElementContent(subjectDN, regexTitle)[0];
            String email = getElementContent(subjectDN, regexEmail)[0];
            String o = getElementContent(subjectDN, regexOrg)[0];
            String ou = getElementContent(subjectDN, regexOrgU)[0];
            String s = getElementContent(subjectDN, regexState)[0];
            String l = getElementContent(subjectDN, regexLocation)[0];
            String phone = getElementContent(subjectDN, regexPhoneNumber)[0];
            String[] UID = getElementContent(subjectDN, regexUID);
            String signerName = null;
            if (cn != null) {
                if (cn.contains(",")) {
                    String[] signerInfor = cn.split(",");
                    signerName = signerInfor[0];
                } else {
                    signerName = cn;
                }
            }

            InfoSignatureDTO infoSignatureDTO = new InfoSignatureDTO();
            infoSignatureDTO.setCn(signerName);
            infoSignatureDTO.setC("VN");
            infoSignatureDTO.setEmail(email);
            infoSignatureDTO.setL(l);
            infoSignatureDTO.setO(o);
            infoSignatureDTO.setOu(ou);
            infoSignatureDTO.setPhone(phone);
            infoSignatureDTO.setS(s);
            infoSignatureDTO.setT(t);
            for (String item : UID) {
                if (item == null)
                    break;
                if (item.contains("CMND")) {
                    infoSignatureDTO.setCmnd(item.split(":")[1]);
                }
                if (item.contains("MST")) {
                    infoSignatureDTO.setMst(item.split(":")[1]);
                }
            }
            return infoSignatureDTO;
        } catch (Exception ex) {
            throw new ApplicationException(String.format("Error when build template: subjectDN: %s -ex: %s", subjectDN, ex.getMessage()), ex);
        }
    }

    public String[] getElementContent(String contentInformation, String regex) throws ApplicationException {
        try {
            String[] info = new String[5];
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(contentInformation);
            int i = 0;
            while (matcher.find()) {
                info[i] = matcher.group();
                if (!StringUtils.isNullOrEmpty(info[i])) {
                    if (info[i].contains("=")) {
                        String[] formatInfo = info[i].split("=");
                        info[i] = formatInfo[1].replace("\"", "");
                    }
                }
                i++;
            }
            return info;
        } catch (Exception ex) {
            throw new ApplicationException(String.format("Parse info has error: [content: %s, regex: %s]", contentInformation, regex), ex);
        }
    }
}
