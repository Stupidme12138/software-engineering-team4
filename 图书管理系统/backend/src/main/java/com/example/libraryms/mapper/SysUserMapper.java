package com.example.libraryms.mapper;

import com.example.libraryms.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysUserMapper {
  SysUser findByUsername(@Param("username") String username);
  SysUser findById(@Param("id") Long id);
  long countAll();
  int insert(SysUser user);
  int update(SysUser user);
  int deleteById(@Param("id") Long id);

  long count(@Param("keyword") String keyword);
  List<SysUser> page(@Param("keyword") String keyword,
                     @Param("offset") int offset,
                     @Param("limit") int limit);
}

