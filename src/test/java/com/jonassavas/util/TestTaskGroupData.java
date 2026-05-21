package com.jonassavas.util;

import com.jonassavas.spring_task_api.domain.dto.task_group.CreateTaskGroupRequestDto;
import com.jonassavas.spring_task_api.domain.dto.task_group.UpdateTaskGroupRequestDto;
import com.jonassavas.spring_task_api.domain.entities.TaskBoardEntity;
import com.jonassavas.spring_task_api.domain.entities.TaskGroupEntity;

public class TestTaskGroupData {

    // Entities ----------------------------------------------------------

    public static TaskGroupEntity createTaskGroupEntityA(TaskBoardEntity taskBoard) {
        return TaskGroupEntity.builder()
                .taskBoard(taskBoard)
                .taskGroupName("Task Group A")
                .position(0)
                .color("#FF0000")
                .build();
    }

    public static TaskGroupEntity createTaskGroupEntityB(TaskBoardEntity taskBoard) {
        return TaskGroupEntity.builder()
                .taskBoard(taskBoard)
                .taskGroupName("Task Group B")
                .position(1)
                .color("#00FF00")
                .build();
    }

    public static TaskGroupEntity createTaskGroupEntityC(TaskBoardEntity taskBoard) {
        return TaskGroupEntity.builder()
                .taskBoard(taskBoard)
                .taskGroupName("Task Group C")
                .position(2)
                .color("#0000FF")
                .build();
    }

    // CREATE DTOs -------------------------------------------------------------
    public static CreateTaskGroupRequestDto createTaskGroupRequestDtoA(TaskBoardEntity taskBoard) {
        return CreateTaskGroupRequestDto.builder().taskGroupName("Task Group A").color("#FF0000").build();
    }

    public static CreateTaskGroupRequestDto createTaskGroupDtoA(TaskBoardEntity taskBoard) {
        return CreateTaskGroupRequestDto.builder().taskGroupName("Task Group A").color("#FF0000").build();
    }

    public static CreateTaskGroupRequestDto createTaskGroupDtoB(TaskBoardEntity taskBoard) {
        return CreateTaskGroupRequestDto.builder().taskGroupName("Task Group B").color("#FF0000").build();
    }

    public static CreateTaskGroupRequestDto createTaskGroupDtoC(TaskBoardEntity taskBoard) {
        return CreateTaskGroupRequestDto.builder().taskGroupName("Task Group C").color("#FF0000").build();
    }

    // UPDATE DTOs -------------------------------------------------------------
    public static UpdateTaskGroupRequestDto updateTaskGroupRequestDtoA(TaskBoardEntity taskBoard) {
        return UpdateTaskGroupRequestDto.builder().taskGroupName("Task Group A").build();
    }

    public static UpdateTaskGroupRequestDto updateTaskGroupDtoA(TaskBoardEntity taskBoard) {
        return UpdateTaskGroupRequestDto.builder().taskGroupName("Task Group A").build();
    }

    public static UpdateTaskGroupRequestDto updateTaskGroupDtoB(TaskBoardEntity taskBoard) {
        return UpdateTaskGroupRequestDto.builder().taskGroupName("Task Group B").build();
    }

    public static UpdateTaskGroupRequestDto updateTaskGroupDtoC(TaskBoardEntity taskBoard) {
        return UpdateTaskGroupRequestDto.builder().taskGroupName("Task Group C").build();
    }
}
