package com.jonassavas.spring_task_api.mappers.impl.task_group;

import com.jonassavas.spring_task_api.domain.dto.task_group.TaskGroupWithTasksDto;
import com.jonassavas.spring_task_api.domain.entities.TaskGroupEntity;
import com.jonassavas.spring_task_api.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class TaskGroupWithTasksMapper implements Mapper<TaskGroupEntity, TaskGroupWithTasksDto> {
    private ModelMapper modelMapper;

    public TaskGroupWithTasksMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

        // Skip taskGroup when mapping DTO -> Entity
        this.modelMapper
                .typeMap(TaskGroupWithTasksDto.class, TaskGroupEntity.class)
                .addMappings(mapper -> mapper.skip(TaskGroupEntity::setTaskBoard));
    }

    @Override
    public TaskGroupWithTasksDto mapTo(TaskGroupEntity taskGroupEntity) {
        TaskGroupWithTasksDto dto = modelMapper.map(taskGroupEntity, TaskGroupWithTasksDto.class);
        dto.setTaskBoardId(taskGroupEntity.getTaskBoard().getId());
        return dto;
    }

    @Override
    public TaskGroupEntity mapFrom(TaskGroupWithTasksDto taskGroupDto) {
        return modelMapper.map(taskGroupDto, TaskGroupEntity.class);
    }
}
