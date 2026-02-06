package com.tecsup.lms.user.application;

import com.tecsup.lms.user.domain.model.User;
import com.tecsup.lms.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .fullName("Juan Perez")
                .email("juan@test.com")
                .status(User.UserStatus.ACTIVE)
                .build();
    }

    @Test
    void shouldSaveUser() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userRepository.save(testUser);

        assertNotNull(result);
        assertEquals("Juan Perez", result.getFullName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldFindUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> result = userRepository.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Juan Perez", result.get().getFullName());
    }

    @Test
    void shouldReturnEmptyWhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<User> result = userRepository.findById(99L);

        assertFalse(result.isPresent());
    }

    @Test
    void shouldFindAllUsers() {
        User user2 = User.builder()
                .id(2L)
                .fullName("Maria Garcia")
                .email("maria@test.com")
                .build();

        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, user2));

        List<User> result = userRepository.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void shouldCheckIfEmailExists() {
        when(userRepository.existsByEmail("juan@test.com")).thenReturn(true);
        when(userRepository.existsByEmail("noexiste@test.com")).thenReturn(false);

        assertTrue(userRepository.existsByEmail("juan@test.com"));
        assertFalse(userRepository.existsByEmail("noexiste@test.com"));
    }
}
