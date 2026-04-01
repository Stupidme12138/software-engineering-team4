package com.example.libraryms.controller;

import com.example.libraryms.common.ApiResponse;
import com.example.libraryms.common.PageResult;
import com.example.libraryms.dto.reader.ReaderCreateRequest;
import com.example.libraryms.dto.reader.ReaderUpdateRequest;
import com.example.libraryms.entity.Reader;
import com.example.libraryms.service.ReaderService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/readers")
public class ReaderController {
  private final ReaderService readerService;

  public ReaderController(ReaderService readerService) {
    this.readerService = readerService;
  }

  @GetMapping("/{id}")
  public ApiResponse<Reader> get(@PathVariable Long id) {
    return ApiResponse.ok(readerService.get(id));
  }

  @GetMapping
  public ApiResponse<PageResult<Reader>> page(@RequestParam(required = false) String keyword,
                                              @RequestParam(required = false) String status,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int pageSize) {
    return ApiResponse.ok(readerService.page(keyword, status, page, pageSize));
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
  public ApiResponse<Long> create(@Valid @RequestBody ReaderCreateRequest req) {
    Reader r = new Reader();
    r.setCardNo(req.getCardNo());
    r.setName(req.getName());
    r.setPhone(req.getPhone());
    r.setEmail(req.getEmail());
    r.setIdCard(req.getIdCard());
    if (req.getPassword() != null && !req.getPassword().isBlank()) {
      // 由 ReaderService 内部编码为 BCrypt（若为空则默认 Reader@123456）
      r.setPasswordHash(req.getPassword());
    }
    return ApiResponse.ok(readerService.create(r));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
  public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody ReaderUpdateRequest req) {
    Reader r = new Reader();
    r.setCardNo(req.getCardNo());
    r.setName(req.getName());
    r.setPhone(req.getPhone());
    r.setEmail(req.getEmail());
    r.setIdCard(req.getIdCard());
    r.setStatus(req.getStatus());
    readerService.update(id, r);
    return ApiResponse.ok();
  }

  @PostMapping("/{id}/cancel")
  @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
  public ApiResponse<Void> cancel(@PathVariable Long id) {
    readerService.cancel(id);
    return ApiResponse.ok();
  }
}

