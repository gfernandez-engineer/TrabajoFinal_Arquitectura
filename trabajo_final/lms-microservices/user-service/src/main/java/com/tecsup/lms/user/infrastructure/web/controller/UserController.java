package com.tecsup.lms.user.infrastructure.web.controller;

import com.tecsup.lms.shared.dto.UserValidationResponse;
import com.tecsup.lms.user.domain.model.User;
import com.tecsup.lms.user.domain.repository.UserRepository;
import com.tecsup.lms.user.infrastructure.web.dto.CreateUserRequest;
import com.tecsup.lms.user.infrastructure.web.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest request) {
        log.info("Creating user: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .status(User.UserStatus.ACTIVE)
                .build();

        User saved = userRepository.save(user);
        log.info("User created with ID: {}", saved.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserResponse.fromEntity(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(UserResponse.fromEntity(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/validate")
    public ResponseEntity<UserValidationResponse> validateUser(@PathVariable Long id) {
        log.info("Validating user: {}", id);

        return userRepository.findById(id)
                .map(user -> {
                    if (user.isActive()) {
                        return ResponseEntity.ok(
                                UserValidationResponse.valid(user.getId(), user.getFullName(), user.getEmail())
                        );
                    } else {
                        return ResponseEntity.ok(
                                UserValidationResponse.invalid("User is not active: " + user.getStatus())
                        );
                    }
                })
                .orElse(ResponseEntity.ok(
                        UserValidationResponse.invalid("User not found with ID: " + id)
                ));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userRepository.findAll().stream()
                .map(UserResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody CreateUserRequest request) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setFullName(request.getFullName());
                    user.setEmail(request.getEmail());
                    User saved = userRepository.save(user);
                    return ResponseEntity.ok(UserResponse.fromEntity(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
