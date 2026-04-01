package com.example.libraryms.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookCategory {
  private Long id;
  private String name;
  private String subject;
  private String description;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}

