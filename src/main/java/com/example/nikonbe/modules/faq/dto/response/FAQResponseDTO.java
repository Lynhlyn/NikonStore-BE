package com.example.nikonbe.modules.faq.dto.response;

import com.example.nikonbe.modules.content_category.dto.response.ContentCategoryResponseDTO;
import com.example.nikonbe.modules.content_tag.dto.response.ContentTagResponseDTO;
import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FAQResponseDTO {
  private Integer id;
  private String question;
  private String answer;
  private ContentCategoryResponseDTO category;
  private ContentTagResponseDTO tag;
  private Boolean status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}


