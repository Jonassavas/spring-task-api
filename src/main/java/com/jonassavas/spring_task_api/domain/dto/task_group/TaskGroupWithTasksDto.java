package com.jonassavas.spring_task_api.domain.dto.task_group;

import java.util.List;

import com.jonassavas.spring_task_api.domain.dto.task.TaskDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskGroupWithTasksDto {
    private Long id;

    private String taskGroupName;

    private List<TaskDto> tasks;

    private Long taskBoardId;
}