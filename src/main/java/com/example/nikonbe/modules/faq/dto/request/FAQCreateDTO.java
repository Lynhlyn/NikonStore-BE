package com.example.nikonbe.modules.faq.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FAQCreateDTO {

  @NotBlank(message = "Câu hỏi không được để trống")
  private String question;

  @NotBlank(message = "Câu trả lời không được để trống")
  private String answer;

  private Integer categoryId;

  private Integer tagId;

  @Builder.Default
  private Boolean status = true;
}


