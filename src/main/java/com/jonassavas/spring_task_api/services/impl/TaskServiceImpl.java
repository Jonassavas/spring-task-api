package com.jonassavas.spring_task_api.services.impl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

import com.jonassavas.spring_task_api.domain.dto.task.TaskRequestDto;
import com.jonassavas.spring_task_api.domain.entities.TaskEntity;
import com.jonassavas.spring_task_api.domain.entities.TaskGroupEntity;
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

    public TaskServiceImpl(TaskRepository taskRepository, TaskGroupRepository taskGroupRepository){
        this.taskRepository = taskRepository;
        this.taskGroupRepository = taskGroupRepository;
    }

    @Override
    public TaskEntity createTask(Long groupId, TaskEntity taskEntity) {
        TaskGroupEntity taskGroup = taskGroupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("TaskGroup not found with id " + groupId));

        taskEntity.setTaskGroup(taskGroup);

        taskGroup.addTask(taskEntity); // Cascade saves task automatically

        return taskRepository.save(taskEntity);
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
    public List<TaskEntity> findAll(){
        return StreamSupport.stream(taskRepository
                                    .findAll()
                                    .spliterator(), false)
                                    .collect(Collectors.toList());
    }


    @Override
    public TaskEntity update(Long id, TaskRequestDto dto){
        TaskEntity task = taskRepository.findById(id)
                            .orElseThrow(() -> new EntityNotFoundException(
                                "Task not found with id " + id));
        
        // Update task name
        if(dto.getTaskName() != null){
            task.setTaskName(dto.getTaskName());
        }

        // Move task to another group (if requested)
        if(dto.getTaskGroupId() != null &&
            !dto.getTaskGroupId().equals(task.getTaskGroup().getId())){
                TaskGroupEntity oldGroup = task.getTaskGroup();

                TaskGroupEntity newGroup = taskGroupRepository
                                            .findById(dto.getTaskGroupId())
                                            .orElseThrow(() -> new EntityNotFoundException(
                                                "TaskGroup not found with id " + dto.getTaskGroupId()));
                
                oldGroup.removeTask(task);
                newGroup.addTask(task);
            }
        
        return task;
    }

}
