package com.example.nikonbe.modules.tag.dto.request;

import com.example.nikonbe.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TagUpdateDTO {

  @NotBlank(message = "Tag name is required")
  private String name;

  @NotBlank(message = "Slug is required")
  @Pattern(
      regexp = "^[a-z0-9-]+$",
      message = "Slug must be lowercase, alphanumeric, and may contain hyphens")
  private String slug;

  private String description;

  @NotNull(message = "Status is required")
  @Schema(
      description = "Trạng thái tag",
      example = "ACTIVE",
      required = true,
      allowableValues = {"ACTIVE", "INACTIVE", "DELETED"})
  private Status status;
}
