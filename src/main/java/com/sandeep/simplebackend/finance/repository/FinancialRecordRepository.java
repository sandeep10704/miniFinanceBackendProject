package com.sandeep.simplebackend.finance.repository;


import com.sandeep.simplebackend.finance.entity.FinancialRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

    List<FinancialRecord> findByUserId(Long userId);

    List<FinancialRecord> findByType(String type);

    List<FinancialRecord> findByCategory(String category);
}
