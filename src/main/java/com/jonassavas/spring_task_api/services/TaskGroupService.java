package com.jonassavas.spring_task_api.services;

import java.util.List;

import com.jonassavas.spring_task_api.domain.dto.task_group.TaskGroupDto;
import com.jonassavas.spring_task_api.domain.dto.task_group.TaskGroupRequestDto;
import com.jonassavas.spring_task_api.domain.dto.task_group.TaskGroupWithTasksDto;

public interface TaskGroupService {
    TaskGroupDto createTaskGroup(Long boardId, TaskGroupRequestDto taskGroupRequestDto);

    List<TaskGroupDto> listGroupsOnBoard(Long boardId);

    TaskGroupWithTasksDto findByIdWithTasks(Long id);

    //List<TaskGroupWithTasksDto> findAllWithTasks();

    boolean isExist(Long id);

    void delete(Long id);

    void deleteAllTasks(Long id);

    TaskGroupDto update(Long id, TaskGroupRequestDto taskGroupRequestDto);
}
