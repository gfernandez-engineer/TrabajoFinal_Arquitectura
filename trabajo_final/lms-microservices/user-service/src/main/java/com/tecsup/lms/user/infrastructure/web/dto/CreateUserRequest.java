package com.tecsup.lms.user.infrastructure.web.dto;

import lombok.Data;

@Data
public class CreateUserRequest {
    private String fullName;
    private String email;
}
