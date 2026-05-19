package com.jonassavas.spring_task_api.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jonassavas.spring_task_api.domain.dto.task_group.CreateTaskGroupRequestDto;
import com.jonassavas.spring_task_api.domain.dto.task_group.ReorderTaskGroupRequestDto;
import com.jonassavas.spring_task_api.domain.dto.task_group.TaskGroupDto;
import com.jonassavas.spring_task_api.domain.dto.task_group.TaskGroupWithTasksDto;
import com.jonassavas.spring_task_api.domain.dto.task_group.UpdateTaskGroupRequestDto;
import com.jonassavas.spring_task_api.domain.entities.TaskBoardEntity;
import com.jonassavas.spring_task_api.domain.entities.TaskGroupEntity;
import com.jonassavas.spring_task_api.mappers.Mapper;
import com.jonassavas.spring_task_api.repositories.TaskBoardRepository;
import com.jonassavas.spring_task_api.repositories.TaskGroupRepository;
import com.jonassavas.spring_task_api.security.SecurityService;
import com.jonassavas.spring_task_api.services.TaskGroupService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class TaskGroupServiceImpl implements TaskGroupService {

    private final TaskGroupRepository taskGroupRepository;
    private final TaskBoardRepository taskBoardRepository;
    private final Mapper<TaskGroupEntity, TaskGroupWithTasksDto> taskGroupWithTasksMapper;
    private final Mapper<TaskGroupEntity, TaskGroupDto> taskGroupMapper;
    private final SecurityService securityService;

    public TaskGroupServiceImpl(
            TaskGroupRepository taskGroupRepository,
            TaskBoardRepository taskBoardRepository,
            Mapper<TaskGroupEntity, TaskGroupWithTasksDto> taskGroupWithTasksMapper,
            Mapper<TaskGroupEntity, TaskGroupDto> taskGroupMapper,
            SecurityService securityService) {
        this.taskGroupRepository = taskGroupRepository;
        this.taskBoardRepository = taskBoardRepository;
        this.taskGroupWithTasksMapper = taskGroupWithTasksMapper;
        this.taskGroupMapper = taskGroupMapper;
        this.securityService = securityService;
    }

    @Override
    public TaskGroupDto createTaskGroup(Long boardId, CreateTaskGroupRequestDto dto) {

        String username = securityService.getCurrentUsername();

        TaskBoardEntity taskBoard =
                taskBoardRepository
                        .findByIdAndOwnerUsername(boardId, username)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "Taskboard not found or not owned by user"));

        TaskGroupEntity taskGroup =
                TaskGroupEntity.builder().taskGroupName(dto.getTaskGroupName()).build();

        taskBoard.addTaskGroup(taskGroup); // sets relationship

        TaskGroupEntity saved = taskGroupRepository.save(taskGroup);

        return taskGroupMapper.mapTo(saved);
    }

    @Override
    public List<TaskGroupDto> listGroupsOnBoard(Long boardId) {

        String username = securityService.getCurrentUsername();

        return taskGroupRepository
                .findByTaskBoardIdAndTaskBoardOwnerUsername(boardId, username)
                .stream()
                .map(taskGroupMapper::mapTo)
                .collect(Collectors.toList());
    }

    @Override
    public TaskGroupWithTasksDto findByIdWithTasks(Long id) {

        String username = securityService.getCurrentUsername();

        TaskGroupEntity taskGroup =
                taskGroupRepository
                        .findByIdWithTasksAndUsername(id, username)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "TaskGroup not found or not owned by user"));

        return taskGroupWithTasksMapper.mapTo(taskGroup);
    }

    @Override
    public boolean isExist(Long id) {
        return taskGroupRepository.existsById(id);
    }

    @Override
    public void delete(Long id) {

        String username = securityService.getCurrentUsername();

        TaskGroupEntity taskGroup =
                taskGroupRepository
                        .findByIdAndTaskBoardOwnerUsername(id, username)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "TaskGroup not found or not owned by user"));

        TaskBoardEntity board = taskGroup.getTaskBoard();

        board.removeTaskGroup(taskGroup);
    }

    @Override
    public TaskGroupDto update(Long id, UpdateTaskGroupRequestDto dto) {

        String username = securityService.getCurrentUsername();

        TaskGroupEntity taskGroup =
                taskGroupRepository
                        .findByIdAndTaskBoardOwnerUsername(id, username)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "TaskGroup not found or not owned by user"));

        if (dto.getTaskGroupName() != null) {
            taskGroup.setTaskGroupName(dto.getTaskGroupName());
        }

        return taskGroupMapper.mapTo(taskGroup);
    }

    @Override
    public void deleteAllTasks(Long id) {

        String username = securityService.getCurrentUsername();

        TaskGroupEntity taskGroup =
                taskGroupRepository
                        .findByIdAndTaskBoardOwnerUsername(id, username)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "TaskGroup not found or not owned by user"));

        taskGroup.getTasks().clear();
    }

    @Override
    public void reorderTaskGroups(Long boardId, List<ReorderTaskGroupRequestDto> dtoList){

    }
}
