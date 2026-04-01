package com.example.libraryms.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
  private int code;
  private String message;
  private T data;

  public static <T> ApiResponse<T> ok(T data) {
    return new ApiResponse<>(0, "OK", data);
  }

  public static ApiResponse<Void> ok() {
    return new ApiResponse<>(0, "OK", null);
  }

  public static <T> ApiResponse<T> fail(int code, String message) {
    return new ApiResponse<>(code, message, null);
  }
}

