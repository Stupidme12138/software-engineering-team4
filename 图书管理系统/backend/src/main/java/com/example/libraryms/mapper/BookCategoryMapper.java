package com.example.libraryms.mapper;

import com.example.libraryms.entity.BookCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookCategoryMapper {
  BookCategory findById(@Param("id") Long id);
  BookCategory findByName(@Param("name") String name);

  int insert(BookCategory category);
  int update(BookCategory category);
  int deleteById(@Param("id") Long id);

  List<BookCategory> listAll();

  long count(@Param("keyword") String keyword, @Param("subject") String subject);
  List<BookCategory> page(@Param("keyword") String keyword,
                          @Param("subject") String subject,
                          @Param("offset") int offset,
                          @Param("limit") int limit);
}

