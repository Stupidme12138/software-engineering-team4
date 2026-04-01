package com.example.libraryms.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Book {
  private Long id;
  private String isbn;
  private String title;
  private String author;
  private String publisher;
  private LocalDate publishDate;
  private Long categoryId;
  private String subject;
  private String shelfLocation;
  private Integer totalQty;
  private Integer availableQty;
  private String status; // ENABLED / DISABLED
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}

