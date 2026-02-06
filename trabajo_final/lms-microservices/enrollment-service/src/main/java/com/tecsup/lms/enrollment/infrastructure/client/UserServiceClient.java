package com.tecsup.lms.enrollment.infrastructure.client;

import com.tecsup.lms.shared.dto.UserValidationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.user.url}")
    private String userServiceUrl;

    public UserValidationResponse validateUser(Long userId) {
        try {
            log.info("Validating user {} at {}", userId, userServiceUrl);

            UserValidationResponse response = restTemplate.getForObject(
                    userServiceUrl + "/api/users/" + userId + "/validate",
                    UserValidationResponse.class
            );

            log.info("User validation response: {}", response);
            return response;

        } catch (RestClientException e) {
            log.error("Error calling user-service: {}", e.getMessage());
            return UserValidationResponse.invalid("User service unavailable: " + e.getMessage());
        }
    }
}
