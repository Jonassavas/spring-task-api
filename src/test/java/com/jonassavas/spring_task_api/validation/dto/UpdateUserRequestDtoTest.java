package com.jonassavas.spring_task_api.validation.dto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jonassavas.spring_task_api.domain.dto.user.UpdateUserRequestDto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class UpdateUserRequestDtoTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldFailWhenEmailInvalid() {
        UpdateUserRequestDto dto = UpdateUserRequestDto.builder()
                .email("not-an-email")
                .build();

        Set<ConstraintViolation<UpdateUserRequestDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldPassWhenValidEmail() {
        UpdateUserRequestDto dto = UpdateUserRequestDto.builder()
                .email("test@email.com")
                .build();

        Set<ConstraintViolation<UpdateUserRequestDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }
}