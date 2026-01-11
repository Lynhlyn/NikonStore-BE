package com.example.nikonbe.modules.product.service.impl;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.product.dto.response.ProductResponseDTO;
import com.example.nikonbe.modules.product.service.interF.ProductService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
public class ProductExcelExportService {

  private final ProductService productService;

  public byte[] exportProductsToExcel(
      String keyword,
      Status status,
      Integer categoryId,
      Integer brandId,
      Integer materialId,
      Integer strapTypeId)
      throws IOException {
    try (Workbook workbook = new XSSFWorkbook()) {
      Sheet sheet = workbook.createSheet("Danh sách sản phẩm");

      CellStyle headerStyle = createHeaderStyle(workbook);
      CellStyle dataStyle = createDataStyle(workbook);

      createHeaderRow(sheet, headerStyle);

      int page = 0;
      int size = 1000;
      int rowNum = 1;

      boolean hasMoreData = true;
      while (hasMoreData) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponseDTO> productPage =
            productService.getAll(
                keyword, status, categoryId, brandId, materialId, strapTypeId, pageable);

        List<ProductResponseDTO> products = productPage.getContent();
        if (products.isEmpty()) {
          hasMoreData = false;
          break;
        }

        rowNum = addDataRows(sheet, products, rowNum, dataStyle);

        hasMoreData = productPage.hasNext();
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
      "ID",
      "Tên sản phẩm",
      "Thương hiệu",
      "Danh mục",
      "Chất liệu",
      "Loại dây đeo",
      "Trạng thái",
      "Ngày tạo",
      "Ngày cập nhật"
    };

    for (int i = 0; i < headers.length; i++) {
      Cell cell = headerRow.createCell(i);
      cell.setCellValue(headers[i]);
      cell.setCellStyle(headerStyle);
    }
  }

  private int addDataRows(
      Sheet sheet, List<ProductResponseDTO> products, int startRowNum, CellStyle dataStyle) {
    int rowNum = startRowNum;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    for (ProductResponseDTO product : products) {
      Row row = sheet.createRow(rowNum++);

      Cell cell0 = row.createCell(0);
      cell0.setCellValue(product.getId() != null ? product.getId() : 0);
      cell0.setCellStyle(dataStyle);

      Cell cell1 = row.createCell(1);
      cell1.setCellValue(product.getName() != null ? product.getName() : "");
      cell1.setCellStyle(dataStyle);

      Cell cell2 = row.createCell(2);
      cell2.setCellValue(
          product.getBrand() != null && product.getBrand().getName() != null
              ? product.getBrand().getName()
              : "");
      cell2.setCellStyle(dataStyle);

      Cell cell3 = row.createCell(3);
      cell3.setCellValue(
          product.getCategory() != null && product.getCategory().getName() != null
              ? product.getCategory().getName()
              : "");
      cell3.setCellStyle(dataStyle);

      Cell cell4 = row.createCell(4);
      cell4.setCellValue(
          product.getMaterial() != null && product.getMaterial().getName() != null
              ? product.getMaterial().getName()
              : "");
      cell4.setCellStyle(dataStyle);

      Cell cell5 = row.createCell(5);
      cell5.setCellValue(
          product.getStrapType() != null && product.getStrapType().getName() != null
              ? product.getStrapType().getName()
              : "");
      cell5.setCellStyle(dataStyle);

      Cell cell6 = row.createCell(6);
      cell6.setCellValue(getStatusText(product.getStatus()));
      cell6.setCellStyle(dataStyle);

      Cell cell7 = row.createCell(7);
      cell7.setCellValue(
          product.getCreatedAt() != null ? product.getCreatedAt().format(formatter) : "");
      cell7.setCellStyle(dataStyle);

      Cell cell8 = row.createCell(8);
      cell8.setCellValue(
          product.getUpdatedAt() != null ? product.getUpdatedAt().format(formatter) : "");
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
