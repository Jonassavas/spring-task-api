package com.jonassavas.spring_task_api.services.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jonassavas.spring_task_api.domain.dto.task.CreateTaskRequestDto;
import com.jonassavas.spring_task_api.domain.dto.task.ReorderTasksRequestDto;
import com.jonassavas.spring_task_api.domain.dto.task.TaskDto;
import com.jonassavas.spring_task_api.domain.dto.task.UpdateTaskRequestDto;
import com.jonassavas.spring_task_api.domain.entities.TaskEntity;
import com.jonassavas.spring_task_api.domain.entities.TaskGroupEntity;
import com.jonassavas.spring_task_api.mappers.Mapper;
import com.jonassavas.spring_task_api.repositories.TaskGroupRepository;
import com.jonassavas.spring_task_api.repositories.TaskRepository;
import com.jonassavas.spring_task_api.security.SecurityService;
import com.jonassavas.spring_task_api.services.TaskService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final TaskGroupRepository taskGroupRepository;
    private final Mapper<TaskEntity, TaskDto> taskMapper;
    private final SecurityService securityService;

    public TaskServiceImpl(
            TaskRepository taskRepository,
            TaskGroupRepository taskGroupRepository,
            Mapper<TaskEntity, TaskDto> taskMapper,
            SecurityService securityService) {
        this.taskRepository = taskRepository;
        this.taskGroupRepository = taskGroupRepository;
        this.taskMapper = taskMapper;
        this.securityService = securityService;
    }

    @Override
    public TaskDto createTask(Long groupId, CreateTaskRequestDto taskRequestDto) {

        String username = securityService.getCurrentUsername();

        TaskGroupEntity taskGroup =
                taskGroupRepository
                        .findByIdAndTaskBoardOwnerUsername(groupId, username)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "TaskGroup not found or not owned by user"));

        Integer maxPosition = taskRepository.findMaxPositionByTaskGroupId(groupId);

        int nextPosition = (maxPosition == null) ? 0 : maxPosition + 1;

        TaskEntity taskEntity =
                TaskEntity.builder()
                        .taskName(taskRequestDto.getTaskName())
                        .position(nextPosition)
                        .build();

        taskGroup.addTask(taskEntity);

        TaskEntity saved = taskRepository.save(taskEntity);

        return taskMapper.mapTo(saved);
    }

    @Override
    public void delete(Long id) {

        String username = securityService.getCurrentUsername();

        TaskEntity task =
                taskRepository
                        .findByIdAndTaskGroupTaskBoardOwnerUsername(id, username)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "Task not found or not owned by user"));

        TaskGroupEntity group = task.getTaskGroup();

        group.removeTask(task);
    }

    @Override
    public boolean isExist(Long id) {
        return taskRepository.existsById(id);
    }

    @Override
    public List<TaskDto> findByGroup(Long groupId) {

        String username = securityService.getCurrentUsername();

        return taskRepository
                .findByTaskGroupIdAndTaskGroupTaskBoardOwnerUsernameOrderByPositionAsc(
                        groupId, username)
                .stream()
                .map(taskMapper::mapTo)
                .collect(Collectors.toList());
    }

    @Override
    public TaskDto update(Long id, UpdateTaskRequestDto taskRequestDto) {

        String username = securityService.getCurrentUsername();

        TaskEntity task =
                taskRepository
                        .findByIdAndTaskGroupTaskBoardOwnerUsername(id, username)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "Task not found or not owned by user"));

        if (taskRequestDto.getTaskName() != null) {
            task.setTaskName(taskRequestDto.getTaskName());
        }

        return taskMapper.mapTo(task);
    }

private void validateReorderRequest(
        ReorderTasksRequestDto dto,
        String username) {

    List<Long> allIds = new ArrayList<>();
    allIds.addAll(dto.getSourceTaskIds());

    // FIX: Only combine lists if moving across different columns.
    // If it's the same column, source and destination lists are identical.
    if (!dto.getSourceGroupId().equals(dto.getDestinationGroupId())) {
        allIds.addAll(dto.getDestinationTaskIds());
    }

    // No duplicates within the expected scope
    Set<Long> uniqueIds = new HashSet<>(allIds);

    if (uniqueIds.size() != allIds.size()) {
        throw new IllegalArgumentException(
                "Duplicate task IDs in reorder request");
    }

    // No null IDs
    if (uniqueIds.contains(null)) {
        throw new IllegalArgumentException(
                "Null task ID in reorder request");
    }

    // Verify all tasks exist and belong to user
    for (Long id : uniqueIds) {
        boolean exists = taskRepository
                .existsByIdAndTaskGroupTaskBoardOwnerUsername(id, username);

        if (!exists) {
            throw new EntityNotFoundException(
                    "Task not found or not owned: " + id);
        }
    }
} 

@Override
@Transactional // Good practice to ensure both phases and flushes are atomic
public void reorderTasks(ReorderTasksRequestDto dto) {

    String username = securityService.getCurrentUsername();

    TaskGroupEntity sourceGroup = taskGroupRepository
            .findByIdAndTaskBoardOwnerUsername(dto.getSourceGroupId(), username)
            .orElseThrow(() -> new EntityNotFoundException("Source group not found"));

    TaskGroupEntity destinationGroup = taskGroupRepository
            .findByIdAndTaskBoardOwnerUsername(dto.getDestinationGroupId(), username)
            .orElseThrow(() -> new EntityNotFoundException("Destination group not found"));

    validateReorderRequest(dto, username);

    // Build the unique list of tasks to fetch from DB
    List<Long> fetchIds = new ArrayList<>(dto.getSourceTaskIds());
    if (!dto.getSourceGroupId().equals(dto.getDestinationGroupId())) {
        fetchIds.addAll(dto.getDestinationTaskIds());
    }

    List<TaskEntity> tasks = taskRepository.findAllById(fetchIds);

    Map<Long, TaskEntity> taskMap = tasks.stream()
            .collect(Collectors.toMap(TaskEntity::getId, t -> t));

    // -----------------------------------
    // PHASE 1: assign temporary positions
    // -----------------------------------
    int tempPosition = -1;
    for (TaskEntity task : tasks) {
        task.setPosition(tempPosition--);
    }

    // Flush changes to avoid temporary database unique constraint collisions
    taskRepository.flush();

    // -----------------------------------
    // PHASE 2: assign final positions
    // -----------------------------------
    boolean isSameGroup = dto.getSourceGroupId().equals(dto.getDestinationGroupId());

    if (isSameGroup) {
        // If moving within the same column, we only need to process one list
        for (int i = 0; i < dto.getSourceTaskIds().size(); i++) {
            TaskEntity task = taskMap.get(dto.getSourceTaskIds().get(i));
            if (task != null) {
                task.setPosition(i);
            }
        }
    } else {
        // CROSS COLUMN: Source group updates positions
        for (int i = 0; i < dto.getSourceTaskIds().size(); i++) {
            TaskEntity task = taskMap.get(dto.getSourceTaskIds().get(i));
            if (task != null) {
                task.setPosition(i);
            }
        }

        // CROSS COLUMN: Destination group updates parent mapping and positions
        for (int i = 0; i < dto.getDestinationTaskIds().size(); i++) {
            TaskEntity task = taskMap.get(dto.getDestinationTaskIds().get(i));
            if (task != null) {
                task.setTaskGroup(destinationGroup);
                task.setPosition(i);
            }
        }
    }
} 
}
