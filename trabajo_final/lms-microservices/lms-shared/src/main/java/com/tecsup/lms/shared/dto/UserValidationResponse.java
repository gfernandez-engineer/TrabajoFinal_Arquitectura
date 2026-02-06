package com.tecsup.lms.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserValidationResponse {

    private Long userId;
    private String fullName;
    private String email;
    private String status;
    private boolean valid;
    private String message;

    public static UserValidationResponse valid(Long userId, String fullName, String email) {
        return UserValidationResponse.builder()
                .userId(userId)
                .fullName(fullName)
                .email(email)
                .status("ACTIVE")
                .valid(true)
                .build();
    }

    public static UserValidationResponse invalid(String message) {
        return UserValidationResponse.builder()
                .valid(false)
                .message(message)
                .build();
    }
}
