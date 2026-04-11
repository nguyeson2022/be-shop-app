package com.project.shopapp.services.Report;

import com.project.shopapp.models.Order;
import com.project.shopapp.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final OrderRepository orderRepository;

    public void generateExcel(HttpServletResponse response) throws IOException {
        List<Order> orders = orderRepository.findAll();

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Thong tin hoa don");

        // Tạo tiêu đề
        String title = "BÁO CÁO HÓA ĐƠN";
        String reportDate = "Ngày xuất báo cáo: " + LocalDate.now().toString();
        createTitleRow(sheet, title, reportDate, workbook);

        // Tạo header
        String[] headers = {"ID", "Họ và tên", "Số điện thoại", "Địa chỉ nhận", "Email", "Ghi chú",
                "Ngày tạo", "Trạng thái",  "Phương thức vận chuyển", "Phương thức thanh toán", "Tổng tiền"};
        createHeaderRow(sheet, headers, workbook);

        // Đổ dữ liệu
        fillData(sheet, orders, workbook);

        // Auto resize cột
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Ghi xuống response
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    private void createTitleRow(Sheet sheet, String title, String reportDate, Workbook workbook) {
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(title);

        // Style cho tiêu đề
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 14);
        titleStyle.setFont(titleFont);
        titleCell.setCellStyle(titleStyle);

        // Dòng ngày xuất báo cáo
        Row reportDateRow = sheet.createRow(1);
        Cell reportDateCell = reportDateRow.createCell(0);
        reportDateCell.setCellValue(reportDate);

        // Style cho ngày xuất báo cáo
        CellStyle dateStyle = workbook.createCellStyle();
        Font dateFont = workbook.createFont();
        dateFont.setFontHeightInPoints((short) 12);
        dateStyle.setFont(dateFont);
        reportDateCell.setCellStyle(dateStyle);
    }

    private void createHeaderRow(Sheet sheet, String[] headers, Workbook workbook) {
        Row headerRow = sheet.createRow(3);

        // Style cho header
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Tạo các cell cho header
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void fillData(Sheet sheet, List<Order> orders, Workbook workbook) {
        int dataRowIndex = 4;

        // Style cho dữ liệu
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setWrapText(true); // Để cho phép dữ liệu xuống dòng tự động

        // Style cho cột tổng tiền
        CellStyle borderedDataStyle = workbook.createCellStyle();
        HSSFFont font = (HSSFFont) workbook.createFont();
        font.setColor(IndexedColors.RED.getIndex());
        borderedDataStyle.setFont(font);
//        borderedDataStyle.setBorderBottom(BorderStyle.THIN);
//        borderedDataStyle.setBorderTop(BorderStyle.THIN);
//        borderedDataStyle.setBorderLeft(BorderStyle.THIN);
//        borderedDataStyle.setBorderRight(BorderStyle.THIN);
//        borderedDataStyle.setBottomBorderColor(IndexedColors.RED.getIndex());

        // Đổ dữ liệu vào từng dòng
        for (Order order : orders) {
            Row dataRow = sheet.createRow(dataRowIndex);
            dataRow.createCell(0).setCellValue(order.getId());
            dataRow.createCell(1).setCellValue(order.getFullName());
            dataRow.createCell(2).setCellValue(order.getPhoneNumber());
            dataRow.createCell(3).setCellValue(order.getAddress());
            dataRow.createCell(4).setCellValue(order.getEmail());
            dataRow.createCell(5).setCellValue(order.getNote());
            dataRow.createCell(6).setCellValue(order.getOrderDate().toString());
            dataRow.createCell(7).setCellValue(order.getStatus());
            dataRow.createCell(8).setCellValue(order.getShippingMethod());
            dataRow.createCell(9).setCellValue(order.getPaymentMethod());

            // Cột tổng tiền
            Cell totalMoneyCell = dataRow.createCell(10);
            totalMoneyCell.setCellValue(order.getTotalMoney());
            DataFormat format = workbook.createDataFormat();
            CellStyle totalMoneyCellStyle = workbook.createCellStyle(); // Tạo một CellStyle mới
            totalMoneyCellStyle.setDataFormat(format.getFormat("#,##0.00")); // Đặt định dạng cho CellStyle
            totalMoneyCell.setCellStyle(totalMoneyCellStyle);
            totalMoneyCell.setCellStyle(borderedDataStyle);

            dataRowIndex++;
        }
    }
}
