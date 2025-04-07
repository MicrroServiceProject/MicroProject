package tn.esprit.backend.dto;

import lombok.Data;

@Data
public class UserDto {
    private String id;
    private String username;
    private String email;
    private boolean isAdmin;
}