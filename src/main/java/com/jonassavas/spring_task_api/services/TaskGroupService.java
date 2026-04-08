package com.jonassavas.spring_task_api.services;

import com.jonassavas.spring_task_api.domain.dto.task_group.CreateTaskGroupRequestDto;
import com.jonassavas.spring_task_api.domain.dto.task_group.TaskGroupDto;
import com.jonassavas.spring_task_api.domain.dto.task_group.TaskGroupWithTasksDto;
import com.jonassavas.spring_task_api.domain.dto.task_group.UpdateTaskGroupRequestDto;
import java.util.List;

public interface TaskGroupService {
    TaskGroupDto createTaskGroup(Long boardId, CreateTaskGroupRequestDto taskGroupRequestDto);

    List<TaskGroupDto> listGroupsOnBoard(Long boardId);

    TaskGroupWithTasksDto findByIdWithTasks(Long id);

    // List<TaskGroupWithTasksDto> findAllWithTasks();

    boolean isExist(Long id);

    void delete(Long id);

    void deleteAllTasks(Long id);

    TaskGroupDto update(Long id, UpdateTaskGroupRequestDto taskGroupRequestDto);
}
