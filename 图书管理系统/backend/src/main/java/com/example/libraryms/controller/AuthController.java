package com.example.libraryms.controller;

import com.example.libraryms.common.ApiResponse;
import com.example.libraryms.dto.auth.ChangePasswordRequest;
import com.example.libraryms.dto.auth.LoginRequest;
import com.example.libraryms.dto.auth.LoginResponse;
import com.example.libraryms.entity.SysUser;
import com.example.libraryms.mapper.SysUserMapper;
import com.example.libraryms.security.AuthPrincipal;
import com.example.libraryms.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;
  private final SysUserMapper userMapper;

  public AuthController(AuthService authService, SysUserMapper userMapper) {
    this.authService = authService;
    this.userMapper = userMapper;
  }

  @PostMapping("/login")
  public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
    String token = authService.login(req.getUsername(), req.getPassword());
    SysUser u = userMapper.findByUsername(req.getUsername());
    return ApiResponse.ok(new LoginResponse(token, u.getId(), u.getUsername(), u.getRole()));
  }

  @GetMapping("/me")
  public ApiResponse<AuthPrincipal> me(@AuthenticationPrincipal AuthPrincipal principal) {
    return ApiResponse.ok(principal);
  }

  @PostMapping("/change-password")
  public ApiResponse<Void> changePassword(@AuthenticationPrincipal AuthPrincipal principal,
                                          @Valid @RequestBody ChangePasswordRequest req) {
    authService.changePassword(principal.id(), req.getOldPassword(), req.getNewPassword());
    return ApiResponse.ok();
  }
}

