package com.example.libraryms.common;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BizException.class)
  @ResponseStatus(HttpStatus.OK)
  public ApiResponse<Void> handleBiz(BizException e) {
    return ApiResponse.fail(e.getCode(), e.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.OK)
  public ApiResponse<Void> handleInvalid(MethodArgumentNotValidException e) {
    String msg = e.getBindingResult().getFieldErrors().stream()
        .map(FieldError::getDefaultMessage)
        .collect(Collectors.joining("; "));
    return ApiResponse.fail(ErrorCodes.BAD_REQUEST, msg.isBlank() ? "参数不合法" : msg);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.OK)
  public ApiResponse<Void> handleConstraint(ConstraintViolationException e) {
    return ApiResponse.fail(ErrorCodes.BAD_REQUEST, e.getMessage());
  }

  @ExceptionHandler(AuthenticationException.class)
  @ResponseStatus(HttpStatus.OK)
  public ApiResponse<Void> handleAuth(AuthenticationException e) {
    return ApiResponse.fail(ErrorCodes.UNAUTHORIZED, "未登录或登录已过期");
  }

  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(HttpStatus.OK)
  public ApiResponse<Void> handleDenied(AccessDeniedException e) {
    return ApiResponse.fail(ErrorCodes.FORBIDDEN, "无权限访问");
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  @ResponseStatus(HttpStatus.OK)
  public ApiResponse<Void> handleDataIntegrity(DataIntegrityViolationException e) {
    // 常见：唯一约束/外键约束/非空约束等
    return ApiResponse.fail(ErrorCodes.DATA_CONFLICT, "数据约束冲突（请检查唯一性/外键/必填字段）");
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.OK)
  public ApiResponse<Void> handleOther(Exception e) {
    return ApiResponse.fail(500, "服务器内部错误");
  }
}

