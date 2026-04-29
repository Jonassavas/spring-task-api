package com.jonassavas.spring_task_api.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jonassavas.spring_task_api.domain.dto.task_board.CreateTaskBoardRequestDto;
import com.jonassavas.spring_task_api.domain.dto.task_board.TaskBoardDto;
import com.jonassavas.spring_task_api.domain.dto.task_board.TaskBoardWithGroupsDto;
import com.jonassavas.spring_task_api.domain.dto.task_board.UpdateTaskBoardRequestDto;
import com.jonassavas.spring_task_api.domain.entities.TaskBoardEntity;
import com.jonassavas.spring_task_api.domain.entities.UserEntity;
import com.jonassavas.spring_task_api.mappers.Mapper;
import com.jonassavas.spring_task_api.mappers.impl.task_board.TaskBoardMapper;
import com.jonassavas.spring_task_api.repositories.TaskBoardRepository;
import com.jonassavas.spring_task_api.security.SecurityService;
import com.jonassavas.spring_task_api.services.TaskBoardService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class TaskBoardServiceImpl implements TaskBoardService {

    private TaskBoardRepository taskBoardRepository;
    private Mapper<TaskBoardEntity, CreateTaskBoardRequestDto> taskBoardRequestMapper;
    private final TaskBoardMapper taskBoardMapper;
    private final SecurityService securityService;

    public TaskBoardServiceImpl(
            TaskBoardRepository taskBoardRepository,
            Mapper<TaskBoardEntity, CreateTaskBoardRequestDto> taskBoardRequestMapper,
            TaskBoardMapper taskBoardMapper,
            SecurityService securityService) {
        this.taskBoardRepository = taskBoardRepository;
        this.taskBoardRequestMapper = taskBoardRequestMapper;
        this.taskBoardMapper = taskBoardMapper;
        this.securityService = securityService;
    }

    @Override
    public TaskBoardDto createTaskBoard(CreateTaskBoardRequestDto requestDto) {
        UserEntity user = securityService.getCurrentUser();
        TaskBoardEntity taskBoard =
                TaskBoardEntity.builder()
                        .taskBoardName(requestDto.getTaskBoardName())
                        .owner(user)
                        .build();
        TaskBoardEntity savedTaskBoard = taskBoardRepository.save(taskBoard);
        return taskBoardMapper.mapTo(savedTaskBoard);
    }

    @Override
    public TaskBoardDto update(Long id, UpdateTaskBoardRequestDto requestDto) {
        UserEntity user = securityService.getCurrentUser();

        TaskBoardEntity board =
                taskBoardRepository
                        .findByIdAndOwnerUsername(id, user.getUsername())
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "TaskBoard not found with id: "
                                                        + id
                                                        + " for user: "
                                                        + user.getUsername()));

        if (requestDto.getTaskBoardName() != null) {
            board.setTaskBoardName(requestDto.getTaskBoardName());
        }

        return taskBoardMapper.mapTo(board);
    }

    @Override
    public void delete(Long id) {
        UserEntity user = securityService.getCurrentUser();

        TaskBoardEntity board =
                taskBoardRepository
                        .findByIdAndOwnerUsername(id, user.getUsername())
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "TaskBoard not found or not owned by user"));

        taskBoardRepository.delete(board);
    }

    @Override
    public Optional<TaskBoardDto> findById(Long id) {

        UserEntity user = securityService.getCurrentUser();

        Optional<TaskBoardEntity> taskBoard =
                taskBoardRepository.findByIdAndOwnerUsername(id, user.getUsername());

        return taskBoard.map(taskBoardMapper::mapTo);
    }

    @Override
    public TaskBoardWithGroupsDto getTaskBoardWithDetails(Long boardId) {

        UserEntity user = securityService.getCurrentUser();

        TaskBoardEntity board = taskBoardRepository
                .findByIdAndOwnerUsernameWithGroupsAndTasks(boardId, user.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("TaskBoard not found"));

        return taskBoardMapper.mapToWithGroups(board);
    } 

    @Override
    public List<TaskBoardDto> listTaskBoardsForCurrentUser() {

        UserEntity user = securityService.getCurrentUser();

        return taskBoardRepository.findByOwnerUsername(user.getUsername()).stream()
                .map(taskBoardMapper::mapTo)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isExist(Long id) {
        return taskBoardRepository.existsById(id);
    }
}
