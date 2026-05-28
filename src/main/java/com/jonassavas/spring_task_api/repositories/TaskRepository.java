package com.jonassavas.spring_task_api.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jonassavas.spring_task_api.domain.entities.TaskEntity;

/*
@Repository Annotation is a specialization of the @Component annotation,
which is used to indicate that the class provides the mechanism for storage,
retrieval, update, delete, and search operation on objects.*/
@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    boolean existsByIdAndTaskGroupTaskBoardOwnerUsername(Long id, String username);

    Optional<TaskEntity> findByIdAndTaskGroupTaskBoardOwnerUsername(Long id, String username);

    List<TaskEntity> findByTaskGroupIdAndTaskGroupTaskBoardOwnerUsernameOrderByPositionAsc(
            Long groupId, String username);

    @Query("SELECT COALESCE(MAX(t.position), -1) FROM TaskEntity t WHERE t.taskGroup.id = :groupId")
    Integer findMaxPositionByTaskGroupId(@Param("groupId") Long groupId);
}
