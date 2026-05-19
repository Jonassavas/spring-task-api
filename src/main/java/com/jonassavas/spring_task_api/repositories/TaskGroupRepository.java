package com.jonassavas.spring_task_api.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jonassavas.spring_task_api.domain.entities.TaskGroupEntity;

/*
@Repository Annotation is a specialization of the @Component annotation,
which is used to indicate that the class provides the mechanism for storage,
retrieval, update, delete, and search operation on objects.*/
@Repository
public interface TaskGroupRepository extends JpaRepository<TaskGroupEntity, Long> {

    List<TaskGroupEntity> findByTaskBoardIdAndTaskBoardOwnerUsername(Long boardId, String username);

    Optional<TaskGroupEntity> findByIdAndTaskBoardOwnerUsername(Long id, String username);

    @Query(
            """
        SELECT tg FROM TaskGroupEntity tg
        LEFT JOIN FETCH tg.tasks
        WHERE tg.taskBoard.owner.username = :username
    """)
    List<TaskGroupEntity> findAllWithTasksByUsername(String username);

    @Query(
            """
        SELECT tg FROM TaskGroupEntity tg
        LEFT JOIN FETCH tg.tasks
        WHERE tg.id = :id AND tg.taskBoard.owner.username = :username
    """)
    Optional<TaskGroupEntity> findByIdWithTasksAndUsername(Long id, String username);

    @Query("""
        SELECT MAX(tg.position)
        FROM TaskGroupEntity tg
        WHERE tg.taskBoard.id = :boardId
        AND tg.taskBoard.owner.username = :username
    """)
    Integer findMaxPositionByBoardIdAndUsername(Long boardId, String username); 
}
