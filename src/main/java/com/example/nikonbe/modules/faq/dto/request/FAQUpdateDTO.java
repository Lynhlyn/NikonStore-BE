package com.example.nikonbe.modules.faq.dto.request;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FAQUpdateDTO {

  private String question;

  private String answer;

  private Integer categoryId;

  private Integer tagId;

  private Boolean status;
}


