package com.jonassavas.spring_task_api.services;

import com.jonassavas.spring_task_api.domain.dto.task_board.CreateTaskBoardRequestDto;
import com.jonassavas.spring_task_api.domain.dto.task_board.TaskBoardDto;
import com.jonassavas.spring_task_api.domain.dto.task_board.UpdateTaskBoardRequestDto;
import java.util.List;
import java.util.Optional;

public interface TaskBoardService {

    TaskBoardDto createTaskBoard(CreateTaskBoardRequestDto requestDto);

    // Save, Delete, Read, Update
    TaskBoardDto update(Long id, UpdateTaskBoardRequestDto requestDto);

    void delete(Long id);

    Optional<TaskBoardDto> findById(Long id);

    // Delete all taskGroups?
    boolean isExist(Long id);

    // List<TaskBoardDto> findAll();

    List<TaskBoardDto> listTaskBoardsForCurrentUser();
}
