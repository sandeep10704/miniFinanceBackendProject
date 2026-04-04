package com.sandeep.simplebackend.finance.service;



import com.sandeep.simplebackend.finance.dto.CreateUserRequest;
import com.sandeep.simplebackend.finance.dto.UserDTO;

import java.util.List;

public interface UserService {

    UserDTO createUser(String token, CreateUserRequest request);
    List<UserDTO> getAllUsers(String token);
    UserDTO getUserById(String token, Long id);
    UserDTO updateUser(String token, Long id, CreateUserRequest request);
    void deleteUser(String token, Long id);
}
