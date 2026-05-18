package com.jonassavas.spring_task_api.domain.dto.task_group;

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

    private String taskGroupName;

    private String color;

    private Integer position;

    private Long taskBoardId;
}
