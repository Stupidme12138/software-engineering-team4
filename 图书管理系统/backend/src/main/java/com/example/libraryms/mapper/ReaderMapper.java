package com.example.libraryms.mapper;

import com.example.libraryms.entity.Reader;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReaderMapper {
  Reader findById(@Param("id") Long id);
  Reader findByCardNo(@Param("cardNo") String cardNo);

  int insert(Reader reader);
  int update(Reader reader);
  int updatePassword(@Param("id") Long id, @Param("passwordHash") String passwordHash);

  long count(@Param("keyword") String keyword, @Param("status") String status);
  List<Reader> page(@Param("keyword") String keyword,
                    @Param("status") String status,
                    @Param("offset") int offset,
                    @Param("limit") int limit);
}

