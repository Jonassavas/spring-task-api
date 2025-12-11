package com.jonassavas.spring_task_api.mappers.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;


import com.jonassavas.spring_task_api.domain.dto.TaskDto;
import com.jonassavas.spring_task_api.domain.entities.TaskEntity;
import com.jonassavas.spring_task_api.mappers.Mapper;

@Component
public class TaskMapper implements Mapper<TaskEntity, TaskDto> {

    private final ModelMapper modelMapper;

    public TaskMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

        // Skip taskGroup when mapping DTO -> Entity
        this.modelMapper.typeMap(TaskDto.class, TaskEntity.class)
                .addMappings(mapper -> mapper.skip(TaskEntity::setTaskGroup));
    }

    @Override
    public TaskDto mapTo(TaskEntity taskEntity) {
        TaskDto dto = modelMapper.map(taskEntity, TaskDto.class);
        dto.setTaskGroupId(taskEntity.getTaskGroup().getId());
        return dto;
    }

    @Override
    public TaskEntity mapFrom(TaskDto taskDto) {
        return modelMapper.map(taskDto, TaskEntity.class);
    }
}

