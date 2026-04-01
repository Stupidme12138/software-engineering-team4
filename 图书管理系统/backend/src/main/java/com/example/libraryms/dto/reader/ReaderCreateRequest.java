package com.example.libraryms.dto.reader;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReaderCreateRequest {
  @NotBlank(message = "读者证号不能为空")
  private String cardNo;
  @NotBlank(message = "姓名不能为空")
  private String name;
  // 为空则使用默认初始密码：Reader@123456
  private String password;
  private String phone;
  private String email;
  private String idCard;
}

