package com.jonassavas.spring_task_api.repositories;

import org.springframework.boot.test.context.SpringBootTest;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import com.jonassavas.spring_task_api.TestDataUtil;
import com.jonassavas.spring_task_api.domain.entities.TaskEntity;
import com.jonassavas.spring_task_api.domain.entities.TaskGroupEntity;

import jakarta.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TaskGroupEntityRepositoryIntegrationTests {
    
    TaskGroupRepository underTest;

    @Autowired
    public TaskGroupEntityRepositoryIntegrationTests(TaskGroupRepository underTest){
        this.underTest = underTest;
    }

    @Test
    @Transactional
    public void testThatTaskGroupCanBeCreatedAndRecalled(){
        TaskGroupEntity testTaskGroup = TestDataUtil.createTaskGroupEntityA();
        underTest.save(testTaskGroup);
        Optional<TaskGroupEntity> result = underTest.findById(1L);
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testTaskGroup);
    }

    @Test
    @Transactional
    public void testThatMultipleTaskGroupsCanBeCreatedAndRecalled(){
        TaskGroupEntity testTaskGroupA = TestDataUtil.createTaskGroupEntityA();
        underTest.save(testTaskGroupA);
        TaskGroupEntity testTaskGroupB = TestDataUtil.createTaskGroupEntityB();
        underTest.save(testTaskGroupB);
        TaskGroupEntity testTaskGroupC = TestDataUtil.createTaskGroupEntityB();
        underTest.save(testTaskGroupC);
        Iterable<TaskGroupEntity> result = underTest.findAll();
        assertThat(result)
                .hasSize(3)
                .containsExactly(testTaskGroupA, testTaskGroupB, testTaskGroupC);
    }

}
