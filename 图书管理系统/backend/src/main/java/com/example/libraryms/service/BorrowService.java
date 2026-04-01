package com.example.libraryms.service;

import com.example.libraryms.common.BizException;
import com.example.libraryms.common.ErrorCodes;
import com.example.libraryms.common.PageResult;
import com.example.libraryms.entity.Book;
import com.example.libraryms.entity.BorrowRecord;
import com.example.libraryms.entity.Reader;
import com.example.libraryms.mapper.BookMapper;
import com.example.libraryms.mapper.BorrowRecordMapper;
import com.example.libraryms.mapper.ReaderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class BorrowService {
  private final BorrowRecordMapper recordMapper;
  private final ReaderMapper readerMapper;
  private final BookMapper bookMapper;

  public BorrowService(BorrowRecordMapper recordMapper, ReaderMapper readerMapper, BookMapper bookMapper) {
    this.recordMapper = recordMapper;
    this.readerMapper = readerMapper;
    this.bookMapper = bookMapper;
  }

  public PageResult<BorrowRecord> page(Long readerId, Long bookId, String status, int page, int pageSize) {
    int offset = Math.max(0, (page - 1) * pageSize);
    long total = recordMapper.count(readerId, bookId, status);
    var items = recordMapper.page(readerId, bookId, status, offset, pageSize);
    return new PageResult<>(total, items);
  }

  public PageResult<BorrowRecord> readerHistory(Long readerId, int page, int pageSize) {
    int offset = Math.max(0, (page - 1) * pageSize);
    long total = recordMapper.countByReader(readerId);
    var items = recordMapper.pageByReader(readerId, offset, pageSize);
    return new PageResult<>(total, items);
  }

  @Transactional
  public Long borrow(Long readerId, Long bookId, LocalDateTime dueTime, Long operatorUserId, String remark) {
    Reader reader = readerMapper.findById(readerId);
    if (reader == null) throw new BizException(ErrorCodes.DATA_NOT_FOUND, "读者不存在");
    if ("CANCELLED".equalsIgnoreCase(reader.getStatus())) {
      throw new BizException(ErrorCodes.READER_CANCELLED, "读者已注销，无法借阅");
    }
    Book book = bookMapper.findById(bookId);
    if (book == null) throw new BizException(ErrorCodes.DATA_NOT_FOUND, "图书不存在");

    BorrowRecord active = recordMapper.findActiveByReaderAndBook(readerId, bookId);
    if (active != null) {
      throw new BizException(ErrorCodes.BORROW_STATE_INVALID, "该读者已借阅此书且未归还");
    }

    int updated = bookMapper.decAvailable(bookId);
    if (updated != 1) {
      throw new BizException(ErrorCodes.BOOK_NOT_AVAILABLE, "该书库存不足或已下架");
    }

    BorrowRecord r = new BorrowRecord();
    r.setReaderId(readerId);
    r.setBookId(bookId);
    r.setBorrowTime(LocalDateTime.now());
    // 默认应还时间：借出后 30 天
    r.setDueTime(dueTime != null ? dueTime : LocalDateTime.now().plus(30, ChronoUnit.DAYS));
    r.setReturnTime(null);
    r.setStatus("BORROWED");
    r.setOperatorUserId(operatorUserId);
    r.setRemark(remark);
    recordMapper.insert(r);
    return r.getId();
  }

  @Transactional
  public void returnBook(Long recordId, Long operatorUserId) {
    BorrowRecord r = recordMapper.findById(recordId);
    if (r == null) throw new BizException(ErrorCodes.DATA_NOT_FOUND, "借阅记录不存在");
    if (!"BORROWED".equalsIgnoreCase(r.getStatus())) {
      throw new BizException(ErrorCodes.BORROW_STATE_INVALID, "该记录不是借出状态");
    }
    r.setStatus("RETURNED");
    r.setReturnTime(LocalDateTime.now());
    r.setOperatorUserId(operatorUserId);
    recordMapper.update(r);

    bookMapper.incAvailable(r.getBookId());
  }

  @Transactional
  public void returnBookByReader(Long recordId, Long readerId) {
    BorrowRecord r = recordMapper.findById(recordId);
    if (r == null) throw new BizException(ErrorCodes.DATA_NOT_FOUND, "借阅记录不存在");
    if (!readerId.equals(r.getReaderId())) {
      throw new BizException(ErrorCodes.FORBIDDEN, "只能归还自己的借阅记录");
    }
    if (!"BORROWED".equalsIgnoreCase(r.getStatus())) {
      throw new BizException(ErrorCodes.BORROW_STATE_INVALID, "该记录不是借出状态");
    }
    r.setStatus("RETURNED");
    r.setReturnTime(LocalDateTime.now());
    r.setOperatorUserId(null);
    recordMapper.update(r);
    bookMapper.incAvailable(r.getBookId());
  }
}

