package com.tienphuckx.boxchat.mapper;

import com.tienphuckx.boxchat.dto.response.JoinedMemberDto;
import com.tienphuckx.boxchat.model.Participant;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ParticipantMapper {

    // Add a user to a group
    @Insert("INSERT INTO participants (user_id, group_id) VALUES (#{userId}, #{groupId})")
    void addParticipant(@Param("userId") Integer userId, @Param("groupId") Integer groupId);

    // Check if a user is in a group
    @Select("SELECT COUNT(*) FROM participants WHERE user_id = #{userId} AND group_id = #{groupId}")
    boolean isUserInGroup(@Param("userId") Integer userId, @Param("groupId") Integer groupId);

    // Find all participants in a group
    @Select("SELECT * FROM participants WHERE group_id = #{groupId}")
    List<Participant> findParticipantsByGroupId(Integer groupId);


    @Select("""
        SELECT u.id AS memberId, u.username AS memberName
        FROM participants p
        JOIN users u ON p.user_id = u.id
        WHERE p.group_id = #{groupId}
    """)
    List<JoinedMemberDto> findJoinedMembersByGroupId(@Param("groupId") Integer groupId);

    @Select("SELECT COUNT(*) FROM participants WHERE group_id = #{groupId}")
    Integer countJoinedMembersByGroupId(@Param("groupId") Integer groupId);

    @Delete("""
        DELETE FROM participants WHERE user_id = #{memberId} AND group_id = #{groupId}
    """)
    void deleteUserFromGroup(Integer memberId, Integer groupId);


    }
