package com.example.libraryms.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateRequest {
  @NotBlank(message = "用户名不能为空")
  private String username;
  @NotBlank(message = "角色不能为空")
  private String role;
  private Boolean enabled;
}

