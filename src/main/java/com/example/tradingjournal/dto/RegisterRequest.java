package com.example.tradingjournal.dto;


import com.example.tradingjournal.model.enums.Role;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private Role role; // Optional
}
