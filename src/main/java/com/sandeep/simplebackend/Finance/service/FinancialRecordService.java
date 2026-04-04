package com.sandeep.simplebackend.Finance.service;





import com.sandeep.simplebackend.Finance.dto.CreateRecordRequest;
import com.sandeep.simplebackend.Finance.dto.FinancialRecordDTO;

import java.util.List;

public interface FinancialRecordService {

    FinancialRecordDTO create(CreateRecordRequest request);
    List<FinancialRecordDTO> getAll();
    FinancialRecordDTO getById(Long id);
    FinancialRecordDTO update(Long id, CreateRecordRequest request);
    void delete(Long id);
    List<FinancialRecordDTO> filter(String type, String category);
}
