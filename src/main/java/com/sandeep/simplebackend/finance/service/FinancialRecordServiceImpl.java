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
    private final JwtUtil jwtUtil;

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

    // ✅ TOKEN → USER
    private User getUserFromToken(String token) {
        String email = jwtUtil.extractEmail(token);
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ✅ VALIDATION (simple but enough)
    private void validateRequest(CreateRecordRequest request) {

        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new RuntimeException("Amount must be greater than 0");
        }

        if (request.getType() == null ||
                (!request.getType().equalsIgnoreCase("INCOME") &&
                        !request.getType().equalsIgnoreCase("EXPENSE"))) {
            throw new RuntimeException("Type must be INCOME or EXPENSE");
        }

        if (request.getCategory() == null || request.getCategory().isBlank()) {
            throw new RuntimeException("Category is required");
        }

        if (request.getDate() == null) {
            throw new RuntimeException("Date is required");
        }
    }

    @Override
    public FinancialRecordDTO create(String token, CreateRecordRequest request) {

        User user = getUserFromToken(token);
        checkAdmin(user);

        validateRequest(request);

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
        checkAnalystOrAdmin(user);

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

        if (Boolean.TRUE.equals(r.getIsDeleted())) {
            throw new RuntimeException("Record already deleted");
        }

        return mapToDTO(r);
    }

    @Override
    public FinancialRecordDTO update(String token, Long id, CreateRecordRequest request) {

        User user = getUserFromToken(token);
        checkAdmin(user);

        validateRequest(request);

        FinancialRecord r = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        if (Boolean.TRUE.equals(r.getIsDeleted())) {
            throw new RuntimeException("Cannot update deleted record");
        }

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

        if (Boolean.TRUE.equals(r.getIsDeleted())) {
            throw new RuntimeException("Record already deleted");
        }

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