package com.sandeep.simplebackend.finance.service;





import com.sandeep.simplebackend.finance.dto.CreateRecordRequest;
import com.sandeep.simplebackend.finance.dto.FinancialRecordDTO;

import java.util.List;

public interface FinancialRecordService {

    FinancialRecordDTO create(String token, CreateRecordRequest request);
    List<FinancialRecordDTO> getAll(String token);
    FinancialRecordDTO getById(String token, Long id);
    FinancialRecordDTO update(String token, Long id, CreateRecordRequest request);
    void delete(String token, Long id);
    List<FinancialRecordDTO> filter(String token, String type, String category);
}
