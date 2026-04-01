package com.example.libraryms.dto.common;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PageRequest {
  @Min(value = 1, message = "page 必须 >= 1")
  private int page = 1;

  @Min(value = 1, message = "pageSize 必须 >= 1")
  @Max(value = 200, message = "pageSize 不能超过 200")
  private int pageSize = 10;

  public int offset() {
    return Math.max(0, (page - 1) * pageSize);
  }
}

