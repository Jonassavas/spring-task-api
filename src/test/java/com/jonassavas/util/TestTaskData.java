package com.jonassavas.util;

import com.jonassavas.spring_task_api.domain.dto.task.TaskDto;
import com.jonassavas.spring_task_api.domain.dto.task.TaskRequestDto;
import com.jonassavas.spring_task_api.domain.entities.TaskEntity;
import com.jonassavas.spring_task_api.domain.entities.TaskGroupEntity;

public class TestTaskData {
   // Entities ----------------------------------------------------------
    public static TaskEntity createTestTaskEntityA(TaskGroupEntity taskGroup){
        return TaskEntity.builder()
                            .taskName("HomeworkA")
                            .taskGroup(taskGroup)
                            .build();
    }

    public static TaskEntity createTestTaskEntityB(TaskGroupEntity taskGroup){
        return TaskEntity.builder()
                            .taskName("HomeworkB")
                            .taskGroup(taskGroup)
                            .build();
    }

    public static TaskEntity createTestTaskEntityC(TaskGroupEntity taskGroup){
        return TaskEntity.builder()
                            .taskName("HomeworkC")
                            .taskGroup(taskGroup)
                            .build();
    }



   // DTOs -------------------------------------------------------------
    public static TaskDto createTestTaskDtoA(TaskGroupEntity taskGroup){
        return TaskDto.builder()
                        .taskGroupId(taskGroup.getId())
                        .taskName("Task A")
                        .build();
    }

    public static TaskRequestDto createTestRequestTaskDto(TaskGroupEntity taskGroup){
        return TaskRequestDto.builder()
                            .taskName("Create Task Dto")
                            .taskGroupId(taskGroup.getId())
                            .build();
    }
}
