package com.jonassavas.spring_task_api.domain.dto;

import java.util.List;

import com.jonassavas.spring_task_api.domain.entities.TaskEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskGroupDto {
    private Long id;

    private String groupName;

    private List<TaskEntity> tasks;
}
