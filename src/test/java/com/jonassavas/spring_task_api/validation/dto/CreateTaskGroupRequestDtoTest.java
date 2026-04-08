package com.jonassavas.spring_task_api.validation.dto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jonassavas.spring_task_api.domain.dto.task_group.CreateTaskGroupRequestDto;
import jakarta.validation.ConstraintViolation;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class CreateTaskGroupRequestDtoTest extends BaseValidationTest {

    @Test
    void shouldFailWhenNameBlank() {
        CreateTaskGroupRequestDto dto =
                CreateTaskGroupRequestDto.builder().taskGroupName("").build();

        Set<ConstraintViolation<CreateTaskGroupRequestDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenNameTooLong() {
        CreateTaskGroupRequestDto dto =
                CreateTaskGroupRequestDto.builder().taskGroupName("a".repeat(60)).build();

        Set<ConstraintViolation<CreateTaskGroupRequestDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldPassWhenValidName() {
        CreateTaskGroupRequestDto dto =
                CreateTaskGroupRequestDto.builder().taskGroupName("Todo").build();

        Set<ConstraintViolation<CreateTaskGroupRequestDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }
}
