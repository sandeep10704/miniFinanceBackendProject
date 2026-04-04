package com.sandeep.simplebackend.Finance.controller;

import com.sandeep.simplebackend.Finance.dto.CreateRecordRequest;
import com.sandeep.simplebackend.Finance.dto.FinancialRecordDTO;
import com.sandeep.simplebackend.Finance.service.FinancialRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class FinancialRecordController {

    private final FinancialRecordService recordService;

    @PostMapping
    public ResponseEntity<FinancialRecordDTO> create(@RequestBody CreateRecordRequest request) {
        return ResponseEntity.ok(recordService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<FinancialRecordDTO>> getAll() {
        return ResponseEntity.ok(recordService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FinancialRecordDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(recordService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FinancialRecordDTO> update(
            @PathVariable Long id,
            @RequestBody CreateRecordRequest request
    ) {
        return ResponseEntity.ok(recordService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        recordService.delete(id);
        return ResponseEntity.ok("Deleted successfully");
    }

    @GetMapping("/filter")
    public ResponseEntity<List<FinancialRecordDTO>> filter(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category
    ) {
        return ResponseEntity.ok(recordService.filter(type, category));
    }
}