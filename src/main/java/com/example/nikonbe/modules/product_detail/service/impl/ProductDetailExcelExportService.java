package com.example.nikonbe.modules.product_detail.service.impl;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.product_detail.dto.response.ProductDetailResponseDTO;
import com.example.nikonbe.modules.product_detail.service.interF.ProductDetailService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductDetailExcelExportService {

  private final ProductDetailService productDetailService;

  public byte[] exportProductDetailsToExcel(
      String sku, Status status, Integer productId, Integer colorId, Integer capacityId)
      throws IOException {
    try (Workbook workbook = new XSSFWorkbook()) {
      Sheet sheet = workbook.createSheet("Danh sách sản phẩm chi tiết");

      CellStyle headerStyle = createHeaderStyle(workbook);
      CellStyle dataStyle = createDataStyle(workbook);
      CellStyle currencyStyle = createCurrencyStyle(workbook);

      createHeaderRow(sheet, headerStyle);

      int page = 0;
      int size = 1000;
      int rowNum = 1;

      boolean hasMoreData = true;
      while (hasMoreData) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDetailResponseDTO> productDetailPage =
            productDetailService.getAll(sku, status, productId, colorId, capacityId, pageable);

        List<ProductDetailResponseDTO> productDetails = productDetailPage.getContent();
        if (productDetails.isEmpty()) {
          hasMoreData = false;
          break;
        }

        rowNum = addDataRows(sheet, productDetails, rowNum, dataStyle, currencyStyle);

        hasMoreData = productDetailPage.hasNext();
        page++;
      }

      autoSizeColumns(sheet);

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      workbook.write(outputStream);
      return outputStream.toByteArray();
    }
  }

  private void createHeaderRow(Sheet sheet, CellStyle headerStyle) {
    Row headerRow = sheet.createRow(0);
    String[] headers = {
      "ID", "SKU", "Tên sản phẩm", "Màu sắc", "Dung tích", "Giá", "Tồn kho", "Trạng thái", "Ngày tạo"
    };

    for (int i = 0; i < headers.length; i++) {
      Cell cell = headerRow.createCell(i);
      cell.setCellValue(headers[i]);
      cell.setCellStyle(headerStyle);
    }
  }

  private int addDataRows(
      Sheet sheet,
      List<ProductDetailResponseDTO> productDetails,
      int startRowNum,
      CellStyle dataStyle,
      CellStyle currencyStyle) {
    int rowNum = startRowNum;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    for (ProductDetailResponseDTO detail : productDetails) {
      Row row = sheet.createRow(rowNum++);

      Cell cell0 = row.createCell(0);
      cell0.setCellValue(detail.getId() != null ? detail.getId() : 0);
      cell0.setCellStyle(dataStyle);

      Cell cell1 = row.createCell(1);
      cell1.setCellValue(detail.getSku() != null ? detail.getSku() : "");
      cell1.setCellStyle(dataStyle);

      Cell cell2 = row.createCell(2);
      cell2.setCellValue(detail.getProductName() != null ? detail.getProductName() : "");
      cell2.setCellStyle(dataStyle);

      Cell cell3 = row.createCell(3);
      String colorName = "";
      if (detail.getColor() != null && detail.getColor().getName() != null) {
        colorName = detail.getColor().getName();
      } else if (detail.getColorName() != null) {
        colorName = detail.getColorName();
      }
      cell3.setCellValue(colorName);
      cell3.setCellStyle(dataStyle);

      Cell cell4 = row.createCell(4);
      String capacityName = "";
      if (detail.getCapacity() != null && detail.getCapacity().getName() != null) {
        capacityName = detail.getCapacity().getName();
      } else if (detail.getCapacityName() != null) {
        capacityName = detail.getCapacityName();
      }
      cell4.setCellValue(capacityName);
      cell4.setCellStyle(dataStyle);

      Cell cell5 = row.createCell(5);
      if (detail.getPrice() != null) {
        cell5.setCellValue(detail.getPrice().doubleValue());
      } else {
        cell5.setCellValue(0.0);
      }
      cell5.setCellStyle(currencyStyle);

      Cell cell6 = row.createCell(6);
      cell6.setCellValue(detail.getStock() != null ? detail.getStock() : 0);
      cell6.setCellStyle(dataStyle);

      Cell cell7 = row.createCell(7);
      cell7.setCellValue(getStatusText(detail.getStatus()));
      cell7.setCellStyle(dataStyle);

      Cell cell8 = row.createCell(8);
      cell8.setCellValue(
          detail.getCreatedAt() != null ? detail.getCreatedAt().format(formatter) : "");
      cell8.setCellStyle(dataStyle);
    }

    return rowNum;
  }

  private CellStyle createHeaderStyle(Workbook workbook) {
    CellStyle style = workbook.createCellStyle();
    Font font = workbook.createFont();
    font.setBold(true);
    font.setFontHeightInPoints((short) 12);
    style.setFont(font);
    style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    style.setBorderTop(BorderStyle.THIN);
    style.setBorderBottom(BorderStyle.THIN);
    style.setBorderLeft(BorderStyle.THIN);
    style.setBorderRight(BorderStyle.THIN);
    style.setAlignment(HorizontalAlignment.CENTER);
    style.setVerticalAlignment(VerticalAlignment.CENTER);
    return style;
  }

  private CellStyle createDataStyle(Workbook workbook) {
    CellStyle style = workbook.createCellStyle();
    style.setBorderTop(BorderStyle.THIN);
    style.setBorderBottom(BorderStyle.THIN);
    style.setBorderLeft(BorderStyle.THIN);
    style.setBorderRight(BorderStyle.THIN);
    style.setVerticalAlignment(VerticalAlignment.CENTER);
    return style;
  }

  private CellStyle createCurrencyStyle(Workbook workbook) {
    CellStyle style = createDataStyle(workbook);
    DataFormat format = workbook.createDataFormat();
    style.setDataFormat(format.getFormat("#,##0 ₫"));
    style.setAlignment(HorizontalAlignment.RIGHT);
    return style;
  }

  private void autoSizeColumns(Sheet sheet) {
    if (sheet.getPhysicalNumberOfRows() > 0) {
      Row firstRow = sheet.getRow(0);
      for (int i = 0; i < firstRow.getPhysicalNumberOfCells(); i++) {
        sheet.autoSizeColumn(i);
        if (sheet.getColumnWidth(i) < 2000) {
          sheet.setColumnWidth(i, 2000);
        }
        if (sheet.getColumnWidth(i) > 8000) {
          sheet.setColumnWidth(i, 8000);
        }
      }
    }
  }

  private String getStatusText(Status status) {
    if (status == null) return "";
    return switch (status) {
      case ACTIVE -> "Hoạt động";
      case INACTIVE -> "Không hoạt động";
      case DELETED -> "Đã xóa";
      default -> "Không xác định";
    };
  }
}
