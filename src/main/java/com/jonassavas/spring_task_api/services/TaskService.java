package com.jonassavas.spring_task_api.services;

import java.util.List;

import com.jonassavas.spring_task_api.domain.dto.task.TaskDto;
import com.jonassavas.spring_task_api.domain.dto.task.TaskRequestDto;

public interface TaskService {
    TaskDto createTask(Long groupId, TaskRequestDto taskEntity);

    void delete(Long id);

    boolean isExist(Long id);

    TaskDto update(Long id, TaskRequestDto dto);

    List<TaskDto> findByGroup(Long groupId);
}
