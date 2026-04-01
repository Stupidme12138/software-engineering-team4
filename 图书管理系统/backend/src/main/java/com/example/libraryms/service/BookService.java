package com.example.libraryms.service;

import com.example.libraryms.common.BizException;
import com.example.libraryms.common.ErrorCodes;
import com.example.libraryms.common.PageResult;
import com.example.libraryms.entity.Book;
import com.example.libraryms.mapper.BookCategoryMapper;
import com.example.libraryms.mapper.BookMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {
  private final BookMapper bookMapper;
  private final BookCategoryMapper categoryMapper;

  public BookService(BookMapper bookMapper, BookCategoryMapper categoryMapper) {
    this.bookMapper = bookMapper;
    this.categoryMapper = categoryMapper;
  }

  public Book get(Long id) {
    Book b = bookMapper.findById(id);
    if (b == null) throw new BizException(ErrorCodes.DATA_NOT_FOUND, "图书不存在");
    return b;
  }

  public PageResult<Book> page(String keyword, Long categoryId, String subject, String status, int page, int pageSize) {
    int offset = Math.max(0, (page - 1) * pageSize);
    long total = bookMapper.count(keyword, categoryId, subject, status);
    var items = bookMapper.page(keyword, categoryId, subject, status, offset, pageSize);
    return new PageResult<>(total, items);
  }

  @Transactional
  public Long create(Book b) {
    if (b.getIsbn() != null && b.getIsbn().isBlank()) b.setIsbn(null);
    if (b.getStatus() == null || b.getStatus().isBlank()) b.setStatus("ENABLED");
    if (b.getTotalQty() == null || b.getTotalQty() < 1) b.setTotalQty(1);
    b.setAvailableQty(b.getTotalQty());
    if (b.getCategoryId() != null && categoryMapper.findById(b.getCategoryId()) == null) {
      throw new BizException(ErrorCodes.BAD_REQUEST, "分类不存在，请先创建分类或清空分类字段");
    }
    if (b.getIsbn() != null && !b.getIsbn().isBlank() && bookMapper.findByIsbn(b.getIsbn()) != null) {
      throw new BizException(ErrorCodes.DATA_CONFLICT, "ISBN 已存在");
    }
    bookMapper.insert(b);
    return b.getId();
  }

  @Transactional
  public void update(Long id, Book b) {
    Book old = bookMapper.findById(id);
    if (old == null) throw new BizException(ErrorCodes.DATA_NOT_FOUND, "图书不存在");
    if (b.getIsbn() != null && b.getIsbn().isBlank()) b.setIsbn(null);
    if (b.getCategoryId() != null && categoryMapper.findById(b.getCategoryId()) == null) {
      throw new BizException(ErrorCodes.BAD_REQUEST, "分类不存在，请先创建分类或清空分类字段");
    }
    if (b.getIsbn() != null && !b.getIsbn().isBlank()) {
      Book byIsbn = bookMapper.findByIsbn(b.getIsbn());
      if (byIsbn != null && !byIsbn.getId().equals(id)) {
        throw new BizException(ErrorCodes.DATA_CONFLICT, "ISBN 已存在");
      }
    }
    b.setId(id);
    if (b.getStatus() == null || b.getStatus().isBlank()) b.setStatus(old.getStatus());

    Integer newTotal = b.getTotalQty();
    if (newTotal == null) newTotal = old.getTotalQty();
    int borrowed = Math.max(0, old.getTotalQty() - old.getAvailableQty());
    if (newTotal < borrowed) {
      throw new BizException(ErrorCodes.BAD_REQUEST, "馆藏数量不能小于已借出数量");
    }
    b.setTotalQty(newTotal);
    b.setAvailableQty(newTotal - borrowed);

    bookMapper.update(b);
  }

  @Transactional
  public void delete(Long id) {
    Book old = bookMapper.findById(id);
    if (old == null) throw new BizException(ErrorCodes.DATA_NOT_FOUND, "图书不存在");
    int borrowed = Math.max(0, old.getTotalQty() - old.getAvailableQty());
    if (borrowed > 0) throw new BizException(ErrorCodes.BAD_REQUEST, "该书仍有未归还记录，无法删除");
    bookMapper.deleteById(id);
  }
}

