package com.jonassavas.spring_task_api.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.jonassavas.spring_task_api.domain.dto.CreateTaskGroupDto;
import com.jonassavas.spring_task_api.domain.dto.TaskDto;
import com.jonassavas.spring_task_api.domain.dto.TaskGroupDto;
import com.jonassavas.spring_task_api.domain.entities.TaskEntity;
import com.jonassavas.spring_task_api.domain.entities.TaskGroupEntity;
import com.jonassavas.spring_task_api.mappers.Mapper;
import com.jonassavas.spring_task_api.services.TaskGroupService;
import com.jonassavas.spring_task_api.services.TaskService;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class TaskGroupController {
    private TaskGroupService taskGroupService;

    private Mapper<TaskGroupEntity, CreateTaskGroupDto> createTaskGroupMapper;
    private Mapper<TaskGroupEntity, TaskGroupDto> taskGroupMapper;
    private Mapper<TaskEntity, TaskDto> taskMapper;
    private TaskService taskService;

    public TaskGroupController(TaskGroupService taskGroupService, 
                                Mapper<TaskGroupEntity, CreateTaskGroupDto> createTaskGroupMapper,
                                Mapper<TaskGroupEntity, TaskGroupDto> taskGroupMapper,
                                Mapper<TaskEntity, TaskDto> taskMapper,
                                TaskService taskService){
        this.taskGroupService = taskGroupService;
        this.createTaskGroupMapper = createTaskGroupMapper;
        this.taskGroupMapper = taskGroupMapper;
        this.taskMapper = taskMapper;
        this.taskService = taskService;
    }

    @PostMapping(path = "/taskgroups")
    public ResponseEntity<CreateTaskGroupDto> createTaskGroup(@RequestBody CreateTaskGroupDto taskGroup) {
        TaskGroupEntity taskGroupEntity = createTaskGroupMapper.mapFrom(taskGroup);
        TaskGroupEntity savedTaskGroupEntity = taskGroupService.save(taskGroupEntity);
        return new ResponseEntity<>(createTaskGroupMapper.mapTo(savedTaskGroupEntity), HttpStatus.CREATED);
    }

    @GetMapping(path = "/taskgroups")
    public List<TaskGroupDto> listTaskGroups() {
        List<TaskGroupEntity> taskGroups = taskGroupService.findAll();
        return taskGroups.stream().map(taskGroupMapper::mapTo).collect(Collectors.toList());
    }
    
    
    @PostMapping("/taskgroups/{groupId}/tasks")
    public ResponseEntity<TaskDto> addTask(
            @PathVariable Long groupId,
            @RequestBody TaskDto dto) {
        
        TaskEntity taskEntity = taskMapper.mapFrom(dto);
        TaskEntity savedTask = taskService.createTask(groupId, taskEntity);
        TaskDto responseDto = taskMapper.mapTo(savedTask);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

}
