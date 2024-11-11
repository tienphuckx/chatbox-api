package com.tienphuckx.boxchat.mapper;

import com.tienphuckx.boxchat.model.WaitingList;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface WaitingListMapper {
    // Add a user to the waiting list
    @Insert("INSERT INTO waiting_list (user_id, group_id, message, created_at) VALUES (#{userId}, #{groupId}, #{message}, NOW())")
    void addToWaitingList(@Param("userId") Integer userId, @Param("groupId") Integer groupId, @Param("message") String message);

    // Find waiting list entries by user ID
    @Select("SELECT * FROM waiting_list WHERE user_id = #{userId}")
    List<WaitingList> findWaitingListByUserId(@Param("userId") Integer userId);

    // Delete a specific entry from the waiting list
    @Delete("DELETE FROM waiting_list WHERE user_id = #{userId} AND group_id = #{groupId}")
    void deleteFromWaitingList(@Param("userId") Integer userId, @Param("groupId") Integer groupId);

    // Delete all entries for a user
    @Delete("DELETE FROM waiting_list WHERE user_id = #{userId}")
    void deleteAllForUser(@Param("userId") Integer userId);
}

