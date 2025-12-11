package com.jonassavas.spring_task_api.repositories;

import org.springframework.boot.test.context.SpringBootTest;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import com.jonassavas.spring_task_api.TestDataUtil;
import com.jonassavas.spring_task_api.domain.entities.TaskEntity;
import com.jonassavas.spring_task_api.domain.entities.TaskGroupEntity;

import jakarta.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TaskGroupEntityRepositoryIntegrationTests {
    
    TaskGroupRepository underTest;

    @Autowired
    public TaskGroupEntityRepositoryIntegrationTests(TaskGroupRepository underTest){
        this.underTest = underTest;
    }

    @Test
    @Transactional
    public void testThatEmptyTaskGroupCanBeCreatedAndRecalled(){
        TaskGroupEntity testTaskGroup = TestDataUtil.createTaskGroupEntityA();
        underTest.save(testTaskGroup);
        Optional<TaskGroupEntity> result = underTest.findById(1L);
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testTaskGroup);
    }

    @Test
    @Transactional
    public void testThatMultipleEmptyTaskGroupsCanBeCreatedAndRecalled(){
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

    @Test
    @Transactional
    public void testThatTaskGroupWithTasksCanBeCreatedAndRecalled(){
        TaskGroupEntity testTaskGroup = TestDataUtil.createTaskGroupEntityA();
        TaskEntity testTaskEntityA = TestDataUtil.createTestTaskEntityA();
        testTaskGroup.addTask(testTaskEntityA);

        underTest.save(testTaskGroup);
        
        Optional<TaskGroupEntity> result = underTest.findById(1L);
        TaskGroupEntity savedGroup = result.get();
        assertThat(savedGroup.getTaskGroupName())
                            .isEqualTo(testTaskGroup.getTaskGroupName());
        assertThat(savedGroup.getTasks())
                            .hasSize(1);
        TaskEntity savedTask = savedGroup.getTasks().get(0);
        assertThat(savedTask.getTaskName())
                            .isEqualTo(testTaskEntityA.getTaskName());
    }

    @Test
    @Transactional
    public void testThatTaskGroupCanBeUpdated(){
        TaskGroupEntity testTaskGroupA = TestDataUtil.createTaskGroupEntityA();
        underTest.save(testTaskGroupA);
        testTaskGroupA.setTaskGroupName("UPDATED");
        underTest.save(testTaskGroupA);

        Optional<TaskGroupEntity> result = underTest.findById(testTaskGroupA.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testTaskGroupA);
    }

    @Test
    @Transactional
    public void testThatTaskGroupCanBeDeleted(){
        TaskGroupEntity testTaskGroupA = TestDataUtil.createTaskGroupEntityA();
        underTest.save(testTaskGroupA);

        underTest.deleteById(testTaskGroupA.getId());
        Optional<TaskGroupEntity> result = underTest.findById(testTaskGroupA.getId());
        assertThat(result).isEmpty();
    }
}
