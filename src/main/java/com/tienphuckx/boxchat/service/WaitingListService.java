package com.tienphuckx.boxchat.service;

import com.tienphuckx.boxchat.mapper.WaitingListMapper;
import com.tienphuckx.boxchat.model.WaitingList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WaitingListService {

    private final WaitingListMapper waitingListMapper;

    @Autowired
    public WaitingListService(WaitingListMapper waitingListMapper) {
        this.waitingListMapper = waitingListMapper;
    }

    // Thêm người dùng vào danh sách chờ
    public void addToWaitingList(Integer userId, Integer groupId, String message) {
        waitingListMapper.addToWaitingList(userId, groupId, message);
    }

    // Lấy danh sách chờ của một người dùng
    public List<WaitingList> getWaitingListByUserId(Integer userId) {
        return waitingListMapper.findWaitingListByUserId(userId);
    }

    // Xóa một mục cụ thể khỏi danh sách chờ
    public void deleteFromWaitingList(Integer userId, Integer groupId) {
        waitingListMapper.deleteFromWaitingList(userId, groupId);
    }

    // Xóa tất cả các mục trong danh sách chờ của một người dùng
    public void deleteAllForUser(Integer userId) {
        waitingListMapper.deleteAllForUser(userId);
    }
}
