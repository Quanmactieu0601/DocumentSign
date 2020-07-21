package vn.easyca.signserver.test;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class Main {


    public static void main(String[] args) {

        String xml = "<Invoice><Content id=\"SigningData\"><Key>V6D3v7x2094730207484007</Key><ArisingDate>04/07/2020</ArisingDate><ComFax></ComFax><ComName>C\u00D4NG TY CP Y D\u01AF\u1EE2C QU\u1ED0C T\u1EBE VI\u1EC6T \u0110\u1EE8C</ComName><ComTaxCode>0107370684</ComTaxCode><ComAddress>S\u1ED1 84A, \u0111\u01B0\u1EDDng Hai B\u00E0 Tr\u01B0ng, Ph\u01B0\u1EDDng C\u1EEDa Nam, Qu\u1EADn Ho\u00E0n Ki\u1EBFm, Th\u00E0nh ph\u1ED1 H\u00E0 N\u1ED9i, Vi\u1EC7t Nam</ComAddress><ComPhone>02439369777</ComPhone><ComEmail></ComEmail><ComBankNo>21110009688668</ComBankNo><ComBankName>Ng\u00E2n h\u00E0ng BIDV - Chi Nh\u00E1nh H\u00E0 N\u1ED9i</ComBankName><Ikey></Ikey><ParentName></ParentName><InvoiceName>H\u00F3a \u0111\u01A1n gi\u00E1 tr\u1ECB gia t\u0103ng</InvoiceName><InvoicePattern>01GTKT0/001</InvoicePattern><SerialNo>VD/18E</SerialNo><InvoiceNo>328</InvoiceNo><PaymentMethod>T/M</PaymentMethod><CusCode></CusCode><CusName></CusName><CusTaxCode></CusTaxCode><CusPhone></CusPhone><CusAddress>S\u1ED1 27B H\u00E0n Thuy\u00EAn, Hai B\u00E0 Tr\u01B0ng, H\u00E0 N\u1ED9i</CusAddress><CusBankName></CusBankName><CusBankNo></CusBankNo><Total>2500000</Total><VATAmount>0</VATAmount><Amount>2500000</Amount><AmountInWords>Hai  tri\u1EC7u n\u0103m  tr\u0103m ngh\u00ECn \u0111\u1ED3ng.</AmountInWords><Buyer>Tr\u1ECBnh Ti\u1EBFn D\u0169ng</Buyer><VATRate>-1</VATRate><Note></Note><CusEmails></CusEmails><Extra /><Products><Product><Code></Code><ProdName>H\u00E0n composite R14,15,16,17,24,25,26</ProdName><ProdPrice>300000</ProdPrice><ProdQuantity>7</ProdQuantity><ProdType>1</ProdType><ProdUnit>chi\u1EBFc</ProdUnit><Extra></Extra><Total>2100000</Total><Amount>2100000</Amount><IsDiscountRow>0</IsDiscountRow></Product><Product><Code></Code><ProdName>H\u00E0n composite R27</ProdName><ProdPrice>400000</ProdPrice><ProdQuantity>1</ProdQuantity><ProdType>1</ProdType><ProdUnit>chi\u1EBFc</ProdUnit><Extra></Extra><Total>400000</Total><Amount>400000</Amount><IsDiscountRow>0</IsDiscountRow></Product></Products><GrossValue>2500000</GrossValue><GrossValue0>0</GrossValue0><VatAmount0>0</VatAmount0><GrossValue5>0</GrossValue5><VatAmount5>0</VatAmount5><GrossValue10>0</GrossValue10><VatAmount10>0</VatAmount10><GrossValueNDeclared>0</GrossValueNDeclared><VatAmountNDeclared>0</VatAmountNDeclared><GrossValueContractor>0</GrossValueContractor><VatAmountContractor>0</VatAmountContractor><ExchangeRate>0</ExchangeRate><CurrencyUnit>VND</CurrencyUnit><PortalLink>http://0107370684hd.easyinvoice.vn</PortalLink><Hidden>False</Hidden><SignDate>04/07/2020</SignDate></Content><RowPerPage>10</RowPerPage><qrCodeData>V6D3v7x2094730207484007|01GTKT0/001;VD/18E;0107370684;;328;2500000;0;04/07/2020 12:00:00 SA;3845</qrCodeData></Invoice>";
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        factory.setValidating(true);
//        factory.setExpandEntityReferences(false);
//        Document doc = null;
//        Node node = null;
//        try {
//            doc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
//            node = getContentSign(doc);
//        } catch (SAXException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ParserConfigurationException e) {
//            e.printStackTrace();
//        } catch (XPathExpressionException e) {
//            e.printStackTrace();
//        }
//        Element element = doc.getElementById("SigningData");
//        System.out.println(element != null);

        try {
            signXml();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Node getContentSign(Document doc) throws XPathExpressionException {

        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        return (Node) xpath.evaluate(String.format("//*[@Id=\"%s\"]", "SigningData"), doc, XPathConstants.NODE);
    }


    private static void signXml() throws Exception {

//
//        String xml = "<Invoice><Content Id=\"SigningData\"><Key>V6D3v7x2094730207484007</Key><ArisingDate>04/07/2020</ArisingDate><ComFax></ComFax><ComName>C\u00D4NG TY CP Y D\u01AF\u1EE2C QU\u1ED0C T\u1EBE VI\u1EC6T \u0110\u1EE8C</ComName><ComTaxCode>0107370684</ComTaxCode><ComAddress>S\u1ED1 84A, \u0111\u01B0\u1EDDng Hai B\u00E0 Tr\u01B0ng, Ph\u01B0\u1EDDng C\u1EEDa Nam, Qu\u1EADn Ho\u00E0n Ki\u1EBFm, Th\u00E0nh ph\u1ED1 H\u00E0 N\u1ED9i, Vi\u1EC7t Nam</ComAddress><ComPhone>02439369777</ComPhone><ComEmail></ComEmail><ComBankNo>21110009688668</ComBankNo><ComBankName>Ng\u00E2n h\u00E0ng BIDV - Chi Nh\u00E1nh H\u00E0 N\u1ED9i</ComBankName><Ikey></Ikey><ParentName></ParentName><InvoiceName>H\u00F3a \u0111\u01A1n gi\u00E1 tr\u1ECB gia t\u0103ng</InvoiceName><InvoicePattern>01GTKT0/001</InvoicePattern><SerialNo>VD/18E</SerialNo><InvoiceNo>328</InvoiceNo><PaymentMethod>T/M</PaymentMethod><CusCode></CusCode><CusName></CusName><CusTaxCode></CusTaxCode><CusPhone></CusPhone><CusAddress>S\u1ED1 27B H\u00E0n Thuy\u00EAn, Hai B\u00E0 Tr\u01B0ng, H\u00E0 N\u1ED9i</CusAddress><CusBankName></CusBankName><CusBankNo></CusBankNo><Total>2500000</Total><VATAmount>0</VATAmount><Amount>2500000</Amount><AmountInWords>Hai  tri\u1EC7u n\u0103m  tr\u0103m ngh\u00ECn \u0111\u1ED3ng.</AmountInWords><Buyer>Tr\u1ECBnh Ti\u1EBFn D\u0169ng</Buyer><VATRate>-1</VATRate><Note></Note><CusEmails></CusEmails><Extra /><Products><Product><Code></Code><ProdName>H\u00E0n composite R14,15,16,17,24,25,26</ProdName><ProdPrice>300000</ProdPrice><ProdQuantity>7</ProdQuantity><ProdType>1</ProdType><ProdUnit>chi\u1EBFc</ProdUnit><Extra></Extra><Total>2100000</Total><Amount>2100000</Amount><IsDiscountRow>0</IsDiscountRow></Product><Product><Code></Code><ProdName>H\u00E0n composite R27</ProdName><ProdPrice>400000</ProdPrice><ProdQuantity>1</ProdQuantity><ProdType>1</ProdType><ProdUnit>chi\u1EBFc</ProdUnit><Extra></Extra><Total>400000</Total><Amount>400000</Amount><IsDiscountRow>0</IsDiscountRow></Product></Products><GrossValue>2500000</GrossValue><GrossValue0>0</GrossValue0><VatAmount0>0</VatAmount0><GrossValue5>0</GrossValue5><VatAmount5>0</VatAmount5><GrossValue10>0</GrossValue10><VatAmount10>0</VatAmount10><GrossValueNDeclared>0</GrossValueNDeclared><VatAmountNDeclared>0</VatAmountNDeclared><GrossValueContractor>0</GrossValueContractor><VatAmountContractor>0</VatAmountContractor><ExchangeRate>0</ExchangeRate><CurrencyUnit>VND</CurrencyUnit><PortalLink>http://0107370684hd.easyinvoice.vn</PortalLink><Hidden>False</Hidden><SignDate>04/07/2020</SignDate></Content><RowPerPage>10</RowPerPage><qrCodeData>V6D3v7x2094730207484007|01GTKT0/001;VD/18E;0107370684;;328;2500000;0;04/07/2020 12:00:00 SA;3845</qrCodeData></Invoice>";
//        Certificate certificate = new Certificate();
//        certificate.setRawData("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");
//        certificate.setAlias("CÔNG TY CỔ PHẦN ĐẦU TƯ CÔNG NGHỆ VÀ THƯƠNG MẠI SOFTDREAMS's Viettel Group ID");
//        certificate.setSerial("540101091067a83a35d374e06688d3a7");
//        CryptoTokenProxy cryptoTokenProxy = new CryptoTokenProxy(certificate, "viettel-ca123");
//        XmlSigner xmlSigner = new XmlSigner(cryptoTokenProxy);
//        String res = xmlSigner.sign(new SignXMLRequest(xml));
//        System.out.println(res);
    }
}
