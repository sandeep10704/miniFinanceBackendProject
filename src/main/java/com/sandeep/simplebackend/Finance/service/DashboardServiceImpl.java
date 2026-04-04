package com.sandeep.simplebackend.Finance.service;



import com.sandeep.simplebackend.Finance.dto.FinancialRecordDTO;
import com.sandeep.simplebackend.Finance.entity.FinancialRecord;
import com.sandeep.simplebackend.Finance.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final FinancialRecordRepository repo;

    @Override
    public Map<String, Object> getSummary() {

        List<FinancialRecord
                > records = repo.findAll().stream()
                .filter(r -> !Boolean.TRUE.equals(r.getIsDeleted()))
                .toList();

        double income = records.stream()
                .filter(r -> r.getType().equalsIgnoreCase("INCOME"))
                .mapToDouble(FinancialRecord::getAmount)
                .sum();

        double expense = records.stream()
                .filter(r -> r.getType().equalsIgnoreCase("EXPENSE"))
                .mapToDouble(FinancialRecord::getAmount)
                .sum();

        Map<String, Object> result = new HashMap<>();
        result.put("totalIncome", income);
        result.put("totalExpense", expense);
        result.put("netBalance", income - expense);

        return result;
    }

    @Override
    public List<Map<String, Object>> getCategoryTotals() {

        List<FinancialRecord> records = repo.findAll().stream()
                .filter(r -> !Boolean.TRUE.equals(r.getIsDeleted()))
                .toList();

        Map<String, Double> grouped = records.stream()
                .collect(Collectors.groupingBy(
                        FinancialRecord::getCategory,
                        Collectors.summingDouble(FinancialRecord::getAmount)
                ));

        return grouped.entrySet().stream()
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("category", e.getKey());
                    map.put("total", e.getValue());
                    return map;
                })
                .toList();
    }

    @Override
    public List<FinancialRecordDTO> getRecent() {

        return repo.findAll().stream()
                .filter(r -> !Boolean.TRUE.equals(r.getIsDeleted()))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(5)
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<Map<String, Object>> getMonthlyTrend() {

        List<FinancialRecord> records = repo.findAll().stream()
                .filter(r -> !Boolean.TRUE.equals(r.getIsDeleted()))
                .toList();

        Map<String, Double> grouped = records.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getDate().getMonth().toString(),
                        Collectors.summingDouble(FinancialRecord::getAmount)
                ));

        return grouped.entrySet().stream()
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("month", e.getKey());
                    map.put("total", e.getValue());
                    return map;
                })
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