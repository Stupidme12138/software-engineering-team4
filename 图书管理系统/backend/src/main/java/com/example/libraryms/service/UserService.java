package com.example.libraryms.service;

import com.example.libraryms.common.BizException;
import com.example.libraryms.common.ErrorCodes;
import com.example.libraryms.common.PageResult;
import com.example.libraryms.dto.user.UserDto;
import com.example.libraryms.entity.SysUser;
import com.example.libraryms.mapper.SysUserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
  private final SysUserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  public UserService(SysUserMapper userMapper, PasswordEncoder passwordEncoder) {
    this.userMapper = userMapper;
    this.passwordEncoder = passwordEncoder;
  }

  public PageResult<UserDto> page(String keyword, int page, int pageSize) {
    int offset = Math.max(0, (page - 1) * pageSize);
    long total = userMapper.count(keyword);
    List<SysUser> items = userMapper.page(keyword, offset, pageSize);
    List<UserDto> dto = items.stream()
        .map(u -> new UserDto(u.getId(), u.getUsername(), u.getRole(), u.getEnabled(), u.getCreatedAt(), u.getUpdatedAt()))
        .toList();
    return new PageResult<>(total, dto);
  }

  @Transactional
  public Long create(String username, String rawPassword, String role, Boolean enabled) {
    if (userMapper.findByUsername(username) != null) {
      throw new BizException(ErrorCodes.DATA_CONFLICT, "用户名已存在");
    }
    SysUser u = new SysUser();
    u.setUsername(username);
    u.setPasswordHash(passwordEncoder.encode(rawPassword));
    u.setRole(role);
    u.setEnabled(enabled == null ? Boolean.TRUE : enabled);
    userMapper.insert(u);
    return u.getId();
  }

  @Transactional
  public void update(Long id, String username, String role, Boolean enabled) {
    SysUser old = userMapper.findById(id);
    if (old == null) throw new BizException(ErrorCodes.DATA_NOT_FOUND, "用户不存在");
    SysUser byName = userMapper.findByUsername(username);
    if (byName != null && !byName.getId().equals(id)) {
      throw new BizException(ErrorCodes.DATA_CONFLICT, "用户名已存在");
    }
    old.setUsername(username);
    old.setRole(role);
    if (enabled != null) old.setEnabled(enabled);
    userMapper.update(old);
  }

  @Transactional
  public void resetPassword(Long id, String newPassword) {
    SysUser old = userMapper.findById(id);
    if (old == null) throw new BizException(ErrorCodes.DATA_NOT_FOUND, "用户不存在");
    old.setPasswordHash(passwordEncoder.encode(newPassword));
    userMapper.update(old);
  }

  @Transactional
  public void delete(Long id) {
    SysUser old = userMapper.findById(id);
    if (old == null) throw new BizException(ErrorCodes.DATA_NOT_FOUND, "用户不存在");
    userMapper.deleteById(id);
  }
}

