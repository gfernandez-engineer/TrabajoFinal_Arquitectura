package com.tecsup.lms.user.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void shouldCreateUserWithActiveStatus() {
        User user = User.builder()
                .fullName("Juan Perez")
                .email("juan@test.com")
                .build();

        assertEquals("Juan Perez", user.getFullName());
        assertEquals("juan@test.com", user.getEmail());
        assertEquals(User.UserStatus.ACTIVE, user.getStatus());
    }

    @Test
    void shouldCreateUserWithSuspendedStatus() {
        User user = User.builder()
                .fullName("Juan Perez")
                .email("juan@test.com")
                .status(User.UserStatus.SUSPENDED)
                .build();

        assertEquals(User.UserStatus.SUSPENDED, user.getStatus());
        assertFalse(user.isActive());
    }

    @Test
    void shouldCheckIfUserIsActive() {
        User activeUser = User.builder()
                .fullName("Juan Perez")
                .email("juan@test.com")
                .status(User.UserStatus.ACTIVE)
                .build();

        User suspendedUser = User.builder()
                .fullName("Maria Garcia")
                .email("maria@test.com")
                .status(User.UserStatus.SUSPENDED)
                .build();

        User inactiveUser = User.builder()
                .fullName("Carlos Lopez")
                .email("carlos@test.com")
                .status(User.UserStatus.INACTIVE)
                .build();

        assertTrue(activeUser.isActive());
        assertFalse(suspendedUser.isActive());
        assertFalse(inactiveUser.isActive());
    }

    @Test
    void shouldHaveCorrectEnumValues() {
        assertEquals(3, User.UserStatus.values().length);
        assertEquals(User.UserStatus.ACTIVE, User.UserStatus.valueOf("ACTIVE"));
        assertEquals(User.UserStatus.SUSPENDED, User.UserStatus.valueOf("SUSPENDED"));
        assertEquals(User.UserStatus.INACTIVE, User.UserStatus.valueOf("INACTIVE"));
    }

    @Test
    void shouldAllowUpdatingUserFields() {
        User user = User.builder()
                .fullName("Juan Perez")
                .email("juan@test.com")
                .build();

        user.setFullName("Juan Carlos Perez");
        user.setEmail("juancarlos@test.com");

        assertEquals("Juan Carlos Perez", user.getFullName());
        assertEquals("juancarlos@test.com", user.getEmail());
    }
}
