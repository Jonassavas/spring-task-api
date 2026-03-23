package com.jonassavas.spring_task_api.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonassavas.spring_task_api.domain.dto.task_group.CreateTaskGroupRequestDto;
import com.jonassavas.spring_task_api.domain.dto.task_group.TaskGroupDto;
import com.jonassavas.spring_task_api.domain.dto.task_group.TaskGroupWithTasksDto;
import com.jonassavas.spring_task_api.domain.dto.task_group.UpdateTaskGroupRequestDto;
import com.jonassavas.spring_task_api.services.TaskGroupService;

@RestController
@RequestMapping("/taskboards/{boardId}/groups")
public class TaskGroupController {

    private final TaskGroupService taskGroupService;

    public TaskGroupController(TaskGroupService taskGroupService){
        this.taskGroupService = taskGroupService;
    }

    @PostMapping
    public ResponseEntity<TaskGroupDto> createTaskGroup(
            @PathVariable Long boardId,
            @RequestBody CreateTaskGroupRequestDto requestDto) {

        TaskGroupDto responseDto =
                taskGroupService.createTaskGroup(boardId, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<TaskGroupDto>> listTaskGroups(
            @PathVariable Long boardId) {

        List<TaskGroupDto> groups =
                taskGroupService.listGroupsOnBoard(boardId);

        return ResponseEntity.ok(groups);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<TaskGroupWithTasksDto> getGroupWithTasks(
            @PathVariable Long groupId) {

        TaskGroupWithTasksDto dto =
                taskGroupService.findByIdWithTasks(groupId);

        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{groupId}")
    public ResponseEntity<TaskGroupDto> updateTaskGroup(
            @PathVariable Long groupId,
            @RequestBody UpdateTaskGroupRequestDto requestDto) {

        TaskGroupDto responseDto =
                taskGroupService.update(groupId, requestDto);

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteTaskGroup(
            @PathVariable Long groupId){

        taskGroupService.delete(groupId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{groupId}/tasks")
    public ResponseEntity<Void> deleteAllTasks(
            @PathVariable Long groupId){

        taskGroupService.deleteAllTasks(groupId);

        return ResponseEntity.noContent().build();
    }
}