package com.jonassavas.spring_task_api.services;

import com.jonassavas.spring_task_api.domain.entities.TaskEntity;

public interface TaskService {
    TaskEntity createTask(Long groupId, TaskEntity taskEntity);
}
