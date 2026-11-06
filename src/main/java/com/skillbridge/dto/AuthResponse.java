package com.skillbridge.dto;

public record AuthResponse(
        String token,
        String type,
        Long id,
        String fullName,
        String email,
        String role,
        String message
) {
    public AuthResponse(String token, Long id, String fullName, String email, String role, String message) {
        this(token, "Bearer", id, fullName, email, role, message);
    }
}
