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

import com.jonassavas.spring_task_api.domain.dto.task_board.TaskBoardDto;
import com.jonassavas.spring_task_api.domain.dto.task_board.TaskBoardRequestDto;
import com.jonassavas.spring_task_api.services.TaskBoardService;


@RestController
public class TaskBoardController {

    private TaskBoardService taskBoardService;


    public TaskBoardController(TaskBoardService taskBoardService){
        
        this.taskBoardService = taskBoardService;
    }

    // Do we need a way to get everything? 
    //      TaskBoard --> TaskGroups --> Tasks ?

    @PostMapping(path = "/boards")
    public ResponseEntity<TaskBoardDto> createTaskBoard(@RequestBody TaskBoardRequestDto requestDto) {
        TaskBoardDto responseDto = taskBoardService.createTaskBoard(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/boards/{boardId}")
    public ResponseEntity deleteTaskBoard(@PathVariable("boardId") Long boardId){
        taskBoardService.delete(boardId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @GetMapping("/boards")
    public List<TaskBoardDto> listTaskGroups() {
        return taskBoardService.findAll();
    }

    @PatchMapping(path = "/boards/{boardId}")
    public ResponseEntity<TaskBoardDto> updateTaskBoard(
            @PathVariable("boardId") Long boardId,
            @RequestBody TaskBoardRequestDto requestDto){
        TaskBoardDto responseDto = taskBoardService.update(boardId, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
