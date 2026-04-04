package com.sandeep.simplebackend.finance.service;

import com.sandeep.simplebackend.finance.dto.CreateUserRequest;
import com.sandeep.simplebackend.finance.dto.UserDTO;
import com.sandeep.simplebackend.finance.entity.Role;
import com.sandeep.simplebackend.finance.entity.User;
import com.sandeep.simplebackend.finance.repository.RoleRepository;
import com.sandeep.simplebackend.finance.repository.UserRepository;
import com.sandeep.simplebackend.finance.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final JwtUtil jwtUtil;

    // ✅ ROLE CHECK
    private void checkAdmin(User user) {
        if (!user.getRole().getName().equalsIgnoreCase("ADMIN")) {
            throw new RuntimeException("Access denied: Admin only");
        }
    }

    // ✅ TOKEN → USER
    private User getUserFromToken(String token) {
        String email = jwtUtil.extractEmail(token);
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ✅ SIMPLE VALIDATION
    private void validateRequest(CreateUserRequest request) {

        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new RuntimeException("Username is required");
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new RuntimeException("Email is required");
        }

        if (request.getPassword() == null || request.getPassword().length() < 3) {
            throw new RuntimeException("Password must be at least 3 characters");
        }
    }

    @Override
    public UserDTO createUser(String token, CreateUserRequest request) {

        User loggedUser = getUserFromToken(token);
        checkAdmin(loggedUser);

        validateRequest(request);


        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        Role role = roleRepo.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(request.getPassword());
        user.setRole(role);
        user.setStatus("ACTIVE");

        return mapToDTO(userRepo.save(user));
    }

    @Override
    public List<UserDTO> getAllUsers(String token) {

        User loggedUser = getUserFromToken(token);

        String role = loggedUser.getRole().getName();
        if (!role.equalsIgnoreCase("ADMIN") && !role.equalsIgnoreCase("ANALYST")) {
            throw new RuntimeException("Access denied");
        }

        return userRepo.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public UserDTO getUserById(String token, Long id) {

        User loggedUser = getUserFromToken(token);

        String role = loggedUser.getRole().getName();
        if (!role.equalsIgnoreCase("ADMIN") && !role.equalsIgnoreCase("ANALYST")) {
            throw new RuntimeException("Access denied");
        }

        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return mapToDTO(user);
    }

    @Override
    public UserDTO updateUser(String token, Long id, CreateUserRequest request) {

        User loggedUser = getUserFromToken(token);
        checkAdmin(loggedUser);

        validateRequest(request);

        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        return mapToDTO(userRepo.save(user));
    }

    @Override
    public void deleteUser(String token, Long id) {

        User loggedUser = getUserFromToken(token);
        checkAdmin(loggedUser);

        if (!userRepo.existsById(id)) {
            throw new RuntimeException("User not found");
        }

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