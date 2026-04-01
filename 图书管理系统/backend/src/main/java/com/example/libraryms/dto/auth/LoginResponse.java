package com.example.libraryms.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
  private String accessToken;
  private Long userId;
  private String username;
  private String role;
}

