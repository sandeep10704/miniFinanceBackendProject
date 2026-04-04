package com.sandeep.simplebackend.finance.controller;

import com.sandeep.simplebackend.finance.dto.CreateRecordRequest;
import com.sandeep.simplebackend.finance.dto.FinancialRecordDTO;
import com.sandeep.simplebackend.finance.service.FinancialRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class FinancialRecordController {

    private final FinancialRecordService recordService;

    // ✅ Extract token
    private String extractToken(String header) {
        return header.substring(7);
    }


    @PostMapping
    public ResponseEntity<FinancialRecordDTO> create(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CreateRecordRequest request) {

        String token = extractToken(authHeader);
        return ResponseEntity.ok(recordService.create(token, request));
    }

    // 🔥 GET ALL (optional token usage)
    @GetMapping
    public ResponseEntity<List<FinancialRecordDTO>> getAll(
            @RequestHeader("Authorization") String authHeader) {

        String token = extractToken(authHeader);
        return ResponseEntity.ok(recordService.getAll(token));
    }

    // 🔥 GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<FinancialRecordDTO> getById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {

        String token = extractToken(authHeader);
        return ResponseEntity.ok(recordService.getById(token, id));
    }

    // 🔥 UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<FinancialRecordDTO> update(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @RequestBody CreateRecordRequest request) {

        String token = extractToken(authHeader);
        return ResponseEntity.ok(recordService.update(token, id, request));
    }

    // 🔥 DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {

        String token = extractToken(authHeader);
        recordService.delete(token, id);
        return ResponseEntity.ok("Deleted successfully");
    }


    @GetMapping("/filter")
    public ResponseEntity<List<FinancialRecordDTO>> filter(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category) {

        String token = extractToken(authHeader);
        return ResponseEntity.ok(recordService.filter(token, type, category));
    }
}