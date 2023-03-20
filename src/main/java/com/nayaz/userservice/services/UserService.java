package com.nayaz.userservice.services;

import com.nayaz.userservice.entities.User;

import java.util.List;

public interface UserService {

    User saveUser(User user);

    List<User> getAllUsers();

    User getUserById(String usedId);

    User updateUser(String userId, User user);

    void deleteUser(String userId);


}
