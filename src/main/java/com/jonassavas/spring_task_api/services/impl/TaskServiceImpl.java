package com.jonassavas.spring_task_api.services.impl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

import com.jonassavas.spring_task_api.domain.dto.task.TaskDto;
import com.jonassavas.spring_task_api.domain.dto.task.TaskRequestDto;
import com.jonassavas.spring_task_api.domain.entities.TaskEntity;
import com.jonassavas.spring_task_api.domain.entities.TaskGroupEntity;
import com.jonassavas.spring_task_api.mappers.Mapper;
import com.jonassavas.spring_task_api.repositories.TaskGroupRepository;
import com.jonassavas.spring_task_api.repositories.TaskRepository;
import com.jonassavas.spring_task_api.services.TaskService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class TaskServiceImpl implements TaskService{
    private final TaskRepository taskRepository;
    private final TaskGroupRepository taskGroupRepository;
    private Mapper<TaskEntity, TaskRequestDto> taskRequestMapper;
    private Mapper<TaskEntity, TaskDto> taskMapper;

    public TaskServiceImpl(TaskRepository taskRepository, 
                            TaskGroupRepository taskGroupRepository,
                            Mapper<TaskEntity, TaskRequestDto> taskRequestMapper,
                            Mapper<TaskEntity, TaskDto> taskMapper){
        this.taskRepository = taskRepository;
        this.taskGroupRepository = taskGroupRepository;
        this.taskRequestMapper = taskRequestMapper;
        this.taskMapper = taskMapper;
    }

    @Override
    public TaskDto createTask(Long groupId, TaskRequestDto taskRequestDto) {
        TaskGroupEntity taskGroup = taskGroupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("TaskGroup not found with id " + groupId));

        TaskEntity taskEntity = taskRequestMapper.mapFrom(taskRequestDto);

        taskEntity.setTaskGroup(taskGroup);

        taskGroup.addTask(taskEntity); // Cascade saves task automatically

        TaskEntity savedTaskEntity = taskRepository.save(taskEntity);

        return taskMapper.mapTo(savedTaskEntity);
    }


    @Override
    public void delete(Long id){
        TaskEntity task = taskRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Task not found with id " + id));

        TaskGroupEntity group = task.getTaskGroup();

        group.removeTask(task); // Triggers orphanRemoval

        taskRepository.delete(task);
    }

    @Override
    public boolean isExist(Long id){
        return taskRepository.existsById(id);
    }

    @Override
    public List<TaskDto> findAll(){
        return StreamSupport.stream(taskRepository
                                    .findAll()
                                    .spliterator(), false)
                                    .map(taskMapper::mapTo)
                                    .collect(Collectors.toList());
    }


    @Override
    public TaskDto update(Long id, TaskRequestDto taskRequestDto){
        TaskEntity task = taskRepository.findById(id)
                            .orElseThrow(() -> new EntityNotFoundException(
                                "Task not found with id " + id));
        
        // Update task name
        if(taskRequestDto.getTaskName() != null){
            task.setTaskName(taskRequestDto.getTaskName());
        }

        // Move task to another group (if requested)
        if(taskRequestDto.getTaskGroupId() != null &&
            !taskRequestDto.getTaskGroupId().equals(task.getTaskGroup().getId())){
                TaskGroupEntity oldGroup = task.getTaskGroup();

                TaskGroupEntity newGroup = taskGroupRepository
                                            .findById(taskRequestDto.getTaskGroupId())
                                            .orElseThrow(() -> new EntityNotFoundException(
                                                "TaskGroup not found with id " + taskRequestDto.getTaskGroupId()));
                
                oldGroup.removeTask(task);
                newGroup.addTask(task);
            }
        
        return taskMapper.mapTo(task);
    }

}
