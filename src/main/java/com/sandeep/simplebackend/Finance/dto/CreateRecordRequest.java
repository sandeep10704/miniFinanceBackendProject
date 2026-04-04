package com.sandeep.simplebackend.Finance.dto;


import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateRecordRequest {

    private Double amount;
    private String type;
    private String category;
    private String description;
    private LocalDate date;
    private Long userId;
}
