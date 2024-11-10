package com.tienphuckx.boxchat.mapper;

import com.tienphuckx.boxchat.model.Group;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface GroupMapper {

    @Insert("INSERT INTO groups (name, group_code, user_id, approval_require, expired_at, maximum_members, created_at) " +
            "VALUES (#{name}, #{groupCode}, #{userId}, #{approvalRequire}, #{expiredAt}, #{maximumMembers}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertGroup(Group group);

    // Find group by ID
    @Select("SELECT * FROM groups WHERE id = #{id}")
    Group findGroupById(Integer id);

    // Find group by group code
    @Select("SELECT * FROM groups WHERE group_code = #{groupCode}")
    Group findGroupByCode(String groupCode);

    // Find all groups created by a user
    @Select("SELECT * FROM groups WHERE user_id = #{userId}")
    List<Group> findGroupsByUserId(Integer userId);

    // Find groups joined by a user
    @Select("""
        SELECT g.* 
        FROM groups g
        JOIN participants p ON g.id = p.group_id
        WHERE p.user_id = #{userId}
    """)
    List<Group> findJoinedGroupsByUserId(@Param("userId") Integer userId);

    // Find groups where the user is in the waiting list
    @Select("""
        SELECT g.* 
        FROM groups g
        JOIN waiting_list w ON g.id = w.group_id
        WHERE w.user_id = #{userId}
    """)
    List<Group> findWaitingGroupsByUserId(@Param("userId") Integer userId);
}
