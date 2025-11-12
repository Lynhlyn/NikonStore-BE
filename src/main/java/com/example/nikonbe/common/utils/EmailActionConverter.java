package com.example.nikonbe.common.utils;

import com.example.nikonbe.common.enums.EmailAction;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EmailActionConverter implements AttributeConverter<EmailAction, String> {

  @Override
  public String convertToDatabaseColumn(EmailAction attribute) {
    if (attribute == null) {
      return null;
    }
    return attribute.getValue();
  }

  @Override
  public EmailAction convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.trim().isEmpty()) {
      return null;
    }

    try {
      return EmailAction.fromValue(dbData);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Unknown email action value: " + dbData, e);
    }
  }
}
