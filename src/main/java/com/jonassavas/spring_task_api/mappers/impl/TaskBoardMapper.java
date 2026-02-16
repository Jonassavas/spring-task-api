package com.jonassavas.spring_task_api.mappers.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.jonassavas.spring_task_api.domain.dto.TaskBoardDto;
import com.jonassavas.spring_task_api.domain.entities.TaskBoardEntity;
import com.jonassavas.spring_task_api.mappers.Mapper;

@Component
public class TaskBoardMapper implements Mapper<TaskBoardEntity, TaskBoardDto>{
   private ModelMapper modelMapper;

    public TaskBoardMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;

        // THIS WILL PROBABLY BE USER LATER
        // // Skip taskGroup when mapping DTO -> Entity
        // this.modelMapper.typeMap(TaskBoardDto.class, TaskBoardEntity.class)
        //         .addMappings(mapper -> mapper.skip(TaskBoardEntity::setTaskBoard));
    }
    
    @Override
    public TaskBoardDto mapTo(TaskBoardEntity taskBoardEntity){
        TaskBoardDto dto = modelMapper.map(taskBoardEntity, TaskBoardDto.class);
        //dto.setTaskBoardId(taskBoardEntity.getTaskBoard().getId());
        return dto;
    }

    @Override
    public TaskBoardEntity mapFrom(TaskBoardDto taskBoardDto){
        return modelMapper.map(taskBoardDto, TaskBoardEntity.class);
    } 
}
