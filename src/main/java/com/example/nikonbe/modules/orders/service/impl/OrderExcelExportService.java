package com.example.nikonbe.modules.orders.service.impl;

import com.example.nikonbe.modules.order_detail.dto.response.OrderDetailReponse;
import com.example.nikonbe.modules.order_detail.entity.OrderDetail;
import com.example.nikonbe.modules.order_detail.mapper.OrderDetailMapper;
import com.example.nikonbe.modules.order_detail.repository.OrderDetailRepository;
import com.example.nikonbe.modules.orders.dto.response.GetOrderDetailResponse;
import com.example.nikonbe.modules.orders.dto.response.OrderAllResponse;
import com.example.nikonbe.modules.orders.service.interF.OrderService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderExcelExportService {

  private final OrderService orderService;
  private final OrderDetailRepository orderDetailRepository;
  private final OrderDetailMapper orderDetailMapper;

  public byte[] exportOrdersToExcel(
      String keyword, String type, Integer status, String fromDate, String toDate)
      throws IOException {
    try (Workbook workbook = new XSSFWorkbook()) {
      Sheet sheet = workbook.createSheet("Danh sách đơn hàng");

      CellStyle headerStyle = createHeaderStyle(workbook);
      CellStyle dataStyle = createDataStyle(workbook);
      CellStyle currencyStyle = createCurrencyStyle(workbook);

      createHeaderRow(sheet, headerStyle);

      int page = 0;
      int size = 1000;
      int rowNum = 1;

      boolean hasMoreData = true;
      while (hasMoreData) {
        Page<OrderAllResponse> orderPage =
            orderService.searchOrders(keyword, type, status, fromDate, toDate, page, size);

        List<OrderAllResponse> orders = orderPage.getContent();
        if (orders.isEmpty()) {
          hasMoreData = false;
          break;
        }

        rowNum = addDataRows(sheet, orders, rowNum, dataStyle, currencyStyle);

        hasMoreData = orderPage.hasNext();
        page++;
      }

      autoSizeColumns(sheet);

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      workbook.write(outputStream);
      return outputStream.toByteArray();
    }
  }

  public byte[] exportOrderDetailsToExcel(Integer orderId) throws IOException {
    try (Workbook workbook = new XSSFWorkbook()) {
      Sheet sheet = workbook.createSheet("Chi tiết đơn hàng #" + orderId);

      CellStyle headerStyle = createHeaderStyle(workbook);
      CellStyle dataStyle = createDataStyle(workbook);
      CellStyle currencyStyle = createCurrencyStyle(workbook);

      GetOrderDetailResponse orderDetail = orderService.getOrderDetailById(orderId);

      int rowNum =
          createOrderSummarySection(sheet, orderDetail, headerStyle, dataStyle, currencyStyle);

      rowNum += 2;

      createProductDetailsHeader(sheet, rowNum, headerStyle);
      rowNum++;

      addProductDetailsRows(sheet, orderDetail.getOrderDetails(), rowNum, dataStyle, currencyStyle);

      autoSizeColumns(sheet);

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      workbook.write(outputStream);
      return outputStream.toByteArray();
    }
  }

  private void createHeaderRow(Sheet sheet, CellStyle headerStyle) {
    Row headerRow = sheet.createRow(0);
    String[] headers = {
      "Mã đơn hàng",
      "Tên khách hàng",
      "Ngày đặt hàng",
      "Tổng tiền hàng",
      "Giảm giá",
      "Phương thức thanh toán",
      "Trạng thái thanh toán",
      "Địa chỉ giao hàng",
      "Phí vận chuyển",
      "Loại đơn hàng",
      "Trạng thái",
      "Ghi chú",
      "Số điện thoại người nhận",
      "Email người nhận",
      "Mã voucher",
      "Tên nhân viên",
      "Tổng tiền thanh toán",
      "Tên sản phẩm",
      "Số lượng",
      "Đơn giá SP"
    };

    for (int i = 0; i < headers.length; i++) {
      Cell cell = headerRow.createCell(i);
      cell.setCellValue(headers[i]);
      cell.setCellStyle(headerStyle);
    }
  }

  private int addDataRows(
      Sheet sheet,
      List<OrderAllResponse> orders,
      int startRowNum,
      CellStyle dataStyle,
      CellStyle currencyStyle) {
    int rowNum = startRowNum;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    List<OrderAllResponse> sortedOrders =
        orders.stream()
            .sorted(
                Comparator.comparing(
                    OrderAllResponse::getOrderDate,
                    Comparator.nullsLast(Comparator.reverseOrder())))
            .toList();

    for (OrderAllResponse order : sortedOrders) {
      List<OrderDetail> orderDetails =
          orderDetailRepository.findByOrderIdWithDetails(order.getOrderid());
      List<OrderDetailReponse> orderDetailList =
          orderDetails.stream().map(orderDetailMapper::toOrderProductResponse).toList();

      if (orderDetailList.isEmpty()) {
        orderDetailList = List.of(new OrderDetailReponse());
      }

      int orderStartRow = rowNum;
      int productCount = orderDetailList.size();

      for (int i = 0; i < productCount; i++) {
        Row row = sheet.createRow(rowNum++);
        OrderDetailReponse product = orderDetailList.get(i);

        if (i == 0) {
          Cell cell0 = row.createCell(0);
          cell0.setCellValue(order.getTrackingNumber() != null ? order.getTrackingNumber() : "");
          cell0.setCellStyle(dataStyle);

          Cell cell1 = row.createCell(1);
          cell1.setCellValue(order.getCustomerName() != null ? order.getCustomerName() : "");
          cell1.setCellStyle(dataStyle);

          Cell cell2 = row.createCell(2);
          cell2.setCellValue(
              order.getOrderDate() != null ? order.getOrderDate().format(formatter) : "");
          cell2.setCellStyle(dataStyle);

          Cell cell3 = row.createCell(3);
          if (order.getTotalAmount() != null) {
            cell3.setCellValue(order.getTotalAmount().doubleValue());
          } else {
            cell3.setCellValue(0.0);
          }
          cell3.setCellStyle(currencyStyle);

          Cell cell4 = row.createCell(4);
          if (order.getDiscount() != null) {
            cell4.setCellValue(order.getDiscount().doubleValue());
          } else {
            cell4.setCellValue(0.0);
          }
          cell4.setCellStyle(currencyStyle);

          Cell cell5 = row.createCell(5);
          cell5.setCellValue(getPaymentMethodText(order.getPaymentMethod()));
          cell5.setCellStyle(dataStyle);

          Cell cell6 = row.createCell(6);
          cell6.setCellValue(order.getPaymentStatus() != null ? order.getPaymentStatus() : "");
          cell6.setCellStyle(dataStyle);

          Cell cell7 = row.createCell(7);
          cell7.setCellValue(order.getShippingAddress() != null ? order.getShippingAddress() : "");
          cell7.setCellStyle(dataStyle);

          Cell cell8 = row.createCell(8);
          if (order.getShippingFee() != null) {
            cell8.setCellValue(order.getShippingFee().doubleValue());
          } else {
            cell8.setCellValue(0.0);
          }
          cell8.setCellStyle(currencyStyle);

          Cell cell9 = row.createCell(9);
          cell9.setCellValue(order.getOrdertype() != null ? order.getOrdertype() : "");
          cell9.setCellStyle(dataStyle);

          Cell cell10 = row.createCell(10);
          cell10.setCellValue(getStatusText(order.getOrderStatus()));
          cell10.setCellStyle(dataStyle);

          Cell cell11 = row.createCell(11);
          cell11.setCellValue(order.getNotes() != null ? order.getNotes() : "");
          cell11.setCellStyle(dataStyle);

          Cell cell12 = row.createCell(12);
          cell12.setCellValue(order.getRecipientPhone() != null ? order.getRecipientPhone() : "");
          cell12.setCellStyle(dataStyle);

          Cell cell13 = row.createCell(13);
          cell13.setCellValue(order.getRecipientEmail() != null ? order.getRecipientEmail() : "");
          cell13.setCellStyle(dataStyle);

          Cell cell14 = row.createCell(14);
          cell14.setCellValue(order.getVoucherCode() != null ? order.getVoucherCode() : "");
          cell14.setCellStyle(dataStyle);

          Cell cell15 = row.createCell(15);
          cell15.setCellValue(order.getStaffName() != null ? order.getStaffName() : "");
          cell15.setCellStyle(dataStyle);

          Cell cell16 = row.createCell(16);
          BigDecimal totalAmount =
              order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO;
          BigDecimal discount = order.getDiscount() != null ? order.getDiscount() : BigDecimal.ZERO;
          BigDecimal shippingFee =
              order.getShippingFee() != null ? order.getShippingFee() : BigDecimal.ZERO;
          BigDecimal finalPaymentAmount = totalAmount.subtract(discount).add(shippingFee);
          cell16.setCellValue(finalPaymentAmount.doubleValue());
          cell16.setCellStyle(currencyStyle);
        }

        Cell cell17 = row.createCell(17);
        cell17.setCellValue(product.getProductName() != null ? product.getProductName() : "");
        cell17.setCellStyle(dataStyle);

        Cell cell18 = row.createCell(18);
        cell18.setCellValue(product.getQuantity() != null ? product.getQuantity() : 0);
        cell18.setCellStyle(dataStyle);

        Cell cell19 = row.createCell(19);
        if (product.getPrice() != null) {
          cell19.setCellValue(product.getPrice().doubleValue());
        } else {
          cell19.setCellValue(0.0);
        }
        cell19.setCellStyle(currencyStyle);
      }

      if (productCount > 1) {
        int orderEndRow = rowNum - 1;

        for (int col = 0; col <= 16; col++) {
          sheet.addMergedRegion(new CellRangeAddress(orderStartRow, orderEndRow, col, col));
        }
      }
    }

    return rowNum;
  }

  private int createOrderSummarySection(
      Sheet sheet,
      GetOrderDetailResponse orderDetail,
      CellStyle headerStyle,
      CellStyle dataStyle,
      CellStyle currencyStyle) {
    int rowNum = 0;

    Row headerRow = sheet.createRow(rowNum++);
    Cell headerCell = headerRow.createCell(0);
    headerCell.setCellValue("THÔNG TIN ĐÔN HÀNG");
    headerCell.setCellStyle(headerStyle);

    String[][] orderInfo = {
      {
        "Mã Đơn Hàng:",
        orderDetail.getTrackingNumber() != null ? orderDetail.getTrackingNumber() : ""
      },
      {"Ngày đặt:", orderDetail.getOrderDate() != null ? orderDetail.getOrderDate() : ""},
      {"Khách hàng:", orderDetail.getCustomerName() != null ? orderDetail.getCustomerName() : ""},
      {"Email:", orderDetail.getCustomerEmail() != null ? orderDetail.getCustomerEmail() : ""},
      {
        "Số điện thoại:",
        orderDetail.getCustomerPhone() != null ? orderDetail.getCustomerPhone() : ""
      },
      {
        "Địa chỉ giao hàng:",
        orderDetail.getShippingAddress() != null ? orderDetail.getShippingAddress() : ""
      },
      {"Phương thức thanh toán:", getPaymentMethodText(orderDetail.getPaymentMethod())},
      {
        "Trạng thái thanh toán:",
        orderDetail.getPaymentStatus() != null ? orderDetail.getPaymentStatus() : ""
      },
      {"Trạng thái đơn hàng:", getStatusText(orderDetail.getOrderStatus())},
      {"Ghi chú:", orderDetail.getNote() != null ? orderDetail.getNote() : ""}
    };

    for (String[] info : orderInfo) {
      Row row = sheet.createRow(rowNum++);

      Cell labelCell = row.createCell(0);
      labelCell.setCellValue(info[0]);
      labelCell.setCellStyle(headerStyle);

      Cell valueCell = row.createCell(1);
      valueCell.setCellValue(info[1]);
      valueCell.setCellStyle(dataStyle);
    }

    Row totalRow = sheet.createRow(rowNum++);
    Cell totalLabelCell = totalRow.createCell(0);
    totalLabelCell.setCellValue("Tổng tiền hàng:");
    totalLabelCell.setCellStyle(headerStyle);

    Cell totalValueCell = totalRow.createCell(1);
    if (orderDetail.getTotalAmount() != null) {
      totalValueCell.setCellValue(orderDetail.getTotalAmount().doubleValue());
    }
    totalValueCell.setCellStyle(currencyStyle);

    if (orderDetail.getDiscount() != null
        && orderDetail.getDiscount().compareTo(BigDecimal.ZERO) > 0) {
      Row discountRow = sheet.createRow(rowNum++);
      Cell discountLabelCell = discountRow.createCell(0);
      discountLabelCell.setCellValue("Giảm giá:");
      discountLabelCell.setCellStyle(headerStyle);

      Cell discountValueCell = discountRow.createCell(1);
      discountValueCell.setCellValue(orderDetail.getDiscount().doubleValue());
      discountValueCell.setCellStyle(currencyStyle);
    }

    if (orderDetail.getShippingFee() != null) {
      Row shippingRow = sheet.createRow(rowNum++);
      Cell shippingLabelCell = shippingRow.createCell(0);
      shippingLabelCell.setCellValue("Phí giao hàng:");
      shippingLabelCell.setCellStyle(headerStyle);

      Cell shippingValueCell = shippingRow.createCell(1);
      shippingValueCell.setCellValue(orderDetail.getShippingFee().doubleValue());
      shippingValueCell.setCellStyle(currencyStyle);
    }

    Row finalRow = sheet.createRow(rowNum++);
    Cell finalLabelCell = finalRow.createCell(0);
    finalLabelCell.setCellValue("THÀNH TIỀN:");
    finalLabelCell.setCellStyle(headerStyle);

    Cell finalValueCell = finalRow.createCell(1);
    if (orderDetail.getTotalAmount() != null) {
      finalValueCell.setCellValue(
          orderDetail.getTotalAmount().doubleValue()
              - (orderDetail.getDiscount() != null ? orderDetail.getDiscount().doubleValue() : 0.0)
              + (orderDetail.getShippingFee() != null
                  ? orderDetail.getShippingFee().doubleValue()
                  : 0.0));
    }
    finalValueCell.setCellStyle(currencyStyle);

    return rowNum;
  }

  private void createProductDetailsHeader(Sheet sheet, int rowNum, CellStyle headerStyle) {
    Row headerRow = sheet.createRow(rowNum);
    String[] headers = {
      "STT", "SKU", "Tên sản phẩm", "Thương hiệu", "Danh mục",
      "Màu sắc", "Dung tích", "Số lượng", "Đơn giá", "Thành tiền"
    };

    for (int i = 0; i < headers.length; i++) {
      Cell cell = headerRow.createCell(i);
      cell.setCellValue(headers[i]);
      cell.setCellStyle(headerStyle);
    }
  }

  private void addProductDetailsRows(
      Sheet sheet,
      List<OrderDetailReponse> orderDetails,
      int startRowNum,
      CellStyle dataStyle,
      CellStyle currencyStyle) {
    int rowNum = startRowNum;
    int stt = 1;

    for (OrderDetailReponse detail : orderDetails) {
      Row row = sheet.createRow(rowNum++);

      Cell sttCell = row.createCell(0);
      sttCell.setCellValue(stt++);
      sttCell.setCellStyle(dataStyle);

      Cell skuCell = row.createCell(1);
      skuCell.setCellValue(detail.getSku() != null ? detail.getSku() : "");
      skuCell.setCellStyle(dataStyle);

      Cell nameCell = row.createCell(2);
      nameCell.setCellValue(detail.getProductName() != null ? detail.getProductName() : "");
      nameCell.setCellStyle(dataStyle);

      Cell brandCell = row.createCell(3);
      brandCell.setCellValue(detail.getBrandName() != null ? detail.getBrandName() : "");
      brandCell.setCellStyle(dataStyle);

      Cell categoryCell = row.createCell(4);
      categoryCell.setCellValue(detail.getCategoryName() != null ? detail.getCategoryName() : "");
      categoryCell.setCellStyle(dataStyle);

      Cell colorCell = row.createCell(5);
      colorCell.setCellValue(detail.getColorName() != null ? detail.getColorName() : "");
      colorCell.setCellStyle(dataStyle);

      Cell capacityCell = row.createCell(6);
      capacityCell.setCellValue(detail.getCapacityName() != null ? detail.getCapacityName() : "");
      capacityCell.setCellStyle(dataStyle);

      Cell quantityCell = row.createCell(7);
      quantityCell.setCellValue(detail.getQuantity() != null ? detail.getQuantity() : 0);
      quantityCell.setCellStyle(dataStyle);

      Cell priceCell = row.createCell(8);
      if (detail.getPrice() != null) {
        priceCell.setCellValue(detail.getPrice().doubleValue());
      }
      priceCell.setCellStyle(currencyStyle);

      Cell totalCell = row.createCell(9);
      if (detail.getPrice() != null && detail.getQuantity() != null) {
        double total = detail.getPrice().doubleValue() * detail.getQuantity();
        totalCell.setCellValue(total);
      }
      totalCell.setCellStyle(currencyStyle);
    }
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

  private String getStatusText(Integer status) {
    if (status == null) return "";
    return switch (status) {
      case 3 -> "Chờ xác nhận";
      case 4 -> "Đã xác nhận";
      case 5 -> "Đang giao hàng";
      case 6 -> "Hoàn thành";
      case 7 -> "Đã hủy";
      case 8 -> "Chờ thanh toán";
      case 12 -> "Giao hàng thất bại";
      case 13 -> "Đang chuẩn bị hàng";
      default -> "Không xác định";
    };
  }

  private String getPaymentMethodText(String paymentMethod) {
    if (paymentMethod == null) return "";
    return switch (paymentMethod.toLowerCase()) {
      case "vnpay", "bank_transfer" -> "Chuyển khoản ngân hàng";
      case "cash" -> "Thanh toán tiền mặt";
      case "card" -> "Thanh toán bằng thẻ";
      case "cod" -> "Thanh toán khi nhận hàng";
      default -> paymentMethod;
    };
  }
}
