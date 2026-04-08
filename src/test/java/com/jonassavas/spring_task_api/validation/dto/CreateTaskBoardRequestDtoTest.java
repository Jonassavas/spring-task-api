package com.jonassavas.spring_task_api.validation.dto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jonassavas.spring_task_api.domain.dto.task_board.CreateTaskBoardRequestDto;
import jakarta.validation.ConstraintViolation;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class CreateTaskBoardRequestDtoTest extends BaseValidationTest {

    @Test
    void shouldFailWhenNameBlank() {
        CreateTaskBoardRequestDto dto =
                CreateTaskBoardRequestDto.builder().taskBoardName("").build();

        Set<ConstraintViolation<CreateTaskBoardRequestDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenNameTooLong() {
        CreateTaskBoardRequestDto dto =
                CreateTaskBoardRequestDto.builder().taskBoardName("a".repeat(60)).build();

        Set<ConstraintViolation<CreateTaskBoardRequestDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldPassWhenValidName() {
        CreateTaskBoardRequestDto dto =
                CreateTaskBoardRequestDto.builder().taskBoardName("My Task Board").build();

        Set<ConstraintViolation<CreateTaskBoardRequestDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }
}
