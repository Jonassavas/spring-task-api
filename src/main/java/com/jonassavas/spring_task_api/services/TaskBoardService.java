package com.jonassavas.spring_task_api.services;

import java.util.List;
import java.util.Optional;

import com.jonassavas.spring_task_api.domain.dto.task_board.TaskBoardDto;
import com.jonassavas.spring_task_api.domain.dto.task_board.TaskBoardRequestDto;
import com.jonassavas.spring_task_api.domain.entities.TaskBoardEntity;

public interface TaskBoardService {

    TaskBoardDto createTaskBoard(TaskBoardRequestDto requestDto);

    // Save, Delete, Read, Update
    TaskBoardDto update(Long id, TaskBoardRequestDto requestDto);

    void delete(Long id);

    Optional<TaskBoardEntity> findById(Long id);

    // Delete all taskGroups?
    boolean isExist(Long id);

    List<TaskBoardEntity> findAll();
    
}
