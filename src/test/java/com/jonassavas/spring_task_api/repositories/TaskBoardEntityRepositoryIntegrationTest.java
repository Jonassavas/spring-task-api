package com.jonassavas.spring_task_api.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import com.jonassavas.spring_task_api.domain.entities.TaskBoardEntity;
import com.jonassavas.util.TestTaskBoardData;

import jakarta.transaction.Transactional;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TaskBoardEntityRepositoryIntegrationTest {

    //TODO
    private TaskBoardRepository underTest;

    @Autowired
    public TaskBoardEntityRepositoryIntegrationTest(TaskBoardRepository underTest){
        this.underTest = underTest;
    }

    @Test
    @Transactional
    public void testThatTaskBoardCanBeCreatedAndRecalled(){
        TaskBoardEntity testTaskBoardA = TestTaskBoardData.createTestTaskBoardEntityA();
        underTest.save(testTaskBoardA);
        Optional<TaskBoardEntity> result = underTest.findById(1L);
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testTaskBoardA);
    }

    @Test
    @Transactional
    public void testThatMultipleTaskBoardsCanBeCreatedAndRecalled(){
        TaskBoardEntity testTaskBoardA = TestTaskBoardData.createTestTaskBoardEntityA();
        underTest.save(testTaskBoardA);
        TaskBoardEntity testTaskBoardB = TestTaskBoardData.createTestTaskBoardEntityB();
        underTest.save(testTaskBoardB);
        TaskBoardEntity testTaskBoardC = TestTaskBoardData.createTestTaskBoardEntityC();
        underTest.save(testTaskBoardC);

        Iterable<TaskBoardEntity> result = underTest.findAll();
        assertThat(result)
                .hasSize(3)
                .containsExactly(testTaskBoardA, testTaskBoardB, testTaskBoardC);
    }

    @Test
    @Transactional
    public void testThatTaskBoardCanBeUpdated(){
        TaskBoardEntity testTaskBoardA = TestTaskBoardData.createTestTaskBoardEntityA();
        underTest.save(testTaskBoardA);
        Optional<TaskBoardEntity> result = underTest.findById(testTaskBoardA.getId());
        assertThat(result.get()).isEqualTo(testTaskBoardA);

        underTest.deleteById(testTaskBoardA.getId());
        result = underTest.findById(testTaskBoardA.getId());
        assertThat(result).isEmpty();
    }
}
