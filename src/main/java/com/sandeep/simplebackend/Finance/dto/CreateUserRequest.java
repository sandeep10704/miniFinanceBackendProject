package com.sandeep.simplebackend.Finance.dto;



import lombok.Data;

@Data
public class CreateUserRequest {

    private String username;
    private String email;
    private String password;
    private Long roleId;
}
