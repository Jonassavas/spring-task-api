package com.jonassavas.spring_task_api.validation.dto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jonassavas.spring_task_api.domain.dto.auth.LoginRequest;
import jakarta.validation.ConstraintViolation;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class LoginRequestDtoTest extends BaseValidationTest {

    @Test
    void shouldFailWhenUsernameBlank() {
        LoginRequest dto = LoginRequest.builder().username("").password("password").build();

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldPassWhenValid() {
        LoginRequest dto = LoginRequest.builder().username("john").password("password").build();

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }
}
