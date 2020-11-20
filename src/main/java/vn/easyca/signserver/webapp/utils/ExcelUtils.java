package vn.easyca.signserver.webapp.utils;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vn.easyca.signserver.core.dto.CertDTO;
import vn.easyca.signserver.webapp.service.dto.UserDTO;
import vn.easyca.signserver.webapp.service.error.RequiredColumnNotFoundException;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtils {
    public static byte[] exportCsrFile(List<CertDTO> dtos) throws IOException {
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

    public static List<UserDTO> convertExcelToUserDTO(InputStream inputStream) throws IOException, RequiredColumnNotFoundException{
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        int rows = sheet.getPhysicalNumberOfRows();
        List<UserDTO> userDTOList = new ArrayList<>();
        UserDTO userDTO = null;
        for(int i = 1; i< rows; i++){
            Row row = sheet.getRow(i);
            if( row!= null){
                userDTO = new UserDTO();
                if(row.getCell(1)!= null ) {
                    userDTO.setLogin(row.getCell(1).getStringCellValue());
                }else {
                    userDTO.setLogin(row.getCell(1, org.apache.poi.ss.usermodel.Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                }

                userDTO.setFirstName(row.getCell(2,  org.apache.poi.ss.usermodel.Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                userDTO.setLastName(row.getCell(3,  org.apache.poi.ss.usermodel.Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                userDTO.setEmail(row.getCell(4,  org.apache.poi.ss.usermodel.Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                userDTO.setPhone(row.getCell(5,  org.apache.poi.ss.usermodel.Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                userDTO.setCommonName(row.getCell(6,  org.apache.poi.ss.usermodel.Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                userDTO.setOrganizationName(row.getCell(7,  org.apache.poi.ss.usermodel.Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                userDTO.setOrganizationUnit(row.getCell(8,  org.apache.poi.ss.usermodel.Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                userDTO.setLocalityName(row.getCell(9,  org.apache.poi.ss.usermodel.Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                userDTO.setStateName(row.getCell(10,  org.apache.poi.ss.usermodel.Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                userDTO.setCountry(row.getCell(11,  org.apache.poi.ss.usermodel.Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                userDTO.setLangKey(row.getCell(12,  org.apache.poi.ss.usermodel.Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                userDTOList.add(userDTO);
            }
        }
        return userDTOList;
    }
}
