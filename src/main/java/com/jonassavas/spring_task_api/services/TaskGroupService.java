package com.jonassavas.spring_task_api.services;

import java.util.List;

import com.jonassavas.spring_task_api.domain.dto.task_group.TaskGroupDto;
import com.jonassavas.spring_task_api.domain.dto.task_group.TaskGroupRequestDto;

public interface TaskGroupService {
    TaskGroupDto save(TaskGroupRequestDto taskGroupRequestDto);

    TaskGroupDto createTaskGroup(Long boardId, TaskGroupRequestDto taskGroupRequestDto);

    List<TaskGroupDto> findAll();

    List<TaskGroupDto> findAllWithTasks();

    TaskGroupDto findByIdWithTasks(Long id);

    boolean isExist(Long id);

    void delete(Long id);

    void deleteAllTasks(Long id);

    TaskGroupDto update(Long id, TaskGroupRequestDto taskGroupRequestDto);
}
