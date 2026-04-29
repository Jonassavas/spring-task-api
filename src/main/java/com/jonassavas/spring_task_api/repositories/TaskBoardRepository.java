package com.jonassavas.spring_task_api.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jonassavas.spring_task_api.domain.entities.TaskBoardEntity;

@Repository
public interface TaskBoardRepository extends JpaRepository<TaskBoardEntity, Long> {
    List<TaskBoardEntity> findByOwnerUsername(String username);

    Optional<TaskBoardEntity> findByIdAndOwnerUsername(Long id, String username);

    @Query("""
        SELECT DISTINCT b FROM TaskBoardEntity b
        LEFT JOIN FETCH b.taskGroups g
        LEFT JOIN FETCH g.tasks
        WHERE b.id = :boardId AND b.owner.username = :username
    """)
    Optional<TaskBoardEntity> findByIdAndOwnerUsernameWithGroupsAndTasks(
            Long boardId,
            String username
    );
}
