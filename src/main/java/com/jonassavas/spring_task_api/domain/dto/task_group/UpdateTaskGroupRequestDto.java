package com.jonassavas.spring_task_api.domain.dto.task_group;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateTaskGroupRequestDto {

    @Size(min = 1, max = 50, message = "Task group name must be between 1-50 characters")
    private String taskGroupName;
}
