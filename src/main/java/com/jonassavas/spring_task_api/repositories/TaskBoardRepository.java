package com.jonassavas.spring_task_api.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jonassavas.spring_task_api.domain.entities.TaskBoardEntity;

@Repository
public interface TaskBoardRepository extends JpaRepository<TaskBoardEntity, Long> {
    List<TaskBoardEntity> findByOwnerUsername(String username);

    Optional<TaskBoardEntity> findByIdAndOwnerUsername(Long id, String username);

    @EntityGraph(attributePaths = {"taskGroups", "taskGroups.tasks"})
    @Query("SELECT b FROM TaskBoardEntity b WHERE b.id = :boardId AND b.owner.username = :username")
    Optional<TaskBoardEntity> findWithDetailsByIdAndOwnerUsername(
            @Param("boardId") Long boardId,
            @Param("username") String username
    );
}
