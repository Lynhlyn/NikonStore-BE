package com.example.nikonbe.modules.blog.dto.response;

import com.example.nikonbe.modules.content_category.dto.response.ContentCategoryResponseDTO;
import com.example.nikonbe.modules.content_tag.dto.response.ContentTagResponseDTO;
import com.example.nikonbe.modules.staff.dto.response.StaffResponseDTO;
import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlogResponseDTO {
  private Integer id;
  private String title;
  private String slug;
  private String summary;
  private String content;
  private String thumbnailUrl;
  private StaffResponseDTO staff;
  private ContentCategoryResponseDTO category;
  private ContentTagResponseDTO tag;
  private Boolean isPublished;
  private Integer viewCount;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}


