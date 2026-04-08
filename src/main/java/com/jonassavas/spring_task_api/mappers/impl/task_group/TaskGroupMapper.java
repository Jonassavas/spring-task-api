package com.jonassavas.spring_task_api.mappers.impl.task_group;

import com.jonassavas.spring_task_api.domain.dto.task_group.TaskGroupDto;
import com.jonassavas.spring_task_api.domain.entities.TaskGroupEntity;
import com.jonassavas.spring_task_api.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class TaskGroupMapper implements Mapper<TaskGroupEntity, TaskGroupDto> {
    private ModelMapper modelMapper;

    public TaskGroupMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

        // Skip taskGroup when mapping DTO -> Entity
        this.modelMapper
                .typeMap(TaskGroupDto.class, TaskGroupEntity.class)
                .addMappings(mapper -> mapper.skip(TaskGroupEntity::setTaskBoard));
    }

    @Override
    public TaskGroupDto mapTo(TaskGroupEntity taskGroupEntity) {
        TaskGroupDto dto = modelMapper.map(taskGroupEntity, TaskGroupDto.class);
        dto.setTaskBoardId(taskGroupEntity.getTaskBoard().getId());
        return dto;
    }

    @Override
    public TaskGroupEntity mapFrom(TaskGroupDto taskGroupDto) {
        return modelMapper.map(taskGroupDto, TaskGroupEntity.class);
    }
}
