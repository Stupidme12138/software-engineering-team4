package com.example.libraryms.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryCreateRequest {
  @NotBlank(message = "分类名称不能为空")
  private String name;
  private String subject;
  private String description;
}

