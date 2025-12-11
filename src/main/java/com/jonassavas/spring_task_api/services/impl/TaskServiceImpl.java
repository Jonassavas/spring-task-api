package com.jonassavas.spring_task_api.services.impl;

import org.springframework.stereotype.Service;

import com.jonassavas.spring_task_api.domain.entities.TaskEntity;
import com.jonassavas.spring_task_api.domain.entities.TaskGroupEntity;
import com.jonassavas.spring_task_api.repositories.TaskGroupRepository;
import com.jonassavas.spring_task_api.repositories.TaskRepository;
import com.jonassavas.spring_task_api.services.TaskService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TaskServiceImpl implements TaskService{
    private final TaskRepository taskRepository;
    private final TaskGroupRepository taskGroupRepository;

    public TaskServiceImpl(TaskRepository taskRepository, TaskGroupRepository taskGroupRepository){
        this.taskRepository = taskRepository;
        this.taskGroupRepository = taskGroupRepository;
    }

    @Override
    public TaskEntity createTask(Long groupId, TaskEntity taskEntity) {
        TaskGroupEntity taskGroup = taskGroupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("TaskGroup not found with id " + groupId));

        taskGroup.addTask(taskEntity);

        return taskRepository.save(taskEntity);
    }

}
