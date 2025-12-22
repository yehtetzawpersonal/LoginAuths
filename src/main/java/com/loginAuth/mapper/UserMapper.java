package com.loginAuth.mapper;

import java.time.LocalDateTime;

import org.apache.ibatis.annotations.Mapper;

import com.loginAuth.dto.UserDto;

@Mapper
public interface UserMapper {

	UserDto findByEmail(String email);

	void save(UserDto user);

	UserDto findByToken(String token);

	void saveResetToken(String email, String token, LocalDateTime expiry);

	void updatePassword(String email, String password);

	void clearResetToken(String email);

}
