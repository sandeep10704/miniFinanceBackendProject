package com.sandeep.simplebackend.finance.service;



import com.sandeep.simplebackend.finance.dto.FinancialRecordDTO;

import java.util.List;
import java.util.Map;

public interface DashboardService {

    Map<String, Object> getSummary();
    List<Map<String, Object>> getCategoryTotals();
    List<FinancialRecordDTO> getRecent();
    List<Map<String, Object>> getMonthlyTrend();
}
