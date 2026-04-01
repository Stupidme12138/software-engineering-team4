package com.example.libraryms.mapper;

import com.example.libraryms.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookMapper {
  Book findById(@Param("id") Long id);
  Book findByIsbn(@Param("isbn") String isbn);

  int insert(Book book);
  int update(Book book);
  int deleteById(@Param("id") Long id);

  long count(@Param("keyword") String keyword,
             @Param("categoryId") Long categoryId,
             @Param("subject") String subject,
             @Param("status") String status);

  List<Book> page(@Param("keyword") String keyword,
                  @Param("categoryId") Long categoryId,
                  @Param("subject") String subject,
                  @Param("status") String status,
                  @Param("offset") int offset,
                  @Param("limit") int limit);

  int decAvailable(@Param("id") Long id);
  int incAvailable(@Param("id") Long id);
}

