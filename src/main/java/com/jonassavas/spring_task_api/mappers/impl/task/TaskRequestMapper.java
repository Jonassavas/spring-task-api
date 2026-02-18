package com.jonassavas.spring_task_api.mappers.impl.task;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.jonassavas.spring_task_api.domain.dto.task.TaskDto;
import com.jonassavas.spring_task_api.domain.dto.task.TaskRequestDto;
import com.jonassavas.spring_task_api.domain.entities.TaskEntity;
import com.jonassavas.spring_task_api.mappers.Mapper;

@Component
public class TaskRequestMapper implements Mapper<TaskEntity, TaskRequestDto> {

    private ModelMapper modelMapper;

    public TaskRequestMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

        // Skip taskGroup when mapping DTO -> Entity
        this.modelMapper.typeMap(TaskRequestDto.class, TaskEntity.class)
                .addMappings(mapper -> mapper.skip(TaskEntity::setTaskGroup));
    }

    @Override
    public TaskRequestDto mapTo(TaskEntity taskEntity) {
        TaskRequestDto dto = modelMapper.map(taskEntity, TaskRequestDto.class);
        dto.setTaskGroupId(taskEntity.getTaskGroup().getId());
        return dto;
    }

    @Override
    public TaskEntity mapFrom(TaskRequestDto createTaskDto) {
        return modelMapper.map(createTaskDto, TaskEntity.class);
    }
}

