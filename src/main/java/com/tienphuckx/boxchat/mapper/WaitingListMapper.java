package com.tienphuckx.boxchat.mapper;

import com.tienphuckx.boxchat.dto.response.WaitingMemberDto;
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

    @Select("""
        SELECT u.id AS memberId, u.username AS memberName, w.message AS memberMessage
        FROM waiting_list w
        JOIN users u ON w.user_id = u.id
        WHERE w.group_id = #{groupId}
    """)
    List<WaitingMemberDto> findWaitingMembersByGroupId(@Param("groupId") Integer groupId);



    @Select("SELECT COUNT(*) FROM waiting_list WHERE group_id = #{groupId}")
    Integer countWaitingMembersByGroupId(@Param("groupId") Integer groupId);
}

