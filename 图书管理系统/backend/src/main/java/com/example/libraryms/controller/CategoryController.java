package com.example.libraryms.controller;

import com.example.libraryms.common.ApiResponse;
import com.example.libraryms.common.PageResult;
import com.example.libraryms.dto.category.CategoryCreateRequest;
import com.example.libraryms.dto.category.CategoryUpdateRequest;
import com.example.libraryms.entity.BookCategory;
import com.example.libraryms.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
  private final CategoryService categoryService;

  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @GetMapping
  public ApiResponse<PageResult<BookCategory>> page(@RequestParam(required = false) String keyword,
                                                    @RequestParam(required = false) String subject,
                                                    @RequestParam(defaultValue = "1") int page,
                                                    @RequestParam(defaultValue = "10") int pageSize) {
    return ApiResponse.ok(categoryService.page(keyword, subject, page, pageSize));
  }

  @GetMapping("/all")
  public ApiResponse<List<BookCategory>> all() {
    return ApiResponse.ok(categoryService.listAll());
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
  public ApiResponse<Long> create(@Valid @RequestBody CategoryCreateRequest req) {
    BookCategory c = new BookCategory();
    c.setName(req.getName());
    c.setSubject(req.getSubject());
    c.setDescription(req.getDescription());
    return ApiResponse.ok(categoryService.create(c));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
  public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody CategoryUpdateRequest req) {
    BookCategory c = new BookCategory();
    c.setName(req.getName());
    c.setSubject(req.getSubject());
    c.setDescription(req.getDescription());
    categoryService.update(id, c);
    return ApiResponse.ok();
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ApiResponse<Void> delete(@PathVariable Long id) {
    categoryService.delete(id);
    return ApiResponse.ok();
  }
}

