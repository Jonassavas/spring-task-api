package com.jonassavas.util;

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
}
