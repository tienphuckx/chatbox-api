package com.tienphuckx.boxchat.mapper;

import com.tienphuckx.boxchat.model.Message;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface MessageMapper {

    @Insert("INSERT INTO messages (content, message_type, created_at, user_id, group_id, message_uuid) " +
            "VALUES (#{content}, #{messageType}, NOW(), #{userId}, #{groupId}, #{messageUuid})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertMessage(Message message);



    // Find all messages in a group
    @Select("SELECT * FROM messages WHERE group_id = #{groupId} ORDER BY created_at ASC")
    List<Message> findMessagesByGroupId(Integer groupId);

    // Find all messages sent by a user
    @Select("SELECT * FROM messages WHERE user_id = #{userId} ORDER BY created_at ASC")
    List<Message> findMessagesByUserId(Integer userId);

    @Select("""
        SELECT * 
        FROM messages 
        WHERE group_id = #{groupId} 
        ORDER BY created_at DESC 
        LIMIT #{limit} OFFSET #{offset}
    """)
    List<Message> findMessagesByGroupIdWithPagination(@Param("groupId") Integer groupId,
                                                      @Param("offset") Integer offset,
                                                      @Param("limit") Integer limit);

    @Delete("DELETE FROM messages WHERE group_id = #{id}")
    void deleteAllMessageOfGroup(Integer id);
}
