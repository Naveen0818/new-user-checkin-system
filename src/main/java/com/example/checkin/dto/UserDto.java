package com.example.checkin.dto;

import com.example.checkin.model.Role;
import lombok.Data;

@Data
public class UserDto {
    
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private Long managerId;
    private String managerName;
    private Long locationId;
    private String locationName;
}
