package com.jonassavas.spring_task_api.validation.dto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jonassavas.spring_task_api.domain.dto.auth.RegisterRequest;
import jakarta.validation.ConstraintViolation;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class RegisterRequestDtoTest extends BaseValidationTest {

    @Test
    void shouldFailWhenEmailInvalid() {
        RegisterRequest dto =
                RegisterRequest.builder()
                        .username("john")
                        .email("invalid-email")
                        .password("password123")
                        .build();

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenPasswordTooShort() {
        RegisterRequest dto =
                RegisterRequest.builder()
                        .username("john")
                        .email("john@email.com")
                        .password("123")
                        .build();

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldPassWhenValid() {
        RegisterRequest dto =
                RegisterRequest.builder()
                        .username("john")
                        .email("john@email.com")
                        .password("password123")
                        .build();

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }
}
