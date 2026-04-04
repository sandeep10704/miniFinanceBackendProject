package com.sandeep.simplebackend.finance.dto;



import lombok.Data;

@Data
public class DashboardSummaryDTO {

    private Double totalIncome;
    private Double totalExpense;
    private Double netBalance;
}
