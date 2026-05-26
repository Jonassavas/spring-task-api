package com.jonassavas.spring_task_api.controllers;

import com.jonassavas.spring_task_api.domain.dto.task.CreateTaskRequestDto;
import com.jonassavas.spring_task_api.domain.dto.task.ReorderTasksRequestDto;
import com.jonassavas.spring_task_api.domain.dto.task.TaskDto;
import com.jonassavas.spring_task_api.domain.dto.task.UpdateTaskRequestDto;
import com.jonassavas.spring_task_api.services.TaskService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/groups/{groupId}/tasks")
    public ResponseEntity<TaskDto> createTask(
            @PathVariable Long groupId, @Valid @RequestBody CreateTaskRequestDto dto) {

        TaskDto responseDto = taskService.createTask(groupId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/groups/{groupId}/tasks")
    public ResponseEntity<List<TaskDto>> getTasksByGroup(@PathVariable Long groupId) {

        List<TaskDto> tasks = taskService.findByGroup(groupId);
        return ResponseEntity.ok(tasks);
    }

    @PatchMapping("/tasks/{taskId}")
    public ResponseEntity<TaskDto> update(
            @PathVariable Long taskId, @Valid @RequestBody UpdateTaskRequestDto requestDto) {

        TaskDto responseDto = taskService.update(taskId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/tasks/reorder")
    public ResponseEntity<Void> reorderTasks(
            @Valid @RequestBody ReorderTasksRequestDto requestDto) {

        taskService.reorderTasks(requestDto);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {

        taskService.delete(taskId);
        return ResponseEntity.noContent().build();
    }
}
