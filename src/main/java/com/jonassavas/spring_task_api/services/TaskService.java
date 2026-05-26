package com.jonassavas.spring_task_api.services;

import com.jonassavas.spring_task_api.domain.dto.task.CreateTaskRequestDto;
import com.jonassavas.spring_task_api.domain.dto.task.ReorderTasksRequestDto;
import com.jonassavas.spring_task_api.domain.dto.task.TaskDto;
import com.jonassavas.spring_task_api.domain.dto.task.UpdateTaskRequestDto;
import java.util.List;

public interface TaskService {
    TaskDto createTask(Long groupId, CreateTaskRequestDto taskEntity);

    void delete(Long id);

    boolean isExist(Long id);

    TaskDto update(Long id, UpdateTaskRequestDto dto);

    List<TaskDto> findByGroup(Long groupId);

    void reorderTasks(ReorderTasksRequestDto dto);
}
