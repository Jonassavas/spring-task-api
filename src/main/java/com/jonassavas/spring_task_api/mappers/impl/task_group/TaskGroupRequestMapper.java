package com.jonassavas.spring_task_api.mappers.impl.task_group;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.jonassavas.spring_task_api.domain.dto.task_group.TaskGroupRequestDto;
import com.jonassavas.spring_task_api.domain.entities.TaskGroupEntity;
import com.jonassavas.spring_task_api.mappers.Mapper;

@Component
public class TaskGroupRequestMapper implements Mapper<TaskGroupEntity, TaskGroupRequestDto>{
    private ModelMapper modelMapper;

    public TaskGroupRequestMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;

        // Skip taskBoard when mapping DTO -> Entity
        this.modelMapper.typeMap(TaskGroupRequestDto.class, TaskGroupEntity.class)
                .addMappings(mapper -> mapper.skip(TaskGroupEntity::setTaskBoard));
    }
    
    @Override
    public TaskGroupRequestDto mapTo(TaskGroupEntity taskGroupEntity){
        TaskGroupRequestDto dto = modelMapper.map(taskGroupEntity, TaskGroupRequestDto.class);
        dto.setTaskBoardId(taskGroupEntity.getTaskBoard().getId());
        return dto;
    }

    @Override
    public TaskGroupEntity mapFrom(TaskGroupRequestDto taskGroupDto){
        return modelMapper.map(taskGroupDto, TaskGroupEntity.class);
    }
}
