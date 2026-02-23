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
import org.springframework.web.bind.annotation.RestController;

import com.jonassavas.spring_task_api.domain.dto.task_group.TaskGroupDto;
import com.jonassavas.spring_task_api.domain.dto.task_group.TaskGroupRequestDto;
import com.jonassavas.spring_task_api.domain.dto.task_group.TaskGroupWithTasksDto;
import com.jonassavas.spring_task_api.services.TaskBoardService;
import com.jonassavas.spring_task_api.services.TaskGroupService;


@RestController
public class TaskGroupController {
    private TaskGroupService taskGroupService;


    private TaskBoardService taskBoardService;

    public TaskGroupController(TaskGroupService taskGroupService, 
                                TaskBoardService taskBoardService){
        this.taskGroupService = taskGroupService;
        this.taskBoardService = taskBoardService;
    }

    @PostMapping(path = "/boards/{boardId}/groups")
    public ResponseEntity<TaskGroupDto> createTaskGroup(
        @PathVariable("boardId") Long boardId,
        @RequestBody TaskGroupRequestDto requestDto) { 
        if(!taskBoardService.isExist(boardId)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        TaskGroupDto responseDto = taskGroupService.createTaskGroup(boardId, requestDto); 
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/boards/{boardId}/groups")
    public List<TaskGroupDto> listTaskGroups() {
        return taskGroupService.findAll(); 
    }

    // @GetMapping("/taskgroups/{id}/tasks")
    // public List<TaskDto> listTasksForGroup(@PathVariable Long id) {
    //     return taskService.findByGroupId(id)
    //         .stream()
    //         .map(taskMapper::mapTo)
    //         .toList();
    // }

    @GetMapping("/boards/{boardId}/groups/with-tasks")
        public List<TaskGroupWithTasksDto> listTaskGroupsWithTasks() {
            return taskGroupService.findAllWithTasks();
    }



    // @GetMapping(path = "/taskgroups")
    // public List<TaskGroupDto> listTaskGroups() {
    //     List<TaskGroupEntity> taskGroups = taskGroupService.findAll();
    //     return taskGroups.stream().map(taskGroupMapper::mapTo).collect(Collectors.toList());
    // }

    @DeleteMapping(path = "/boards/{boardId}/groups/{groupId}")
    public ResponseEntity deleteTaskGroup(@PathVariable("groupId") Long groupId){
        taskGroupService.delete(groupId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(path = "/boards/{boardId}/groups/{groupId}/tasks")
    public ResponseEntity deleteAllTasks(@PathVariable("groupId") Long groupId){
        taskGroupService.deleteAllTasks(groupId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(path = "/boards/{boardId}/groups/{groupId}")
    public ResponseEntity<TaskGroupDto> updateTaskGroup(
            @PathVariable("groupId") Long groupId, 
            @RequestBody TaskGroupRequestDto requestDto) {
        TaskGroupDto responseDto = taskGroupService.update(groupId, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

}
