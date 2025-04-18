package com.tradingjournal.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private User user;
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class User{
        private String username;
        private String email;
        private String role;
        private Long user_id;
    }

}
