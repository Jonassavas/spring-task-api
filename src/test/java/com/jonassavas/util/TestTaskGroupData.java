package com.jonassavas.util;

import com.jonassavas.spring_task_api.domain.dto.task_group.TaskGroupRequestDto;
import com.jonassavas.spring_task_api.domain.entities.TaskBoardEntity;
import com.jonassavas.spring_task_api.domain.entities.TaskGroupEntity;

public class TestTaskGroupData {

    // Entities ----------------------------------------------------------
    
    public static TaskGroupEntity createTaskGroupEntityA(TaskBoardEntity taskBoard){
        return TaskGroupEntity.builder()
                                .taskBoard(taskBoard)
                                .taskGroupName("Task Group A")
                                .build();
    }

    public static TaskGroupEntity createTaskGroupEntityB(TaskBoardEntity taskBoard){
        return TaskGroupEntity.builder()
                                .taskBoard(taskBoard)
                                .taskGroupName("Task Group B")
                                .build();
    }

    public static TaskGroupEntity createTaskGroupEntityC(TaskBoardEntity taskBoard){
        return TaskGroupEntity.builder()
                                .taskBoard(taskBoard)
                                .taskGroupName("Task Group C")
                                .build();
    }

    
    // DTOs -------------------------------------------------------------
    public static TaskGroupRequestDto createTaskGroupRequestDtoA(TaskBoardEntity taskBoard){
        return TaskGroupRequestDto.builder()
                                .taskBoardId(taskBoard.getId())
                                .taskGroupName("Task Group A")
                                .build();
    }

    public static TaskGroupRequestDto createTaskGroupDtoA(TaskBoardEntity taskBoard){
        return TaskGroupRequestDto.builder()
                                .taskBoardId(taskBoard.getId())
                                .taskGroupName("Task Group A")
                                .build();
    }

    public static TaskGroupRequestDto createTaskGroupDtoB(TaskBoardEntity taskBoard){
        return TaskGroupRequestDto.builder()
                                .taskBoardId(taskBoard.getId())
                                .taskGroupName("Task Group B")
                                .build();
    }

    public static TaskGroupRequestDto createTaskGroupDtoC(TaskBoardEntity taskBoard){
        return TaskGroupRequestDto.builder()
                                .taskBoardId(taskBoard.getId())
                                .taskGroupName("Task Group C")
                                .build();
    }
}
