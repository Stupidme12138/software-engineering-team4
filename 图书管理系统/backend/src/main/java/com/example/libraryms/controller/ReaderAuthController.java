package com.example.libraryms.controller;

import com.example.libraryms.common.ApiResponse;
import com.example.libraryms.common.BizException;
import com.example.libraryms.common.ErrorCodes;
import com.example.libraryms.dto.auth.ChangePasswordRequest;
import com.example.libraryms.dto.readerAuth.ReaderLoginRequest;
import com.example.libraryms.dto.readerAuth.ReaderLoginResponse;
import com.example.libraryms.dto.readerAuth.ReaderRegisterRequest;
import com.example.libraryms.entity.Reader;
import com.example.libraryms.mapper.ReaderMapper;
import com.example.libraryms.security.AuthPrincipal;
import com.example.libraryms.security.JwtService;
import com.example.libraryms.security.UserRole;
import com.example.libraryms.service.ReaderService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reader-auth")
public class ReaderAuthController {
  private final ReaderMapper readerMapper;
  private final ReaderService readerService;
  private final JwtService jwtService;

  public ReaderAuthController(ReaderMapper readerMapper, ReaderService readerService, JwtService jwtService) {
    this.readerMapper = readerMapper;
    this.readerService = readerService;
    this.jwtService = jwtService;
  }

  @PostMapping("/login")
  public ApiResponse<ReaderLoginResponse> login(@Valid @RequestBody ReaderLoginRequest req) {
    readerService.loginByCardNo(req.getCardNo(), req.getPassword());
    Reader r = readerMapper.findByCardNo(req.getCardNo());
    if (r == null) throw new BizException(ErrorCodes.USERNAME_OR_PASSWORD_INVALID, "证号或密码错误");
    String token = jwtService.generateToken(r.getId(), r.getCardNo(), UserRole.READER.name());
    return ApiResponse.ok(new ReaderLoginResponse(token, r.getId(), r.getCardNo(), r.getName(), UserRole.READER.name()));
  }

  @PostMapping("/register")
  public ApiResponse<ReaderLoginResponse> register(@Valid @RequestBody ReaderRegisterRequest req) {
    Reader r = new Reader();
    r.setCardNo(req.getCardNo());
    r.setName(req.getName());
    r.setPhone(req.getPhone());
    r.setEmail(req.getEmail());
    r.setStatus("ACTIVE");
    r.setPasswordHash(req.getPassword()); // ReaderService.create 内会 BCrypt 编码
    Long id = readerService.create(r);
    String token = jwtService.generateToken(id, req.getCardNo(), UserRole.READER.name());
    return ApiResponse.ok(new ReaderLoginResponse(token, id, req.getCardNo(), req.getName(), UserRole.READER.name()));
  }

  @GetMapping("/me")
  public ApiResponse<AuthPrincipal> me(@AuthenticationPrincipal AuthPrincipal principal) {
    return ApiResponse.ok(principal);
  }

  @PostMapping("/change-password")
  public ApiResponse<Void> changePassword(@AuthenticationPrincipal AuthPrincipal principal,
                                          @Valid @RequestBody ChangePasswordRequest req) {
    if (!UserRole.READER.name().equalsIgnoreCase(principal.role())) {
      throw new BizException(ErrorCodes.FORBIDDEN, "仅读者可调用此接口");
    }
    readerService.changePassword(principal.id(), req.getOldPassword(), req.getNewPassword());
    return ApiResponse.ok();
  }
}

