package com.jonassavas.util;

import com.jonassavas.spring_task_api.domain.dto.task_board.TaskBoardRequestDto;
import com.jonassavas.spring_task_api.domain.entities.TaskBoardEntity;

public class TestTaskBoardData {

    // Entities ----------------------------------------------------------

    public static TaskBoardEntity createTestTaskBoardEntityA(){
        // This will require a user at some point
        return TaskBoardEntity.builder()
                                .name("Task Board A")
                                .build();
    }
    
    public static TaskBoardEntity createTestTaskBoardEntityB(){
        // This will require a user at some point
        return TaskBoardEntity.builder()
                                .name("Task Board B")
                                .build();
    }

    public static TaskBoardEntity createTestTaskBoardEntityC(){
        // This will require a user at some point
        return TaskBoardEntity.builder()
                                .name("Task Board C")
                                .build();
    }

    // DTOs -------------------------------------------------------------
    public static TaskBoardRequestDto createTestTaskBoardRequestDtoA(){
        // This will require a user at some point
        return TaskBoardRequestDto.builder()
                                .name("Task Board A")
                                .build();
    }
    
    public static TaskBoardRequestDto createTestTaskBoardRequestDtoB(){
        // This will require a user at some point
        return TaskBoardRequestDto.builder()
                                .name("Task Board B")
                                .build();
    }

    public static TaskBoardRequestDto createTestTaskBoardRequestDtoC(){
        // This will require a user at some point
        return TaskBoardRequestDto.builder()
                                .name("Task Board C")
                                .build();
    }
}
