package com.jonassavas.spring_task_api.services;

import java.util.List;
import java.util.Optional;

import com.jonassavas.spring_task_api.domain.entities.TaskBoardEntity;

public interface TaskBoardService {

    TaskBoardEntity save(TaskBoardEntity taskBoard);

    // Save, Delete, Read, Update
    TaskBoardEntity update(Long id, TaskBoardEntity taskBoard);

    void delete(Long id);

    Optional<TaskBoardEntity> findById(Long id);

    // Delete all taskGroups?
    boolean isExist(Long id);

    List<TaskBoardEntity> findAll();
    
}
