package com.example.libraryms.dto.reader;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReaderUpdateRequest {
  @NotBlank(message = "读者证号不能为空")
  private String cardNo;
  @NotBlank(message = "姓名不能为空")
  private String name;
  private String phone;
  private String email;
  private String idCard;
  private String status; // ACTIVE / CANCELLED
}

