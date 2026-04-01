package com.example.libraryms.mapper;

import com.example.libraryms.entity.BorrowRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BorrowRecordMapper {
  BorrowRecord findById(@Param("id") Long id);
  BorrowRecord findActiveByReaderAndBook(@Param("readerId") Long readerId, @Param("bookId") Long bookId);

  int insert(BorrowRecord record);
  int update(BorrowRecord record);

  long count(@Param("readerId") Long readerId,
             @Param("bookId") Long bookId,
             @Param("status") String status);

  List<BorrowRecord> page(@Param("readerId") Long readerId,
                          @Param("bookId") Long bookId,
                          @Param("status") String status,
                          @Param("offset") int offset,
                          @Param("limit") int limit);

  long countByReader(@Param("readerId") Long readerId);
  List<BorrowRecord> pageByReader(@Param("readerId") Long readerId,
                                  @Param("offset") int offset,
                                  @Param("limit") int limit);
}

