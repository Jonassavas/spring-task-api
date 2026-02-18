package com.jonassavas.spring_task_api.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

import com.jonassavas.spring_task_api.domain.dto.task_board.TaskBoardDto;
import com.jonassavas.spring_task_api.domain.dto.task_board.TaskBoardRequestDto;
import com.jonassavas.spring_task_api.domain.entities.TaskBoardEntity;
import com.jonassavas.spring_task_api.mappers.Mapper;
import com.jonassavas.spring_task_api.repositories.TaskBoardRepository;
import com.jonassavas.spring_task_api.services.TaskBoardService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class TaskBoardServiceImpl implements TaskBoardService {

    private TaskBoardRepository taskBoardRepository;
    private Mapper<TaskBoardEntity, TaskBoardRequestDto> taskBoardRequestMapper;
    private Mapper<TaskBoardEntity, TaskBoardDto> taskBoardMapper;


    public TaskBoardServiceImpl(TaskBoardRepository taskBoardRepository,
                                Mapper<TaskBoardEntity, TaskBoardRequestDto> taskBoardRequestMapper,
                                Mapper<TaskBoardEntity, TaskBoardDto> taskBoardMapper){
        this.taskBoardRepository = taskBoardRepository;
        this.taskBoardRequestMapper = taskBoardRequestMapper;
        this.taskBoardMapper = taskBoardMapper;
    }

    @Override
    public TaskBoardDto createTaskBoard(TaskBoardRequestDto requestDto){
        // Will need to check for the user later here
        TaskBoardEntity taskBoard = taskBoardRequestMapper.mapFrom(requestDto);
        TaskBoardEntity savedTaskBoard = taskBoardRepository.save(taskBoard);
        return taskBoardMapper.mapTo(savedTaskBoard);
    } 

    // TODO check the taskgroup aswell for this: Might just want the dto as input here
    @Override
    public TaskBoardEntity update(Long id, TaskBoardEntity taskBoard){
        TaskBoardEntity board = taskBoardRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException(
                                    "TaskBoard not found with id: " + id));
        if(taskBoard.getTaskBoardName() != null){
            board.setTaskBoardName(taskBoard.getTaskBoardName());
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
