package com.example.libraryms.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BorrowRecord {
  private Long id;
  private Long readerId;
  private Long bookId;
  private LocalDateTime borrowTime;
  private LocalDateTime dueTime;
  private LocalDateTime returnTime;
  private String status; // BORROWED / RETURNED
  private Long operatorUserId;
  private String remark;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}

