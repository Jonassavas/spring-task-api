package com.jonassavas.spring_task_api.domain.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTaskRequestDto {
    @NotBlank(message = "Task name is required")
    @Size(max = 100, message = "Task name must be between 1-100 characters")
    private String taskName;
}
