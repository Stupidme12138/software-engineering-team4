package com.example.libraryms.dto.borrow;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BorrowRequest {
  @NotNull(message = "readerId 不能为空")
  private Long readerId;
  @NotNull(message = "bookId 不能为空")
  private Long bookId;
  private LocalDateTime dueTime;
  private String remark;
}

