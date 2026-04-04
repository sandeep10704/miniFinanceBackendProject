package com.sandeep.simplebackend.Finance.service;


import com.sandeep.simplebackend.Finance.dto.CreateRecordRequest;
import com.sandeep.simplebackend.Finance.dto.FinancialRecordDTO;
import com.sandeep.simplebackend.Finance.entity.FinancialRecord;
import com.sandeep.simplebackend.Finance.entity.User;
import com.sandeep.simplebackend.Finance.repository.FinancialRecordRepository;
import com.sandeep.simplebackend.Finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FinancialRecordServiceImpl implements FinancialRecordService {

    private final FinancialRecordRepository repo;
    private final UserRepository userRepo;

    @Override
    public FinancialRecordDTO create(CreateRecordRequest request) {

        User user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

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
    public List<FinancialRecordDTO> getAll() {
        return repo.findAll().stream()
                .filter(r -> !Boolean.TRUE.equals(r.getIsDeleted()))
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public FinancialRecordDTO getById(Long id) {
        FinancialRecord r = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));
        return mapToDTO(r);
    }

    @Override
    public FinancialRecordDTO update(Long id, CreateRecordRequest request) {

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
    public void delete(Long id) {
        FinancialRecord r = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        r.setIsDeleted(true);
        repo.save(r);
    }

    @Override
    public List<FinancialRecordDTO> filter(String type, String category) {
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
