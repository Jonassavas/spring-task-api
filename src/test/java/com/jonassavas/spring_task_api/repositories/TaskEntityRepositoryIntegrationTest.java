package com.jonassavas.spring_task_api.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import com.jonassavas.spring_task_api.domain.entities.TaskEntity;
import com.jonassavas.spring_task_api.domain.entities.TaskGroupEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TaskEntityRepositoryIntegrationTest {
    
    private TaskRepository underTest;

    @Autowired
    public TaskEntityRepositoryIntegrationTest(TaskRepository underTest){
        this.underTest = underTest;
    }

    @Test
    public void testThatTaskCanBeCreatedAndRecalled(){
        TaskEntity task = TaskEntity.builder()
                                    .taskName("Homework")
                                    .taskGroup(null)
                                    .build();
        underTest.save(task);
        Optional<TaskEntity> result = underTest.findById(1L);
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(task);
    }
}
