package com.sandeep.simplebackend.finance.service;

import com.sandeep.simplebackend.finance.dto.CreateRecordRequest;
import com.sandeep.simplebackend.finance.dto.FinancialRecordDTO;

import com.sandeep.simplebackend.finance.entity.FinancialRecord;
import com.sandeep.simplebackend.finance.entity.User;
import com.sandeep.simplebackend.finance.repository.FinancialRecordRepository;
import com.sandeep.simplebackend.finance.repository.UserRepository;
import com.sandeep.simplebackend.finance.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FinancialRecordServiceImpl implements FinancialRecordService {

    private final FinancialRecordRepository repo;
    private final UserRepository userRepo;
    private final JwtUtil jwtUtil; // ✅ NEW

    // ✅ ROLE CHECKS
    private void checkAdmin(User user) {
        if (!user.getRole().getName().equalsIgnoreCase("ADMIN")) {
            throw new RuntimeException("Access denied: Admin only");
        }
    }

    private void checkAnalystOrAdmin(User user) {
        String role = user.getRole().getName();
        if (!role.equalsIgnoreCase("ADMIN") && !role.equalsIgnoreCase("ANALYST")) {
            throw new RuntimeException("Access denied: Analyst/Admin only");
        }
    }

    // ✅ Extract user from token
    private User getUserFromToken(String token) {
        String email = jwtUtil.extractEmail(token);
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public FinancialRecordDTO create(String token, CreateRecordRequest request) {

        User user = getUserFromToken(token);
        checkAdmin(user); // 🔥 ADMIN only

        FinancialRecord r = new FinancialRecord();
        r.setAmount(request.getAmount());
        r.setType(request.getType());
        r.setCategory(request.getCategory());
        r.setDescription(request.getDescription());
        r.setDate(request.getDate());
        r.setUser(user);

        return mapToDTO(repo.save(r));
    }

    @Override
    public List<FinancialRecordDTO> getAll(String token) {

        User user = getUserFromToken(token);
        checkAnalystOrAdmin(user); // 🔥 ANALYST or ADMIN

        return repo.findAll().stream()
                .filter(r -> !Boolean.TRUE.equals(r.getIsDeleted()))
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public FinancialRecordDTO getById(String token, Long id) {

        User user = getUserFromToken(token);
        checkAnalystOrAdmin(user);

        FinancialRecord r = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        return mapToDTO(r);
    }

    @Override
    public FinancialRecordDTO update(String token, Long id, CreateRecordRequest request) {

        User user = getUserFromToken(token);
        checkAdmin(user);

        FinancialRecord r = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        r.setAmount(request.getAmount());
        r.setType(request.getType());
        r.setCategory(request.getCategory());
        r.setDescription(request.getDescription());
        r.setDate(request.getDate());

        return mapToDTO(repo.save(r));
    }

    @Override
    public void delete(String token, Long id) {

        User user = getUserFromToken(token);
        checkAdmin(user);

        FinancialRecord r = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        r.setIsDeleted(true);
        repo.save(r);
    }

    @Override
    public List<FinancialRecordDTO> filter(String token, String type, String category) {

        User user = getUserFromToken(token);
        checkAnalystOrAdmin(user);

        return repo.findAll().stream()
                .filter(r -> !Boolean.TRUE.equals(r.getIsDeleted()))
                .filter(r -> type == null || r.getType().equalsIgnoreCase(type))
                .filter(r -> category == null || r.getCategory().equalsIgnoreCase(category))
                .map(this::mapToDTO)
                .toList();
    }

    private FinancialRecordDTO mapToDTO(FinancialRecord r) {
        FinancialRecordDTO dto = new FinancialRecordDTO();
        dto.setId(r.getId());
        dto.setAmount(r.getAmount());
        dto.setType(r.getType());
        dto.setCategory(r.getCategory());
        dto.setDescription(r.getDescription());
        dto.setDate(r.getDate());
        return dto;
    }
}