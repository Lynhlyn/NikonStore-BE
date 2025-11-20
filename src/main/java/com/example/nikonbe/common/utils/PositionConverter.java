package com.example.nikonbe.common.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class PositionConverter implements AttributeConverter<Integer, String> {

  @Override
  public String convertToDatabaseColumn(Integer position) {
    if (position == null) {
      return null;
    }
    try {
      // Convert Integer to JSON number format
      // MySQL JSON accepts number directly, so we return the number as string
      // MySQL will automatically convert it to JSON number
      return String.valueOf(position);
    } catch (Exception e) {
      throw new RuntimeException("Error converting position to JSON", e);
    }
  }

  @Override
  public Integer convertToEntityAttribute(String json) {
    if (json == null || json.trim().isEmpty()) {
      return null;
    }
    try {
      // Parse JSON string to Integer
      // MySQL JSON stores numbers as strings, so we need to parse it
      String cleaned = json.trim();
      if (cleaned.startsWith("\"") && cleaned.endsWith("\"")) {
        cleaned = cleaned.substring(1, cleaned.length() - 1);
      }
      return Integer.parseInt(cleaned);
    } catch (Exception e) {
      throw new RuntimeException("Error converting JSON to position: " + json, e);
    }
  }
}

