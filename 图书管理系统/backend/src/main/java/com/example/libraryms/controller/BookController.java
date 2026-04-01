package com.example.libraryms.controller;

import com.example.libraryms.common.ApiResponse;
import com.example.libraryms.common.PageResult;
import com.example.libraryms.dto.book.BookCreateRequest;
import com.example.libraryms.dto.book.BookUpdateRequest;
import com.example.libraryms.entity.Book;
import com.example.libraryms.service.BookService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class BookController {
  private final BookService bookService;

  public BookController(BookService bookService) {
    this.bookService = bookService;
  }

  @GetMapping("/{id}")
  public ApiResponse<Book> get(@PathVariable Long id) {
    return ApiResponse.ok(bookService.get(id));
  }

  @GetMapping
  public ApiResponse<PageResult<Book>> page(@RequestParam(required = false) String keyword,
                                            @RequestParam(required = false) Long categoryId,
                                            @RequestParam(required = false) String subject,
                                            @RequestParam(required = false) String status,
                                            @RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int pageSize) {
    return ApiResponse.ok(bookService.page(keyword, categoryId, subject, status, page, pageSize));
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
  public ApiResponse<Long> create(@Valid @RequestBody BookCreateRequest req) {
    Book b = new Book();
    b.setIsbn(req.getIsbn());
    b.setTitle(req.getTitle());
    b.setAuthor(req.getAuthor());
    b.setPublisher(req.getPublisher());
    b.setPublishDate(req.getPublishDate());
    b.setCategoryId(req.getCategoryId());
    b.setSubject(req.getSubject());
    b.setShelfLocation(req.getShelfLocation());
    b.setTotalQty(req.getTotalQty());
    return ApiResponse.ok(bookService.create(b));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
  public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody BookUpdateRequest req) {
    Book b = new Book();
    b.setIsbn(req.getIsbn());
    b.setTitle(req.getTitle());
    b.setAuthor(req.getAuthor());
    b.setPublisher(req.getPublisher());
    b.setPublishDate(req.getPublishDate());
    b.setCategoryId(req.getCategoryId());
    b.setSubject(req.getSubject());
    b.setShelfLocation(req.getShelfLocation());
    b.setTotalQty(req.getTotalQty());
    b.setStatus(req.getStatus());
    bookService.update(id, b);
    return ApiResponse.ok();
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ApiResponse<Void> delete(@PathVariable Long id) {
    bookService.delete(id);
    return ApiResponse.ok();
  }
}

