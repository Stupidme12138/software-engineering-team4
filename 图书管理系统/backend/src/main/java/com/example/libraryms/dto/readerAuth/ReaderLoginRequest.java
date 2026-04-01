package com.example.libraryms.dto.readerAuth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReaderLoginRequest {
  @NotBlank(message = "读者证号不能为空")
  private String cardNo;

  @NotBlank(message = "密码不能为空")
  private String password;
}

