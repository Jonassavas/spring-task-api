package com.jonassavas.spring_task_api.mappers.impl.task_board;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.jonassavas.spring_task_api.domain.dto.task_board.TaskBoardDto;
import com.jonassavas.spring_task_api.domain.entities.TaskBoardEntity;
import com.jonassavas.spring_task_api.mappers.Mapper;

@Component
public class TaskBoardMapper implements Mapper<TaskBoardEntity, TaskBoardDto>{
   private ModelMapper modelMapper;

    public TaskBoardMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;

        // THIS WILL PROBABLY BE USER LATER
        // Skip taskGroup when mapping DTO -> Entity
        // this.modelMapper.typeMap(TaskBoardDto.class, TaskBoardEntity.class)
        //         .addMappings(mapper -> mapper.skip(TaskBoardEntity::setTaskGroups));
    }
    
    @Override
    public TaskBoardDto mapTo(TaskBoardEntity taskBoardEntity){
        TaskBoardDto dto = modelMapper.map(taskBoardEntity, TaskBoardDto.class);
        //dto.setId(taskBoardEntity.getId());
        return dto;
    }

    @Override
    public TaskBoardEntity mapFrom(TaskBoardDto taskBoardDto){
        return modelMapper.map(taskBoardDto, TaskBoardEntity.class);
    } 
}
