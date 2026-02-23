package com.jonassavas.spring_task_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jonassavas.spring_task_api.domain.entities.TaskBoardEntity;


@Repository
public interface TaskBoardRepository extends JpaRepository<TaskBoardEntity, Long> {
    
}
