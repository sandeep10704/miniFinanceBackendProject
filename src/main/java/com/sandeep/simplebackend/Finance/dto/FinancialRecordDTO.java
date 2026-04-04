package com.sandeep.simplebackend.Finance.dto;


import lombok.Data;

import java.time.LocalDate;

@Data
public class FinancialRecordDTO {

    private Long id;
    private Double amount;
    private String type;
    private String category;
    private String description;
    private LocalDate date;
}
