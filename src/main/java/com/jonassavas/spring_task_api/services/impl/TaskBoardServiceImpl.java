package com.jonassavas.spring_task_api.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

import com.jonassavas.spring_task_api.domain.entities.TaskBoardEntity;
import com.jonassavas.spring_task_api.repositories.TaskBoardRepository;
import com.jonassavas.spring_task_api.services.TaskBoardService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class TaskBoardServiceImpl implements TaskBoardService {

    private TaskBoardRepository taskBoardRepository;

    public TaskBoardServiceImpl(TaskBoardRepository taskBoardRepository){
        this.taskBoardRepository = taskBoardRepository;
    }

    @Override
    public TaskBoardEntity save(TaskBoardEntity taskBoard){
        // Will need to check for the user later here
        return taskBoardRepository.save(taskBoard);
    } 

    // TODO check the taskgroup aswell for this: Might just want the dto as input here
    @Override
    public TaskBoardEntity update(Long id, TaskBoardEntity taskBoard){
        TaskBoardEntity board = taskBoardRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException(
                                    "TaskBoard not found with id: " + id));
        if(taskBoard.getName() != null){
            board.setName(taskBoard.getName());
        }

        return board; 
    }

    @Override
    public void delete(Long id){
        taskBoardRepository.deleteById(id);
    }

    @Override
    public Optional<TaskBoardEntity> findById(Long id){
        return taskBoardRepository.findById(id);
    }

    @Override
    public List<TaskBoardEntity> findAll(){
        return StreamSupport.stream(taskBoardRepository
                                    .findAll()
                                    .spliterator(), false)
                                    .collect(Collectors.toList());
    }

    @Override
    public boolean isExist(Long id){
        return taskBoardRepository.existsById(id);
    }
}
