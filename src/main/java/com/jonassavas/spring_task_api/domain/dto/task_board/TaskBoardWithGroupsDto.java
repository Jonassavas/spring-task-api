package com.jonassavas.spring_task_api.domain.dto.task_board;

import java.util.List;

import com.jonassavas.spring_task_api.domain.dto.task_group.TaskGroupWithTasksDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskBoardWithGroupsDto {
    private Long id;
    private String taskBoardName;
    private List<TaskGroupWithTasksDto> taskGroups;
}
