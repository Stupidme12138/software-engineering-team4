package com.example.libraryms.controller;

import com.example.libraryms.common.ApiResponse;
import com.example.libraryms.common.PageResult;
import com.example.libraryms.dto.user.ResetPasswordRequest;
import com.example.libraryms.dto.user.UserCreateRequest;
import com.example.libraryms.dto.user.UserDto;
import com.example.libraryms.dto.user.UserUpdateRequest;
import com.example.libraryms.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public ApiResponse<PageResult<UserDto>> page(@RequestParam(required = false) String keyword,
                                               @RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "10") int pageSize) {
    return ApiResponse.ok(userService.page(keyword, page, pageSize));
  }

  @PostMapping
  public ApiResponse<Long> create(@Valid @RequestBody UserCreateRequest req) {
    return ApiResponse.ok(userService.create(req.getUsername(), req.getPassword(), req.getRole(), req.getEnabled()));
  }

  @PutMapping("/{id}")
  public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest req) {
    userService.update(id, req.getUsername(), req.getRole(), req.getEnabled());
    return ApiResponse.ok();
  }

  @PostMapping("/{id}/reset-password")
  public ApiResponse<Void> resetPassword(@PathVariable Long id, @Valid @RequestBody ResetPasswordRequest req) {
    userService.resetPassword(id, req.getNewPassword());
    return ApiResponse.ok();
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Void> delete(@PathVariable Long id) {
    userService.delete(id);
    return ApiResponse.ok();
  }
}

