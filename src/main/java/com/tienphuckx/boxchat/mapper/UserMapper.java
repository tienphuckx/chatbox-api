package com.tienphuckx.boxchat.mapper;

import com.tienphuckx.boxchat.model.User;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserMapper {

    // Insert a new user
    @Insert("INSERT INTO users (username, user_code, created_at) VALUES (#{username}, #{userCode}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertUser(User user);

    // Find user by ID
    @Select("SELECT * FROM users WHERE id = #{id}")
    User findUserById(Integer id);

    // Find user by username
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findUserByUsername(String username);

    @Select("SELECT * FROM users WHERE user_code = #{userCode}")
    User findUserByCode(String userCode);
}
