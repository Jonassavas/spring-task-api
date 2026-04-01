package com.jonassavas.spring_task_api.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jonassavas.spring_task_api.domain.entities.TaskEntity;

/*
@Repository Annotation is a specialization of the @Component annotation, 
which is used to indicate that the class provides the mechanism for storage, 
retrieval, update, delete, and search operation on objects.*/
@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    // Finding an individual Task with its id, requiring the correct authorized user.
    Optional<TaskEntity> findByIdAndTaskGroupTaskBoardOwnerUsername(Long id, String username);
    // Finding all Task within a TaskGroup using the groupId, requiring the correct authorized user.
    List<TaskEntity> findByTaskGroupIdAndTaskGroupTaskBoardOwnerUsername(Long groupId, String username);
}
