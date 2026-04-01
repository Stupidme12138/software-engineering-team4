package com.example.libraryms.controller;

import com.example.libraryms.common.ApiResponse;
import com.example.libraryms.common.PageResult;
import com.example.libraryms.dto.borrow.BorrowRequest;
import com.example.libraryms.dto.borrow.ReturnRequest;
import com.example.libraryms.entity.BorrowRecord;
import com.example.libraryms.security.AuthPrincipal;
import com.example.libraryms.service.BorrowService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/borrows")
public class BorrowController {
  private final BorrowService borrowService;

  public BorrowController(BorrowService borrowService) {
    this.borrowService = borrowService;
  }

  @GetMapping
  public ApiResponse<PageResult<BorrowRecord>> page(@RequestParam(required = false) Long readerId,
                                                    @RequestParam(required = false) Long bookId,
                                                    @RequestParam(required = false) String status,
                                                    @RequestParam(defaultValue = "1") int page,
                                                    @RequestParam(defaultValue = "10") int pageSize) {
    return ApiResponse.ok(borrowService.page(readerId, bookId, status, page, pageSize));
  }

  @GetMapping("/reader/{readerId}")
  public ApiResponse<PageResult<BorrowRecord>> readerHistory(@PathVariable Long readerId,
                                                             @RequestParam(defaultValue = "1") int page,
                                                             @RequestParam(defaultValue = "10") int pageSize) {
    return ApiResponse.ok(borrowService.readerHistory(readerId, page, pageSize));
  }

  @PostMapping("/borrow")
  @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
  public ApiResponse<Long> borrow(@AuthenticationPrincipal AuthPrincipal principal,
                                  @Valid @RequestBody BorrowRequest req) {
    return ApiResponse.ok(
        borrowService.borrow(req.getReaderId(), req.getBookId(), req.getDueTime(), principal.id(), req.getRemark())
    );
  }

  @PostMapping("/return")
  @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
  public ApiResponse<Void> returnBook(@AuthenticationPrincipal AuthPrincipal principal,
                                      @Valid @RequestBody ReturnRequest req) {
    borrowService.returnBook(req.getRecordId(), principal.id());
    return ApiResponse.ok();
  }
}

