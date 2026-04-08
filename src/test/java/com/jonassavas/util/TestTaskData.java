package com.jonassavas.util;

import com.jonassavas.spring_task_api.domain.dto.task.CreateTaskRequestDto;
import com.jonassavas.spring_task_api.domain.dto.task.UpdateTaskRequestDto;
import com.jonassavas.spring_task_api.domain.entities.TaskEntity;
import com.jonassavas.spring_task_api.domain.entities.TaskGroupEntity;

public class TestTaskData {
    // Entities ----------------------------------------------------------
    public static TaskEntity createTestTaskEntityA(TaskGroupEntity taskGroup) {
        return TaskEntity.builder().taskName("HomeworkA").taskGroup(taskGroup).build();
    }

    public static TaskEntity createTestTaskEntityB(TaskGroupEntity taskGroup) {
        return TaskEntity.builder().taskName("HomeworkB").taskGroup(taskGroup).build();
    }

    public static TaskEntity createTestTaskEntityC(TaskGroupEntity taskGroup) {
        return TaskEntity.builder().taskName("HomeworkC").taskGroup(taskGroup).build();
    }

    // CREATE DTOs -------------------------------------------------------------
    public static CreateTaskRequestDto createTestTaskRequestDtoA(TaskGroupEntity taskGroup) {
        return CreateTaskRequestDto.builder().taskName("Task A").build();
    }

    public static CreateTaskRequestDto createTestTaskRequestDtoB(TaskGroupEntity taskGroup) {
        return CreateTaskRequestDto.builder().taskName("Task B").build();
    }

    public static CreateTaskRequestDto createTestTaskRequestDtoC(TaskGroupEntity taskGroup) {
        return CreateTaskRequestDto.builder().taskName("Task C").build();
    }

    public static CreateTaskRequestDto createTestRequestTaskDto(TaskGroupEntity taskGroup) {
        return CreateTaskRequestDto.builder().taskName("Create Task Dto").build();
    }

    // UPDATE DTOs -------------------------------------------------------------
    public static UpdateTaskRequestDto updateTestTaskRequestDtoA(TaskGroupEntity taskGroup) {
        return UpdateTaskRequestDto.builder()
                .taskGroupId(taskGroup.getId())
                .taskName("Task A")
                .build();
    }

    public static UpdateTaskRequestDto updateTestTaskRequestDtoB(TaskGroupEntity taskGroup) {
        return UpdateTaskRequestDto.builder()
                .taskGroupId(taskGroup.getId())
                .taskName("Task B")
                .build();
    }

    public static UpdateTaskRequestDto updateTestTaskRequestDtoC(TaskGroupEntity taskGroup) {
        return UpdateTaskRequestDto.builder()
                .taskGroupId(taskGroup.getId())
                .taskName("Task C")
                .build();
    }

    public static UpdateTaskRequestDto updateTestRequestTaskDto(TaskGroupEntity taskGroup) {
        return UpdateTaskRequestDto.builder()
                .taskName("Create Task Dto")
                .taskGroupId(taskGroup.getId())
                .build();
    }
}
