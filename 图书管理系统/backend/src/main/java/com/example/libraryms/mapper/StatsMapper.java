package com.example.libraryms.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StatsMapper {
  long countBooks(@Param("status") String status);
  long sumAvailableBooks();
  long countReaders(@Param("status") String status);
  long countBorrowRecords(@Param("status") String status);
}

