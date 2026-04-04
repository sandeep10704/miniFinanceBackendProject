package com.sandeep.simplebackend.Finance.service;



import com.sandeep.simplebackend.Finance.dto.CreateUserRequest;
import com.sandeep.simplebackend.Finance.dto.UserDTO;

import java.util.List;

public interface UserService {

    UserDTO createUser(CreateUserRequest request);
    List<UserDTO> getAllUsers();
    UserDTO getUserById(Long id);
    UserDTO updateUser(Long id, CreateUserRequest request);
    void deleteUser(Long id);
}
