package com.jonassavas.spring_task_api.validation.dto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jonassavas.spring_task_api.domain.dto.task.CreateTaskRequestDto;
import jakarta.validation.ConstraintViolation;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class CreateTaskRequestDtoTest extends BaseValidationTest {

    @Test
    void shouldFailWhenNameBlank() {
        CreateTaskRequestDto dto = CreateTaskRequestDto.builder().taskName("").build();

        Set<ConstraintViolation<CreateTaskRequestDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenNameTooLong() {
        CreateTaskRequestDto dto = CreateTaskRequestDto.builder().taskName("a".repeat(120)).build();

        Set<ConstraintViolation<CreateTaskRequestDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldPassWhenValidName() {
        CreateTaskRequestDto dto =
                CreateTaskRequestDto.builder().taskName("Implement login endpoint").build();

        Set<ConstraintViolation<CreateTaskRequestDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }
}
