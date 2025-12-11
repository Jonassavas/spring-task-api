package com.jonassavas.spring_task_api.mappers.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;


import com.jonassavas.spring_task_api.domain.dto.CreateTaskGroupDto;
import com.jonassavas.spring_task_api.domain.entities.TaskGroupEntity;
import com.jonassavas.spring_task_api.mappers.Mapper;

@Component
public class CreateTaskGroupMapper implements Mapper<TaskGroupEntity, CreateTaskGroupDto>{
    private ModelMapper modelMapper;

    public CreateTaskGroupMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }
    
    @Override
    public CreateTaskGroupDto mapTo(TaskGroupEntity taskGroupEntity){
        return modelMapper.map(taskGroupEntity, CreateTaskGroupDto.class);
    }

    @Override
    public TaskGroupEntity mapFrom(CreateTaskGroupDto taskGroupDto){
        return modelMapper.map(taskGroupDto, TaskGroupEntity.class);
    }
}
