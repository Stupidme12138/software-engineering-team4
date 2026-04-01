package com.example.libraryms.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserDto {
  private Long id;
  private String username;
  private String role;
  private Boolean enabled;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}

