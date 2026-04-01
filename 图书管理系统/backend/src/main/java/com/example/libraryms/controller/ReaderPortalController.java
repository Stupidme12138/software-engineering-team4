package com.example.libraryms.controller;

import com.example.libraryms.common.ApiResponse;
import com.example.libraryms.common.PageResult;
import com.example.libraryms.dto.borrow.BorrowRequest;
import com.example.libraryms.dto.borrow.ReaderReturnRequest;
import com.example.libraryms.entity.BorrowRecord;
import com.example.libraryms.security.AuthPrincipal;
import com.example.libraryms.security.UserRole;
import com.example.libraryms.service.BorrowService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 读者端接口（前台）
 */
@RestController
@RequestMapping("/api/reader")
@PreAuthorize("hasRole('READER')")
public class ReaderPortalController {
  private final BorrowService borrowService;

  public ReaderPortalController(BorrowService borrowService) {
    this.borrowService = borrowService;
  }

  @GetMapping("/borrows")
  public ApiResponse<PageResult<BorrowRecord>> myBorrows(@AuthenticationPrincipal AuthPrincipal principal,
                                                         @RequestParam(required = false) String status,
                                                         @RequestParam(defaultValue = "1") int page,
                                                         @RequestParam(defaultValue = "10") int pageSize) {
    return ApiResponse.ok(borrowService.page(principal.id(), null, status, page, pageSize));
  }

  @PostMapping("/borrow")
  public ApiResponse<Long> borrow(@AuthenticationPrincipal AuthPrincipal principal,
                                  @Valid @RequestBody BorrowRequest req) {
    // 读者端：忽略传入 readerId，强制使用 token 中的读者ID
    if (!UserRole.READER.name().equalsIgnoreCase(principal.role())) {
      return ApiResponse.fail(403, "无权限");
    }
    return ApiResponse.ok(borrowService.borrow(principal.id(), req.getBookId(), req.getDueTime(), null, req.getRemark()));
  }

  @PostMapping("/return")
  public ApiResponse<Void> returnBook(@AuthenticationPrincipal AuthPrincipal principal,
                                      @Valid @RequestBody ReaderReturnRequest req) {
    borrowService.returnBookByReader(req.getRecordId(), principal.id());
    return ApiResponse.ok();
  }
}

