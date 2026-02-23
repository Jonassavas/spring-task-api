package com.jonassavas.spring_task_api.services.impl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

import com.jonassavas.spring_task_api.domain.dto.task_group.TaskGroupDto;
import com.jonassavas.spring_task_api.domain.dto.task_group.TaskGroupRequestDto;
import com.jonassavas.spring_task_api.domain.dto.task_group.TaskGroupWithTasksDto;
import com.jonassavas.spring_task_api.domain.entities.TaskBoardEntity;
import com.jonassavas.spring_task_api.domain.entities.TaskGroupEntity;
import com.jonassavas.spring_task_api.mappers.Mapper;
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
    private Mapper<TaskGroupEntity, TaskGroupRequestDto> taskGroupRequestMapper;
    private Mapper<TaskGroupEntity, TaskGroupWithTasksDto> taskGroupWithTasksMapper;
    private Mapper<TaskGroupEntity, TaskGroupDto> taskGroupMapper;

    public TaskGroupServiceImpl(TaskGroupRepository taskGroupRepository,
                                TaskBoardRepository taskBoardRepository,
                                Mapper<TaskGroupEntity, TaskGroupRequestDto> taskGroupRequestMapper,
                                Mapper<TaskGroupEntity, TaskGroupWithTasksDto> taskGroupWithTasksMapper,
                                Mapper<TaskGroupEntity, TaskGroupDto> taskGroupMapper){
        this.taskGroupRepository = taskGroupRepository;
        this.taskBoardRepository = taskBoardRepository;
        this.taskGroupRequestMapper = taskGroupRequestMapper;
        this.taskGroupWithTasksMapper = taskGroupWithTasksMapper;
        this.taskGroupMapper = taskGroupMapper;
    }

    @Override
    public TaskGroupDto save(TaskGroupRequestDto taskGroupRequestDto){
        TaskGroupEntity taskGroupEntity = taskGroupRequestMapper.mapFrom(taskGroupRequestDto);
        TaskGroupEntity savedTaskGroup =  taskGroupRepository.save(taskGroupEntity);
        return taskGroupMapper.mapTo(savedTaskGroup);
    }

    @Override
    public TaskGroupDto createTaskGroup(Long boardId, TaskGroupRequestDto taskGroupRequestDto){
        TaskBoardEntity taskBoard = taskBoardRepository.findById(boardId)
            .orElseThrow(() -> new EntityNotFoundException("Taskboard not found with id: " + boardId));

        TaskGroupEntity taskGroupEntity = taskGroupRequestMapper.mapFrom(taskGroupRequestDto);
        
        taskGroupEntity.setTaskBoard(taskBoard);
        taskBoard.addTaskGroup(taskGroupEntity);

        TaskGroupEntity savedTaskGroup = taskGroupRepository.save(taskGroupEntity);

        return taskGroupMapper.mapTo(savedTaskGroup);
    }

    @Override
    public List<TaskGroupDto> findAll(){
        return StreamSupport.stream(taskGroupRepository
                                    .findAll()
                                    .spliterator(), false)
                                    .map(taskGroupMapper::mapTo)
                                    .collect(Collectors.toList());
    }

    @Override
    public List<TaskGroupWithTasksDto> findAllWithTasks(){
        return StreamSupport.stream(taskGroupRepository
                                    .findAllWithTasks()
                                    .spliterator(), false)
                                    .map(taskGroupWithTasksMapper::mapTo)
                                    .collect(Collectors.toList());
    }

    @Override
    public TaskGroupWithTasksDto findByIdWithTasks(Long id){
        TaskGroupEntity taskGroup = taskGroupRepository.findByIdWithTasks(id)
            .orElseThrow(() -> new EntityNotFoundException(
                "TaskGroup not found with id " + id));
        return taskGroupWithTasksMapper.mapTo(taskGroup);
        
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
    public TaskGroupDto update(Long id, TaskGroupRequestDto dto){
        TaskGroupEntity taskGroup = taskGroupRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(
                                        "TaskGroup not found with id: " + id));
    
        if(dto.getTaskGroupName() != null){
            taskGroup.setTaskGroupName(dto.getTaskGroupName());
        }

        return taskGroupMapper.mapTo(taskGroup);
    }
}
