package com.jonassavas.spring_task_api.services;

import java.util.List;

import com.jonassavas.spring_task_api.domain.dto.task.TaskRequestDto;
import com.jonassavas.spring_task_api.domain.entities.TaskEntity;

public interface TaskService {
    TaskEntity createTask(Long groupId, TaskEntity taskEntity);

    void delete(Long id);

    boolean isExist(Long id);

    TaskEntity update(Long id, TaskRequestDto dto);

    List<TaskEntity> findAll();
}
