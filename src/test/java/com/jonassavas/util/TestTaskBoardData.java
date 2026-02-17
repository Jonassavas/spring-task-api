package com.jonassavas.util;

import com.jonassavas.spring_task_api.domain.entities.TaskBoardEntity;

public class TestTaskBoardData {

    // Entities ----------------------------------------------------------

    public static TaskBoardEntity createTestTaskBoardEntityA(){
        // This will require a user at some point
        return TaskBoardEntity.builder()
                                .name(null)
                                .build();
    }


    // DTOs -------------------------------------------------------------
}
