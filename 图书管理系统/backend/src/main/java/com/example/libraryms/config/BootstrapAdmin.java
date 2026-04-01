package com.example.libraryms.config;

import com.example.libraryms.entity.SysUser;
import com.example.libraryms.mapper.SysUserMapper;
import com.example.libraryms.security.UserRole;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BootstrapAdmin implements ApplicationRunner {
  private final SysUserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  public BootstrapAdmin(SysUserMapper userMapper, PasswordEncoder passwordEncoder) {
    this.userMapper = userMapper;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void run(ApplicationArguments args) {
    if (userMapper.countAll() > 0) return;

    SysUser admin = new SysUser();
    admin.setUsername("admin");
    admin.setPasswordHash(passwordEncoder.encode("Admin@123456"));
    admin.setRole(UserRole.ADMIN.name());
    admin.setEnabled(true);
    userMapper.insert(admin);
  }
}

