package com.jonassavas.spring_task_api.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.jonassavas.spring_task_api.domain.dto.TaskGroupDto;
import com.jonassavas.spring_task_api.domain.entities.TaskGroupEntity;
import com.jonassavas.spring_task_api.mappers.Mapper;
import com.jonassavas.spring_task_api.services.TaskGroupService;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class TaskGroupController {
    private TaskGroupService taskGroupService;

    private Mapper<TaskGroupEntity, TaskGroupDto> taskGroupMapper;

    public TaskGroupController(TaskGroupService taskGroupService, Mapper<TaskGroupEntity, TaskGroupDto> taskGroupMapper){
        this.taskGroupService = taskGroupService;
        this.taskGroupMapper = taskGroupMapper;
    }

    @PostMapping(path = "/taskgroups")
    public ResponseEntity<TaskGroupDto> createTaskGroup(@RequestBody TaskGroupDto taskGroup) {
        TaskGroupEntity taskGroupEntity = taskGroupMapper.mapFrom(taskGroup);
        TaskGroupEntity savedTaskGroupEntity = taskGroupService.save(taskGroupEntity);
        return new ResponseEntity<>(taskGroupMapper.mapTo(savedTaskGroupEntity), HttpStatus.CREATED);
    }

    @GetMapping(path = "/taskgroups")
    public List<TaskGroupDto> listTaskGroups() {
        List<TaskGroupEntity> taskGroups = taskGroupService.findAll();
        return taskGroups.stream().map(taskGroupMapper::mapTo).collect(Collectors.toList());
    }
    
}
