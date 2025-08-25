package com.exchanger.service;

import com.exchanger.dto.UserDto;
import com.exchanger.dto.record.UserRecord;
import com.exchanger.model.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UUID createUser(UserRecord userRecord);
    String activateUser(String email, long chatId);
    void createDefaultWalletsForUser(User user);
    List<UserDto> getUsers();
    UserDto getUserById(UUID id);
    User findByEmail(String email);
    UserDto getUserByEmail(String email);
    void deleteUser(UUID id);

}
