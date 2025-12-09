package com.jonassavas.spring_task_api.services.impl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

import com.jonassavas.spring_task_api.domain.entities.TaskGroupEntity;
import com.jonassavas.spring_task_api.repositories.TaskGroupRepository;
import com.jonassavas.spring_task_api.services.TaskGroupService;

@Service
public class TaskGroupServiceImpl implements TaskGroupService{
    
    private TaskGroupRepository taskGroupRepository;

    public TaskGroupServiceImpl(TaskGroupRepository taskGroupRepository){
        this.taskGroupRepository = taskGroupRepository;
    }

    @Override
    public TaskGroupEntity save(TaskGroupEntity taskGroupEntity){
        return taskGroupRepository.save(taskGroupEntity);
    }

    @Override
    public List<TaskGroupEntity> findAll(){
        return StreamSupport.stream(taskGroupRepository
                                    .findAll()
                                    .spliterator(), false)
                                    .collect(Collectors.toList());
    }
}
