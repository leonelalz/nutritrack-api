package com.nutritrack.nutritrackapi.dto.response;

import lombok.Builder;

@Builder
public record AuthResponse(
        String token,
        String type,
        String email,
        String name,
        String message
) {
    public AuthResponse(String token, String email, String name) {
        this(token, "Bearer", email, name,"");
    }
}
