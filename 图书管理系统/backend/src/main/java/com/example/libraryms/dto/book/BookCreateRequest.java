package com.example.libraryms.dto.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookCreateRequest {
  private String isbn;

  @NotBlank(message = "书名不能为空")
  private String title;

  private String author;
  private String publisher;
  private LocalDate publishDate;
  private Long categoryId;
  private String subject;
  private String shelfLocation;

  @Min(value = 1, message = "馆藏数量必须 >= 1")
  private Integer totalQty = 1;
}

