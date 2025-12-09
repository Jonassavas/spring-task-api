package com.jonassavas.spring_task_api;

import com.jonassavas.spring_task_api.domain.entities.TaskEntity;
import com.jonassavas.spring_task_api.domain.entities.TaskGroupEntity;

public class TestDataUtil {

    private TestDataUtil(){

    }

    public static TaskEntity createTestTaskEntityA(){
        return TaskEntity.builder()
                            .taskName("HomeworkA")
                            .taskGroup(null)
                            .build();
    }

    public static TaskEntity createTestTaskEntityB(){
        return TaskEntity.builder()
                            .taskName("HomeworkB")
                            .taskGroup(null)
                            .build();
    }

    public static TaskEntity createTestTaskEntityC(){
        return TaskEntity.builder()
                            .taskName("HomeworkC")
                            .taskGroup(null)
                            .build();
    }
    
    public static TaskGroupEntity createTaskGroupEntityA(){
        return TaskGroupEntity.builder()
                                .groupName("Task Group A")
                                .build();
    }

    public static TaskGroupEntity createTaskGroupEntityB(){
        return TaskGroupEntity.builder()
                                .groupName("Task Group B")
                                .build();
    }

    public static TaskGroupEntity createTaskGroupEntityC(){
        return TaskGroupEntity.builder()
                                .groupName("Task Group C")
                                .build();
    }
}
