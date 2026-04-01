package com.example.libraryms.service;

import com.example.libraryms.common.BizException;
import com.example.libraryms.common.ErrorCodes;
import com.example.libraryms.common.PageResult;
import com.example.libraryms.entity.Reader;
import com.example.libraryms.mapper.ReaderMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReaderService {
  private final ReaderMapper readerMapper;
  private final PasswordEncoder passwordEncoder;

  public ReaderService(ReaderMapper readerMapper, PasswordEncoder passwordEncoder) {
    this.readerMapper = readerMapper;
    this.passwordEncoder = passwordEncoder;
  }

  public Reader get(Long id) {
    Reader r = readerMapper.findById(id);
    if (r == null) throw new BizException(ErrorCodes.DATA_NOT_FOUND, "读者不存在");
    return r;
  }

  public PageResult<Reader> page(String keyword, String status, int page, int pageSize) {
    int offset = Math.max(0, (page - 1) * pageSize);
    long total = readerMapper.count(keyword, status);
    var items = readerMapper.page(keyword, status, offset, pageSize);
    return new PageResult<>(total, items);
  }

  @Transactional
  public Long create(Reader r) {
    if (r.getStatus() == null || r.getStatus().isBlank()) r.setStatus("ACTIVE");
    if (readerMapper.findByCardNo(r.getCardNo()) != null) {
      throw new BizException(ErrorCodes.DATA_CONFLICT, "读者证号已存在");
    }
    String raw = r.getPasswordHash();
    if (raw == null || raw.isBlank()) raw = "Reader@123456";
    r.setPasswordHash(passwordEncoder.encode(raw));
    readerMapper.insert(r);
    return r.getId();
  }

  @Transactional
  public void update(Long id, Reader r) {
    Reader old = readerMapper.findById(id);
    if (old == null) throw new BizException(ErrorCodes.DATA_NOT_FOUND, "读者不存在");
    Reader byCard = readerMapper.findByCardNo(r.getCardNo());
    if (byCard != null && !byCard.getId().equals(id)) {
      throw new BizException(ErrorCodes.DATA_CONFLICT, "读者证号已存在");
    }
    r.setId(id);
    // 防止更新时把密码覆盖掉
    r.setPasswordHash(old.getPasswordHash());
    readerMapper.update(r);
  }

  @Transactional
  public void cancel(Long id) {
    Reader r = readerMapper.findById(id);
    if (r == null) throw new BizException(ErrorCodes.DATA_NOT_FOUND, "读者不存在");
    r.setStatus("CANCELLED");
    readerMapper.update(r);
  }

  @Transactional
  public String loginByCardNo(String cardNo, String password) {
    Reader r = readerMapper.findByCardNo(cardNo);
    if (r == null || !passwordEncoder.matches(password, r.getPasswordHash())) {
      throw new BizException(ErrorCodes.USERNAME_OR_PASSWORD_INVALID, "证号或密码错误");
    }
    if ("CANCELLED".equalsIgnoreCase(r.getStatus())) {
      throw new BizException(ErrorCodes.FORBIDDEN, "读者已注销，无法登录");
    }
    return "OK";
  }

  @Transactional
  public void changePassword(Long readerId, String oldPassword, String newPassword) {
    Reader r = readerMapper.findById(readerId);
    if (r == null) throw new BizException(ErrorCodes.DATA_NOT_FOUND, "读者不存在");
    if (!passwordEncoder.matches(oldPassword, r.getPasswordHash())) {
      throw new BizException(ErrorCodes.BAD_REQUEST, "旧密码不正确");
    }
    readerMapper.updatePassword(readerId, passwordEncoder.encode(newPassword));
  }
}

