package com.jonassavas.spring_task_api.services.impl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

import com.jonassavas.spring_task_api.domain.dto.task_group.TaskGroupRequestDto;
import com.jonassavas.spring_task_api.domain.entities.TaskBoardEntity;
import com.jonassavas.spring_task_api.domain.entities.TaskGroupEntity;
import com.jonassavas.spring_task_api.repositories.TaskBoardRepository;
import com.jonassavas.spring_task_api.repositories.TaskGroupRepository;
import com.jonassavas.spring_task_api.services.TaskGroupService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class TaskGroupServiceImpl implements TaskGroupService{
    
    private TaskGroupRepository taskGroupRepository;
    private TaskBoardRepository taskBoardRepository;

    public TaskGroupServiceImpl(TaskGroupRepository taskGroupRepository,
                                TaskBoardRepository taskBoardRepository){
        this.taskGroupRepository = taskGroupRepository;
        this.taskBoardRepository = taskBoardRepository;
    }

    @Override
    public TaskGroupEntity save(TaskGroupEntity taskGroupEntity){
        return taskGroupRepository.save(taskGroupEntity);
    }

    @Override
    public TaskGroupEntity createTaskGroup(Long boardId, TaskGroupEntity taskGroupEntity){
        TaskBoardEntity taskBoard = taskBoardRepository.findById(boardId)
            .orElseThrow(() -> new EntityNotFoundException("Taskboard not found with id: " + boardId));
        
        taskGroupEntity.setTaskBoard(taskBoard);
        taskBoard.addTaskGroup(taskGroupEntity);

        return taskGroupRepository.save(taskGroupEntity);
    }

    @Override
    public List<TaskGroupEntity> findAll(){
        return StreamSupport.stream(taskGroupRepository
                                    .findAll()
                                    .spliterator(), false)
                                    .collect(Collectors.toList());
    }

    @Override
    public List<TaskGroupEntity> findAllWithTasks(){
        return taskGroupRepository.findAllWithTasks();
    }

    @Override
    public TaskGroupEntity findByIdWithTasks(Long id){
        return taskGroupRepository.findByIdWithTasks(id)
            .orElseThrow(() -> new EntityNotFoundException(
                "TaskGroup not found with id " + id));
    }

    @Override
    public boolean isExist(Long id){
        return taskGroupRepository.existsById(id);
    }

    @Override
    public void delete(Long id){
        taskGroupRepository.deleteById(id);
    }
 
    @Override
    public void deleteAllTasks(Long id){
        TaskGroupEntity taskGroup = taskGroupRepository.findById(id)
                                    .orElseThrow(() -> new EntityNotFoundException(
                                        "TaskGroup not found with id " + id));

        taskGroup.getTasks().clear();
    }

    @Override
    public TaskGroupEntity update(Long id, TaskGroupRequestDto dto){
        TaskGroupEntity taskGroup = taskGroupRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(
                                        "TaskGroup not found with id: " + id));
    
        if(dto.getTaskGroupName() != null){
            taskGroup.setTaskGroupName(dto.getTaskGroupName());
        }

        return taskGroup;
    }
}
