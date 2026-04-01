package com.example.libraryms.dto.readerAuth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReaderLoginResponse {
  private String accessToken;
  private Long readerId;
  private String cardNo;
  private String name;
  private String role; // READER
}

