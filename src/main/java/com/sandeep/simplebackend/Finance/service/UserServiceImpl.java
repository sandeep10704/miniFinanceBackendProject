package com.sandeep.simplebackend.Finance.service;

import com.sandeep.simplebackend.Finance.dto.CreateUserRequest;
import com.sandeep.simplebackend.Finance.dto.UserDTO;
import com.sandeep.simplebackend.Finance.entity.Role;
import com.sandeep.simplebackend.Finance.entity.User;
import com.sandeep.simplebackend.Finance.repository.RoleRepository;
import com.sandeep.simplebackend.Finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;

    @Override
    public UserDTO createUser(CreateUserRequest request) {

        Role role = roleRepo.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(request.getPassword()); // later hash it
        user.setRole(role);
        user.setStatus("ACTIVE");

        return mapToDTO(userRepo.save(user));
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepo.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToDTO(user);
    }

    @Override
    public UserDTO updateUser(Long id, CreateUserRequest request) {

        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        return mapToDTO(userRepo.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        userRepo.deleteById(id);
    }

    private UserDTO mapToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().getName());
        dto.setStatus(user.getStatus());
        return dto;
    }
}