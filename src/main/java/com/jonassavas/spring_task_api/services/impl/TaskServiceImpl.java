package com.jonassavas.spring_task_api.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jonassavas.spring_task_api.domain.dto.task.CreateTaskRequestDto;
import com.jonassavas.spring_task_api.domain.dto.task.TaskDto;
import com.jonassavas.spring_task_api.domain.dto.task.UpdateTaskRequestDto;
import com.jonassavas.spring_task_api.domain.entities.TaskEntity;
import com.jonassavas.spring_task_api.domain.entities.TaskGroupEntity;
import com.jonassavas.spring_task_api.mappers.Mapper;
import com.jonassavas.spring_task_api.repositories.TaskGroupRepository;
import com.jonassavas.spring_task_api.repositories.TaskRepository;
import com.jonassavas.spring_task_api.security.SecurityService;
import com.jonassavas.spring_task_api.services.TaskService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class TaskServiceImpl implements TaskService{
    private final TaskRepository taskRepository;
    private final TaskGroupRepository taskGroupRepository;
    private final Mapper<TaskEntity, TaskDto> taskMapper;
    private final SecurityService securityService;

    public TaskServiceImpl(TaskRepository taskRepository, 
                            TaskGroupRepository taskGroupRepository,
                            Mapper<TaskEntity, TaskDto> taskMapper,
                            SecurityService securityService){
        this.taskRepository = taskRepository;
        this.taskGroupRepository = taskGroupRepository;
        this.taskMapper = taskMapper;
        this.securityService = securityService;
    }

    @Override
    public TaskDto createTask(Long groupId, CreateTaskRequestDto taskRequestDto) {

        String username = securityService.getCurrentUsername();

        TaskGroupEntity taskGroup = taskGroupRepository
                .findByIdAndTaskBoardOwnerUsername(groupId, username)
                .orElseThrow(() -> new EntityNotFoundException(
                        "TaskGroup not found or not owned by user"));

        TaskEntity taskEntity = TaskEntity.builder()
        .taskName(taskRequestDto.getTaskName())
        .build();

        taskGroup.addTask(taskEntity);

        TaskEntity saved = taskRepository.save(taskEntity);

        return taskMapper.mapTo(saved);
    }


    @Override
    public void delete(Long id) {

        String username = securityService.getCurrentUsername();

        TaskEntity task = taskRepository
                .findByIdAndTaskGroupTaskBoardOwnerUsername(id, username)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Task not found or not owned by user"));

        TaskGroupEntity group = task.getTaskGroup();

        group.removeTask(task);
    } 

    @Override
    public boolean isExist(Long id){
        return taskRepository.existsById(id);
    }

    @Override
    public List<TaskDto> findByGroup(Long groupId) {

        String username = securityService.getCurrentUsername();

        return taskRepository
                .findByTaskGroupIdAndTaskGroupTaskBoardOwnerUsername(groupId, username)
                .stream()
                .map(taskMapper::mapTo)
                .collect(Collectors.toList());
    } 


    @Override
    public TaskDto update(Long id, UpdateTaskRequestDto taskRequestDto) {

        String username = securityService.getCurrentUsername();

        TaskEntity task = taskRepository
                .findByIdAndTaskGroupTaskBoardOwnerUsername(id, username)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Task not found or not owned by user"));

        // Update task name
        if (taskRequestDto.getTaskName() != null) {
            task.setTaskName(taskRequestDto.getTaskName());
        }

        // Move task to another group
        if (taskRequestDto.getTaskGroupId() != null &&
                !taskRequestDto.getTaskGroupId().equals(task.getTaskGroup().getId())) {

            TaskGroupEntity oldGroup = task.getTaskGroup();

            TaskGroupEntity newGroup = taskGroupRepository
                    .findByIdAndTaskBoardOwnerUsername(taskRequestDto.getTaskGroupId(), username)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Target TaskGroup not found or not owned by user"));

            oldGroup.removeTask(task);
            newGroup.addTask(task);
        }

        return taskMapper.mapTo(task);
    } 

}
