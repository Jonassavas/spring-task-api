package com.jonassavas.spring_task_api.controllers;

import com.jonassavas.spring_task_api.domain.dto.task_board.CreateTaskBoardRequestDto;
import com.jonassavas.spring_task_api.domain.dto.task_board.TaskBoardDto;
import com.jonassavas.spring_task_api.domain.dto.task_board.TaskBoardWithGroupsDto;
import com.jonassavas.spring_task_api.domain.dto.task_board.UpdateTaskBoardRequestDto;
import com.jonassavas.spring_task_api.services.TaskBoardService;
import jakarta.validation.Valid;
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

@RestController
@RequestMapping("/taskboards")
public class TaskBoardController {

    private TaskBoardService taskBoardService;

    public TaskBoardController(TaskBoardService taskBoardService) {
        this.taskBoardService = taskBoardService;
    }

    @PostMapping
    public ResponseEntity<TaskBoardDto> createTaskBoard(
            @Valid @RequestBody CreateTaskBoardRequestDto requestDto) {
        TaskBoardDto responseDto = taskBoardService.createTaskBoard(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<TaskBoardDto>> listTaskBoards() {

        List<TaskBoardDto> boards = taskBoardService.listTaskBoardsForCurrentUser();

        return ResponseEntity.ok(boards);
    }

    // Might need to get taskGroups here aswell (lazy loaded)
    @GetMapping("/{boardId}")
    public ResponseEntity<TaskBoardWithGroupsDto> getCompleteTaskBoard(@PathVariable Long boardId) {

        return ResponseEntity.ok(taskBoardService.getTaskBoardWithDetails(boardId));
    }

    @PatchMapping("/{boardId}")
    public ResponseEntity<TaskBoardDto> updateTaskBoard(
            @PathVariable Long boardId, @Valid @RequestBody UpdateTaskBoardRequestDto requestDto) {

        TaskBoardDto responseDto = taskBoardService.update(boardId, requestDto);

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteTaskBoard(@PathVariable Long boardId) {

        taskBoardService.delete(boardId);

        return ResponseEntity.noContent().build();
    }
}
