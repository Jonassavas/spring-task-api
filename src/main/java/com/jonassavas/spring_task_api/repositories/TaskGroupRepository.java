package com.jonassavas.spring_task_api.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.jonassavas.spring_task_api.domain.entities.TaskGroupEntity;

/*
@Repository Annotation is a specialization of the @Component annotation, 
which is used to indicate that the class provides the mechanism for storage, 
retrieval, update, delete, and search operation on objects.*/
@Repository 
public interface TaskGroupRepository extends CrudRepository<TaskGroupEntity, Long>{
    
}
