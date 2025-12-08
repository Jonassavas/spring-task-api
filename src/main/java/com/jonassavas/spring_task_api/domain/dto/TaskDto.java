package com.jonassavas.spring_task_api.domain.dto;

import com.jonassavas.spring_task_api.domain.entities.TaskGroupEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskDto {
    private Long id;

    private String taskName;

    private TaskGroupEntity taskGroup;
}
