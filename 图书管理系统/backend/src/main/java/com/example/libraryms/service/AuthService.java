package com.example.libraryms.service;

import com.example.libraryms.common.BizException;
import com.example.libraryms.common.ErrorCodes;
import com.example.libraryms.entity.SysUser;
import com.example.libraryms.mapper.SysUserMapper;
import com.example.libraryms.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
  private final SysUserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public AuthService(SysUserMapper userMapper, PasswordEncoder passwordEncoder, JwtService jwtService) {
    this.userMapper = userMapper;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  public String login(String username, String password) {
    SysUser user = userMapper.findByUsername(username);
    if (user == null || !passwordEncoder.matches(password, user.getPasswordHash())) {
      throw new BizException(ErrorCodes.USERNAME_OR_PASSWORD_INVALID, "用户名或密码错误");
    }
    if (Boolean.FALSE.equals(user.getEnabled())) {
      throw new BizException(ErrorCodes.USER_DISABLED, "账号已被禁用");
    }
    return jwtService.generateToken(user.getId(), user.getUsername(), user.getRole());
  }

  @Transactional
  public void changePassword(Long userId, String oldPassword, String newPassword) {
    SysUser user = userMapper.findById(userId);
    if (user == null) {
      throw new BizException(ErrorCodes.DATA_NOT_FOUND, "用户不存在");
    }
    if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
      throw new BizException(ErrorCodes.BAD_REQUEST, "旧密码不正确");
    }
    user.setPasswordHash(passwordEncoder.encode(newPassword));
    userMapper.update(user);
  }
}

