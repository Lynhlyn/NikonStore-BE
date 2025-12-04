package com.example.nikonbe.common.utils;

import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class DeviceInfoUtil {

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DeviceInfo {
    private String deviceName;
    private String browserName;
    private String deviceType;
  }

  public static DeviceInfo parseUserAgent(String userAgent) {
    if (userAgent == null || userAgent.trim().isEmpty()) {
      return new DeviceInfo("Unknown", "Unknown", "Unknown");
    }

    String deviceType = extractDeviceType(userAgent);
    String browserName = extractBrowserName(userAgent);
    String deviceName = extractDeviceName(userAgent, deviceType);

    return new DeviceInfo(deviceName, browserName, deviceType);
  }

  private static String extractDeviceType(String userAgent) {
    String ua = userAgent.toLowerCase();

    if (ua.contains("mobile") || ua.contains("android") || ua.contains("iphone") || ua.contains("ipod")) {
      return "Mobile";
    } else if (ua.contains("tablet") || ua.contains("ipad")) {
      return "Tablet";
    } else {
      return "Desktop";
    }
  }

  private static String extractBrowserName(String userAgent) {
    String ua = userAgent.toLowerCase();

    if (ua.contains("edg/")) {
      return "Edge";
    } else if (ua.contains("chrome/") && !ua.contains("edg/")) {
      return "Chrome";
    } else if (ua.contains("safari/") && !ua.contains("chrome/")) {
      return "Safari";
    } else if (ua.contains("firefox/")) {
      return "Firefox";
    } else if (ua.contains("opera/") || ua.contains("opr/")) {
      return "Opera";
    } else if (ua.contains("msie") || ua.contains("trident/")) {
      return "Internet Explorer";
    } else {
      return "Unknown";
    }
  }

  private static String extractDeviceName(String userAgent, String deviceType) {
    String ua = userAgent.toLowerCase();

    if ("Mobile".equals(deviceType)) {
      if (ua.contains("iphone")) {
        Pattern pattern = Pattern.compile("iphone\\s*(?:os\\s*)?(\\d+[_.]\\d+)");
        java.util.regex.Matcher matcher = pattern.matcher(ua);
        if (matcher.find()) {
          String version = matcher.group(1).replace("_", ".");
          return "iPhone " + version;
        }
        return "iPhone";
      } else if (ua.contains("android")) {
        if (ua.contains("samsung")) {
          return "Samsung";
        } else if (ua.contains("xiaomi")) {
          return "Xiaomi";
        } else if (ua.contains("oppo")) {
          return "OPPO";
        } else if (ua.contains("vivo")) {
          return "Vivo";
        } else {
          return "Android Phone";
        }
      }
    } else if ("Tablet".equals(deviceType)) {
      if (ua.contains("ipad")) {
        return "iPad";
      } else if (ua.contains("android")) {
        return "Android Tablet";
      }
    } else {
      if (ua.contains("windows")) {
        return "Windows PC";
      } else if (ua.contains("macintosh") || ua.contains("mac os")) {
        return "Mac";
      } else if (ua.contains("linux")) {
        return "Linux PC";
      }
    }

    return "Unknown Device";
  }

  public static String getClientIpAddress(jakarta.servlet.http.HttpServletRequest request) {
    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
      return xForwardedFor.split(",")[0].trim();
    }

    String xRealIp = request.getHeader("X-Real-IP");
    if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
      return xRealIp;
    }

    return request.getRemoteAddr();
  }
}

