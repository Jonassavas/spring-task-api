package com.jonassavas.spring_task_api.services.impl;

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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

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
            String username,
            List<TaskEntity> sourceTasks,
            List<TaskEntity> destinationTasks) {

        // 1. Collect all incoming IDs
        List<Long> allIncomingIds = new ArrayList<>();
        allIncomingIds.addAll(dto.getSourceTaskIds());
        allIncomingIds.addAll(dto.getDestinationTaskIds());

        // 2. Check for duplicates in request
        Set<Long> uniqueIds = new HashSet<>(allIncomingIds);
        if (uniqueIds.size() != allIncomingIds.size()) {
            throw new IllegalArgumentException("Duplicate task IDs in reorder request");
        }

        // 3. Ensure no nulls (frontend safety)
        if (uniqueIds.contains(null)) {
            throw new IllegalArgumentException("Null task ID in reorder request");
        }

        // 4. Validate ownership + existence
        for (Long id : uniqueIds) {
            boolean exists =
                    taskRepository.existsByIdAndTaskGroupTaskBoardOwnerUsername(id, username);

            if (!exists) {
                throw new EntityNotFoundException("Task not found or not owned: " + id);
            }
        }

        // 5. Ensure request matches current DB state (no missing / extra tasks)
        Set<Long> currentTaskIds = new HashSet<>();

        for (TaskEntity t : sourceTasks) {
            currentTaskIds.add(t.getId());
        }

        for (TaskEntity t : destinationTasks) {
            currentTaskIds.add(t.getId());
        }

        if (!currentTaskIds.equals(uniqueIds)) {
            throw new IllegalArgumentException(
                    "Reorder request does not match current board state (missing or extra tasks)");
        }
    }

    @Override
    public void reorderTasks(ReorderTasksRequestDto dto) {

        String username = securityService.getCurrentUsername();

        TaskGroupEntity sourceGroup =
                taskGroupRepository
                        .findByIdAndTaskBoardOwnerUsername(dto.getSourceGroupId(), username)
                        .orElseThrow(() -> new EntityNotFoundException("Source group not found"));

        TaskGroupEntity destinationGroup =
                taskGroupRepository
                        .findByIdAndTaskBoardOwnerUsername(dto.getDestinationGroupId(), username)
                        .orElseThrow(
                                () -> new EntityNotFoundException("Destination group not found"));

        List<Long> allIds = new ArrayList<>();
        allIds.addAll(dto.getSourceTaskIds());
        allIds.addAll(dto.getDestinationTaskIds());

        List<TaskEntity> tasks = taskRepository.findAllById(allIds);

        Map<Long, TaskEntity> taskMap =
                tasks.stream().collect(Collectors.toMap(TaskEntity::getId, t -> t));

        // VALIDATION
        validateReorderRequest(dto, username, tasks, tasks);

        // apply source ordering
        for (int i = 0; i < dto.getSourceTaskIds().size(); i++) {
            TaskEntity task = taskMap.get(dto.getSourceTaskIds().get(i));
            task.setPosition(i);
        }

        // apply destination ordering
        for (int i = 0; i < dto.getDestinationTaskIds().size(); i++) {
            TaskEntity task = taskMap.get(dto.getDestinationTaskIds().get(i));
            task.setTaskGroup(destinationGroup);
            task.setPosition(i);
        }
    }
}
