package com.sandeep.simplebackend.Finance.dto;



import lombok.Data;

@Data
public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private String role;
    private String status;
}
