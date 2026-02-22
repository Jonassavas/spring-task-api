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
import com.jonassavas.spring_task_api.domain.entities.TaskGroupEntity;
import com.jonassavas.spring_task_api.mappers.Mapper;
import com.jonassavas.spring_task_api.services.TaskBoardService;
import com.jonassavas.spring_task_api.services.TaskGroupService;


@RestController
public class TaskGroupController {
    private TaskGroupService taskGroupService;

    private Mapper<TaskGroupEntity, TaskGroupRequestDto> taskGroupRequestMapper;
    private Mapper<TaskGroupEntity, TaskGroupWithTasksDto> taskGroupWithTasksMapper;
    private Mapper<TaskGroupEntity, TaskGroupDto> taskGroupMapper;
    private TaskBoardService taskBoardService;

    public TaskGroupController(TaskGroupService taskGroupService, 
                                Mapper<TaskGroupEntity, TaskGroupRequestDto> taskGroupRequestMapper,
                                Mapper<TaskGroupEntity, TaskGroupDto> taskGroupMapper,
                                Mapper<TaskGroupEntity, TaskGroupWithTasksDto> taskGroupWithTasksMapper,
                                TaskBoardService taskBoardService){
        this.taskGroupService = taskGroupService;
        this.taskGroupRequestMapper = taskGroupRequestMapper;
        this.taskGroupMapper = taskGroupMapper;
        this.taskGroupWithTasksMapper = taskGroupWithTasksMapper;
        this.taskBoardService = taskBoardService;
    }

    @PostMapping(path = "/boards/{boardId}/groups")
    public ResponseEntity<TaskGroupRequestDto> createTaskGroup(@PathVariable("boardId") Long boardId,
                                                                @RequestBody TaskGroupRequestDto taskGroup) {

        
        //taskGroup.setTaskBoardId(boardId);
        if(!taskBoardService.isExist(boardId)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        TaskGroupEntity taskGroupEntity = taskGroupRequestMapper.mapFrom(taskGroup);
        TaskGroupEntity savedTaskGroupEntity = taskGroupService.createTaskGroup(boardId, taskGroupEntity);
        TaskGroupRequestDto responseDto = taskGroupRequestMapper.mapTo(savedTaskGroupEntity);
        
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/boards/{boardId}/groups")
    public List<TaskGroupDto> listTaskGroups() {
        return taskGroupService.findAll()
            .stream()
            .map(taskGroupMapper::mapTo)
            .toList();
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
            return taskGroupService.findAllWithTasks()
                .stream()
                .map(taskGroupWithTasksMapper::mapTo)
                .toList();
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
            @PathVariable("groupId") Long id, 
            @RequestBody TaskGroupRequestDto dto) {
        TaskGroupEntity updated = taskGroupService.update(id, dto);
        return new ResponseEntity<>(taskGroupRequestMapper.mapTo(updated), HttpStatus.OK);
    }

}
