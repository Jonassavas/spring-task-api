package com.jonassavas.spring_task_api.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.jonassavas.spring_task_api.domain.dto.task.TaskDto;
import com.jonassavas.spring_task_api.domain.dto.task.TaskRequestDto;
import com.jonassavas.spring_task_api.domain.entities.TaskEntity;
import com.jonassavas.spring_task_api.mappers.Mapper;
import com.jonassavas.spring_task_api.services.TaskGroupService;
import com.jonassavas.spring_task_api.services.TaskService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
//@RequestMapping(path = "/taskgroups/{groupId}/tasks")
public class TaskController {
    
    private Mapper<TaskEntity, TaskDto> taskMapper;
    private Mapper<TaskEntity, TaskRequestDto> taskRequestMapper;
    private TaskService taskService;
    private TaskGroupService taskGroupService;


    public TaskController(Mapper<TaskEntity, TaskDto> taskMapper, 
                        TaskService taskService, 
                        TaskGroupService taskGroupService,
                        Mapper<TaskEntity, TaskRequestDto> taskRequestMapper){
        this.taskMapper = taskMapper;
        this.taskService = taskService;
        this.taskGroupService = taskGroupService;
        this.taskRequestMapper = taskRequestMapper;
    }
    

    @PostMapping("/groups/{groupId}/tasks")
    public ResponseEntity<TaskDto> createTask(
            @PathVariable Long groupId,
            @RequestBody TaskDto dto) {

        // Can't create a task without a valid task group
        if(!taskGroupService.isExist(groupId)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        TaskEntity taskEntity = taskMapper.mapFrom(dto);
        TaskEntity savedTask = taskService.createTask(groupId, taskEntity);
        TaskDto responseDto = taskMapper.mapTo(savedTask);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/tasks/{taskId}")
    public ResponseEntity deleteTask(@PathVariable("taskId") Long id){
        taskService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(path = "/tasks/{taskId}")
    public ResponseEntity<TaskRequestDto> update(@PathVariable("taskId") Long id, @RequestBody TaskRequestDto dto){
        TaskEntity updated = taskService.update(id, dto);
        
        return new ResponseEntity<>(taskRequestMapper.mapTo(updated), HttpStatus.OK);
    }
}
