package com.example.libraryms.entity;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

@Data
public class Reader {
  private Long id;
  private String cardNo;
  @JsonIgnore
  private String passwordHash;
  private String name;
  private String phone;
  private String email;
  private String idCard;
  private String status; // ACTIVE / CANCELLED
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}

