package com.jonassavas.spring_task_api.mappers.impl.task_board;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.jonassavas.spring_task_api.domain.dto.task_board.TaskBoardRequestDto;
import com.jonassavas.spring_task_api.domain.entities.TaskBoardEntity;
import com.jonassavas.spring_task_api.mappers.Mapper;

@Component
public class TaskBoardRequestMapper implements Mapper<TaskBoardEntity, TaskBoardRequestDto>{
   private ModelMapper modelMapper;

    public TaskBoardRequestMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;

        // THIS WILL PROBABLY BE USER LATER
        // // Skip taskGroup when mapping DTO -> Entity
        // this.modelMapper.typeMap(TaskBoardDto.class, TaskBoardEntity.class)
        //         .addMappings(mapper -> mapper.skip(TaskBoardEntity::setTaskBoard));
    }
    
    @Override
    public TaskBoardRequestDto mapTo(TaskBoardEntity taskBoardEntity){
        TaskBoardRequestDto dto = modelMapper.map(taskBoardEntity, TaskBoardRequestDto.class);
        //dto.setTaskBoardId(taskBoardEntity.getTaskBoard().getId());
        return dto;
    }

    @Override
    public TaskBoardEntity mapFrom(TaskBoardRequestDto taskBoardDto){
        return modelMapper.map(taskBoardDto, TaskBoardEntity.class);
    } 
}
