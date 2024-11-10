package com.tienphuckx.boxchat.mapper;

import com.tienphuckx.boxchat.model.WaitingList;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface WaitingListMapper {
    // Add a user to the waiting list
    @Insert("INSERT INTO waiting_list (user_id, group_id, message, created_at) VALUES (#{userId}, #{groupId}, #{message}, NOW())")
    void addToWaitingList(@Param("userId") Integer userId, @Param("groupId") Integer groupId, @Param("message") String message);

    // Find waiting list entries by user ID
    @Select("SELECT * FROM waiting_list WHERE user_id = #{userId}")
    List<WaitingList> findWaitingListByUserId(@Param("userId") Integer userId);
}
