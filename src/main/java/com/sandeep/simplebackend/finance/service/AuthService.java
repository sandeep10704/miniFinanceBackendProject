package com.sandeep.simplebackend.finance.service;

import com.sandeep.simplebackend.finance.dto.LoginRequest;
import com.sandeep.simplebackend.finance.entity.User;
import com.sandeep.simplebackend.finance.repository.UserRepository;
import com.sandeep.simplebackend.finance.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;

    public String login(LoginRequest request) {

        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPasswordHash().equals(request.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().getName()
        );
    }
}
