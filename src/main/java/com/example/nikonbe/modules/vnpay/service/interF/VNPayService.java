package com.example.nikonbe.modules.vnpay.service.interF;

import java.util.Map;

public interface VNPayService {

  String createPaymentUrl(long amount, String orderInfo, String ipAddr, String orderId);

  boolean verifyReturn(Map<String, String> params);

  String generateQrCode(String paymentUrl);
}
