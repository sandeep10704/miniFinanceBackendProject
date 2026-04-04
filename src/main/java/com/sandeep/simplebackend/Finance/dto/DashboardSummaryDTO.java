package com.sandeep.simplebackend.Finance.dto;



import lombok.Data;

@Data
public class DashboardSummaryDTO {

    private Double totalIncome;
    private Double totalExpense;
    private Double netBalance;
}
