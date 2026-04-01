package com.example.libraryms.dto.borrow;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReaderReturnRequest {
  @NotNull(message = "recordId 不能为空")
  private Long recordId;
}

