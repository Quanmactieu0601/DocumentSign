package vn.easyca.signserver.webapp.utils;

import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import vn.easyca.signserver.core.dto.CertDTO;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.webapp.service.FileResourceService;
import vn.easyca.signserver.webapp.service.dto.CertRequestInfoDTO;
import vn.easyca.signserver.webapp.service.dto.UserDTO;

import java.io.*;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class ExcelUtils {
    private final FileResourceService fileResourceService;

    public ExcelUtils(FileResourceService fileResourceService) {
        this.fileResourceService = fileResourceService;
    }

    public byte[] exportCsrFileFormat1(List<CertDTO> dtos) throws IOException {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("EasyCA - CSR Result");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("STT");
        headerRow.createCell(1).setCellValue("OwnerID");
        headerRow.createCell(2).setCellValue("CSR VALUE");
        headerRow.createCell(3).setCellValue("MESSAGE");

        int index = 1;
        for (CertDTO dto : dtos) {
            Row row = sheet.createRow(index);
            row.createCell(0).setCellValue(index);
            row.createCell(1).setCellValue(dto.getOwnerId());
            row.createCell(2).setCellValue(dto.getCsr());
            row.createCell(3).setCellValue(dto.getMessage());
            index++;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            wb.write(bos);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            bos.close();
        }
        return bos.toByteArray();
    }

    public byte[] exportCsrFileFormat2(List<CertRequestInfoDTO> dtos, int step) throws IOException, ApplicationException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            InputStream is = fileResourceService.getTemplateFile("/templates/excel/Certificate-Request-Infomation.xlsx");
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            int index = 2;
            for (CertRequestInfoDTO dto : dtos) {
                Row row = sheet.createRow(index);
                row.createCell(0).setCellValue(index - 1);
                row.createCell(1).setCellValue(dto.getTaxCode());
                row.createCell(2).setCellValue(dto.getCompanyName());
                row.createCell(3).setCellValue(dto.getOrganization());
                row.createCell(4).setCellValue(dto.getOrganizationUnit());
                row.createCell(5).setCellValue(dto.getTitle());
                row.createCell(6).setCellValue(dto.getPersonalId());
                row.createCell(7).setCellValue(dto.getPersonalName());
                row.createCell(8).setCellValue(dto.getEmail());
                row.createCell(9).setCellValue(dto.getPhoneNumber());
                row.createCell(10).setCellValue(dto.getLocality());
                row.createCell(11).setCellValue(dto.getState());
                row.createCell(12).setCellValue(dto.getCountry());
                row.createCell(13).setCellValue(dto.getAlias());
                row.createCell(14).setCellValue(step == CertRequestInfoDTO.STEP_2 ? dto.getCsrValue() : "");
                row.createCell(15).setCellValue("");
                row.createCell(16).setCellValue(step == CertRequestInfoDTO.STEP_4 ? dto.getSerial() : "");
                row.createCell(17).setCellValue(step == CertRequestInfoDTO.STEP_4 ? dto.getPin() : "");
                index++;
            }
            workbook.write(bos);
            return bos.toByteArray();
        } finally {
            bos.close();
        }
    }

    public static List<CertDTO> convertExcelToCertDTO(InputStream inputStream) throws IOException {
        Workbook wb = new XSSFWorkbook(inputStream);
        Sheet sheet = wb.getSheetAt(0);
        int rows = sheet.getPhysicalNumberOfRows();
        List<CertDTO> dtos = new ArrayList<>();
        CertDTO dto = null;
        for (int i = 1; i < rows; i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                dto = new CertDTO();
                if (row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING) {
                    dto.setOwnerId(row.getCell(1).getStringCellValue());
                }
                if (row.getCell(1).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    dto.setOwnerId(String.valueOf(row.getCell(1).getNumericCellValue()));
                }

                dto.setSerial(row.getCell(2).getStringCellValue());
                dto.setCert(row.getCell(3).getStringCellValue());
                dtos.add(dto);
            }
        }
        return dtos;
    }

    public static List<UserDTO> convertExcelToUserDTO(InputStream inputStream) throws ApplicationException, IOException {
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        int rows = sheet.getPhysicalNumberOfRows();
        List<UserDTO> userDTOList = new ArrayList<>();
        UserDTO userDTO = null;
        for (int i = 1; i < rows; i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                userDTO = new UserDTO();
                if (row.getCell(1) != null) {
                    userDTO.setLogin(row.getCell(1).getStringCellValue());
                } else {
                    userDTO.setLogin(row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
                }

                userDTO.setFirstName(row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
                userDTO.setLastName(row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
                userDTO.setEmail(row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
                userDTO.setPhone(row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
                userDTO.setCommonName(row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
                userDTO.setOrganizationName(row.getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
                userDTO.setOrganizationUnit(row.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
                userDTO.setLocalityName(row.getCell(9, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
                userDTO.setStateName(row.getCell(10, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
                userDTO.setCountry(row.getCell(11, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
                userDTO.setLangKey(row.getCell(12, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
                userDTOList.add(userDTO);
            }
        }
        return userDTOList;
    }

    public static List<CertRequestInfoDTO> convertCertRequest(InputStream inputStream) throws IOException {
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        int rows = sheet.getPhysicalNumberOfRows();
        List<CertRequestInfoDTO> csrDTOs = new ArrayList<>();
        CertRequestInfoDTO csrDTO;
        DataFormatter formatter = new DataFormatter(Locale.US);
        for (int i = 2; i < rows; i++) {
            Row row = sheet.getRow(i);
            if (!checkIfRowIsEmpty(row)) {
                csrDTO = new CertRequestInfoDTO();
                csrDTO.setTaxCode(formatter.formatCellValue(row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)));
                csrDTO.setCompanyName(formatter.formatCellValue(row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)));
                csrDTO.setOrganization(formatter.formatCellValue(row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)));
                csrDTO.setOrganizationUnit(formatter.formatCellValue(row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)));
                csrDTO.setTitle(formatter.formatCellValue(row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)));
                csrDTO.setPersonalId(formatter.formatCellValue(row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)));
                csrDTO.setPersonalName(formatter.formatCellValue(row.getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)));
                csrDTO.setEmail(formatter.formatCellValue(row.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)));
                csrDTO.setPhoneNumber(formatter.formatCellValue(row.getCell(9, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)));
                csrDTO.setLocality(formatter.formatCellValue(row.getCell(10, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)));
                csrDTO.setState(formatter.formatCellValue(row.getCell(11, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)));
                csrDTO.setCountry(formatter.formatCellValue(row.getCell(12, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)));
                csrDTO.setAlias(formatter.formatCellValue(row.getCell(13, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)));
                csrDTO.setCsrValue(formatter.formatCellValue(row.getCell(14, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)));
                csrDTO.setCertValue(formatter.formatCellValue(row.getCell(15, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)));
                csrDTOs.add(csrDTO);
            }
        }
        return csrDTOs;
    }


    public static boolean checkIfRowIsEmpty(Row row) {
        if (row == null) {
            return true;
        }
        if (row.getLastCellNum() <= 0) {
            return true;
        }
        for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK && cell.getCellType() != Cell.CELL_TYPE_FORMULA && StringUtils.isNotBlank(cell.toString()) && StringUtils.isNotEmpty(cell.toString())) {
                return false;
            }
        }
        return true;
    }


    public static byte[] exportImageImportResult(List<Pair<String, Pair<String, Boolean>>> lstResult) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Result import image");
        sheet.setColumnWidth(0, 1000);
        sheet.setColumnWidth(1, 10000);
        sheet.setColumnWidth(2, 15000);
        sheet.setColumnWidth(3, 10000);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern((short) 1);

        String headers[] = new String[]{"STT", "CMND", "Trạng thái import", "Lỗi"};
        int idx = 0;
        Row header = sheet.createRow(0);
        for (String h : headers) {
            Cell headerCell = header.createCell(idx++);
            headerCell.setCellValue(h);
            headerCell.setCellStyle(headerStyle);
        }

        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);
        headerStyle.setFont(font);

        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        int rowIndex = 1;
        for (Pair<String, Pair<String, Boolean>> result : lstResult) {

            boolean status = result.getValue().getValue();
            String pid = result.getKey();
            String message = result.getValue().getKey();

            Row row = sheet.createRow(rowIndex);
            Cell cell = row.createCell(0);
            cell.setCellValue(rowIndex);
            cell.setCellStyle(style);

            Cell cellPesonalId = row.createCell(1);
            cellPesonalId.setCellValue(pid);
            cellPesonalId.setCellStyle(style);

            Cell cellStatus = row.createCell(2);
            cellStatus.setCellValue(status);
            cellStatus.setCellStyle(style);


            if (!status) {
                Cell cellMessage = row.createCell(3);
                cellMessage.setCellValue(message);
                cellMessage.setCellStyle(style);
            }
            rowIndex++;
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            bos.close();
        }
        return bos.toByteArray();
    }
}
