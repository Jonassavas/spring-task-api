package com.jonassavas.spring_task_api.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTaskGroupDto {
    private Long id;

    private String taskGroupName;

    //private List<TaskEntity> tasks; //We don't allow tasks on create
}
