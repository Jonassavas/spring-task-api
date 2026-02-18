package com.jonassavas.spring_task_api.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.jonassavas.spring_task_api.domain.dto.task_board.TaskBoardDto;
import com.jonassavas.spring_task_api.domain.dto.task_board.TaskBoardRequestDto;
import com.jonassavas.spring_task_api.domain.entities.TaskBoardEntity;
import com.jonassavas.spring_task_api.mappers.Mapper;
import com.jonassavas.spring_task_api.services.TaskBoardService;
import com.jonassavas.spring_task_api.services.TaskGroupService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class TaskBoardController {

    private TaskBoardService taskBoardService;
    private Mapper<TaskBoardEntity, TaskBoardDto> taskBoardMapper;

    public TaskBoardController(TaskBoardService taskBoardService,
                            Mapper<TaskBoardEntity, TaskBoardDto> taskBoardMapper){
        
        this.taskBoardService = taskBoardService;
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

    @PostMapping(path = "/boards")
    public ResponseEntity<TaskBoardDto> createTaskBoard(@RequestBody TaskBoardRequestDto taskBoardDto) {
        TaskBoardDto responseDto = taskBoardService.createTaskBoard(taskBoardDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
    
    
}
