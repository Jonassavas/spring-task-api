package com.jonassavas.spring_task_api.repositories;

import com.jonassavas.spring_task_api.domain.entities.TaskEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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

    Integer findMaxPositionByTaskGroupId(Long groupId);
}
