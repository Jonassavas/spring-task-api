package com.jonassavas.spring_task_api.mappers.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;


import com.jonassavas.spring_task_api.domain.dto.TaskGroupDto;
import com.jonassavas.spring_task_api.domain.entities.TaskGroupEntity;
import com.jonassavas.spring_task_api.mappers.Mapper;

@Component
public class TaskGroupMapper implements Mapper<TaskGroupEntity, TaskGroupDto>{
    private ModelMapper modelMapper;

    public TaskGroupMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }
    
    @Override
    public TaskGroupDto mapTo(TaskGroupEntity taskGroupEntity){
        return modelMapper.map(taskGroupEntity, TaskGroupDto.class);
    }

    @Override
    public TaskGroupEntity mapFrom(TaskGroupDto taskGroupDto){
        return modelMapper.map(taskGroupDto, TaskGroupEntity.class);
    }
}
