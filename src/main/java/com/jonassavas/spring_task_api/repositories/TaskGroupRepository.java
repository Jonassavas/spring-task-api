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
public interface TaskGroupRepository extends JpaRepository<TaskGroupEntity, Long>{
    
    @Query("""
        SELECT tg FROM TaskGroupEntity tg
        LEFT JOIN FETCH tg.tasks            
    """) 
    List<TaskGroupEntity> findAllWithTasks();

    @Query("""
        SELECT tg from TaskGroupEntity tg
        LEFT JOIN FETCH tg.tasks
        WHERE tg.id = :id
    """)
    Optional<TaskGroupEntity> findByIdWithTasks(Long id);
}
