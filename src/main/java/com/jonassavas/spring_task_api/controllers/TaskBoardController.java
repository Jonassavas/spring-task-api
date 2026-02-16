package com.jonassavas.spring_task_api.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.jonassavas.spring_task_api.domain.dto.TaskBoardDto;
import com.jonassavas.spring_task_api.domain.entities.TaskBoardEntity;
import com.jonassavas.spring_task_api.mappers.Mapper;
import com.jonassavas.spring_task_api.services.TaskGroupService;

@RestController
public class TaskBoardController {

    private TaskGroupService taskGroupService;
    private Mapper<TaskBoardEntity, TaskBoardDto> taskBoardMapper;

    public TaskBoardController(TaskGroupService taskGroupService,
                            Mapper<TaskBoardEntity, TaskBoardDto> taskBoardMapper){
        
        this.taskGroupService = taskGroupService;
        this.taskBoardMapper = taskBoardMapper;
    }

    // POST /boards
    // GET /boards (used for selecting which board)
    // GET /boards/{boardId}

    // We need a way to create a taskboard
    // We need a way to update the name of a taskboard
    // We need a way to delete a taskboard
    // Do we need a way to get everything? 
    //      TaskBoard --> TaskGroups --> Tasks ?

    
}
