package com.example.libraryms.service;

import com.example.libraryms.common.BizException;
import com.example.libraryms.common.ErrorCodes;
import com.example.libraryms.common.PageResult;
import com.example.libraryms.entity.BookCategory;
import com.example.libraryms.mapper.BookCategoryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {
  private final BookCategoryMapper categoryMapper;

  public CategoryService(BookCategoryMapper categoryMapper) {
    this.categoryMapper = categoryMapper;
  }

  public BookCategory get(Long id) {
    BookCategory c = categoryMapper.findById(id);
    if (c == null) throw new BizException(ErrorCodes.DATA_NOT_FOUND, "分类不存在");
    return c;
  }

  public List<BookCategory> listAll() {
    return categoryMapper.listAll();
  }

  public PageResult<BookCategory> page(String keyword, String subject, int page, int pageSize) {
    int offset = Math.max(0, (page - 1) * pageSize);
    long total = categoryMapper.count(keyword, subject);
    var items = categoryMapper.page(keyword, subject, offset, pageSize);
    return new PageResult<>(total, items);
  }

  @Transactional
  public Long create(BookCategory c) {
    if (categoryMapper.findByName(c.getName()) != null) {
      throw new BizException(ErrorCodes.DATA_CONFLICT, "分类名称已存在");
    }
    categoryMapper.insert(c);
    return c.getId();
  }

  @Transactional
  public void update(Long id, BookCategory c) {
    BookCategory old = categoryMapper.findById(id);
    if (old == null) throw new BizException(ErrorCodes.DATA_NOT_FOUND, "分类不存在");
    BookCategory byName = categoryMapper.findByName(c.getName());
    if (byName != null && !byName.getId().equals(id)) {
      throw new BizException(ErrorCodes.DATA_CONFLICT, "分类名称已存在");
    }
    c.setId(id);
    categoryMapper.update(c);
  }

  @Transactional
  public void delete(Long id) {
    BookCategory old = categoryMapper.findById(id);
    if (old == null) throw new BizException(ErrorCodes.DATA_NOT_FOUND, "分类不存在");
    categoryMapper.deleteById(id);
  }
}

