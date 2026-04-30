package com.jonassavas.spring_task_api.mappers.impl.task_board;

import com.jonassavas.spring_task_api.domain.dto.task_board.TaskBoardDto;
import com.jonassavas.spring_task_api.domain.dto.task_board.TaskBoardWithGroupsDto;
import com.jonassavas.spring_task_api.domain.entities.TaskBoardEntity;
import com.jonassavas.spring_task_api.mappers.Mapper;
import com.jonassavas.spring_task_api.mappers.impl.task_group.TaskGroupWithTasksMapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class TaskBoardMapper implements Mapper<TaskBoardEntity, TaskBoardDto> {

    private final ModelMapper modelMapper;
    private final TaskGroupWithTasksMapper taskGroupWithTasksMapper;

    public TaskBoardMapper(
            ModelMapper modelMapper, TaskGroupWithTasksMapper taskGroupWithTasksMapper) {
        this.modelMapper = modelMapper;
        this.taskGroupWithTasksMapper = taskGroupWithTasksMapper;
    }

    @Override
    public TaskBoardDto mapTo(TaskBoardEntity taskBoardEntity) {
        return modelMapper.map(taskBoardEntity, TaskBoardDto.class);
    }

    @Override
    public TaskBoardEntity mapFrom(TaskBoardDto taskBoardDto) {
        return modelMapper.map(taskBoardDto, TaskBoardEntity.class);
    }

    public TaskBoardWithGroupsDto mapToWithGroups(TaskBoardEntity entity) {
        return TaskBoardWithGroupsDto.builder()
                .id(entity.getId())
                .taskBoardName(entity.getTaskBoardName())
                .taskGroups(
                        entity.getTaskGroups().stream()
                                .map(taskGroupWithTasksMapper::mapTo)
                                .toList())
                .build();
    }
}
