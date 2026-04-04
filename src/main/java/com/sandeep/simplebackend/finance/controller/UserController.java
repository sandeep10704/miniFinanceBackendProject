package com.sandeep.simplebackend.finance.controller;

import com.sandeep.simplebackend.finance.dto.CreateUserRequest;
import com.sandeep.simplebackend.finance.dto.UserDTO;
import com.sandeep.simplebackend.finance.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ✅ Extract token
    private String extractToken(String header) {
        return header.substring(7);
    }


    @PostMapping
    public ResponseEntity<UserDTO> createUser(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CreateUserRequest request) {
            @Valid
        String token = extractToken(authHeader);
        return ResponseEntity.ok(userService.createUser(token, request));
    }


    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(
            @RequestHeader("Authorization") String authHeader) {

        String token = extractToken(authHeader);
        return ResponseEntity.ok(userService.getAllUsers(token));
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {

        String token = extractToken(authHeader);
        return ResponseEntity.ok(userService.getUserById(token, id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @RequestBody CreateUserRequest request) {
            @Valid
        String token = extractToken(authHeader);
        return ResponseEntity.ok(userService.updateUser(token, id, request));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {

        String token = extractToken(authHeader);
        userService.deleteUser(token, id);
        return ResponseEntity.ok("User deleted successfully");
    }
}