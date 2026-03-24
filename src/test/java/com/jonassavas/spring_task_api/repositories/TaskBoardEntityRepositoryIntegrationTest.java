package com.jonassavas.spring_task_api.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import com.jonassavas.spring_task_api.domain.entities.TaskBoardEntity;
import com.jonassavas.spring_task_api.domain.entities.UserEntity;
import com.jonassavas.util.TestTaskBoardData;
import com.jonassavas.util.TestUserData;

import jakarta.transaction.Transactional;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TaskBoardEntityRepositoryIntegrationTest {

    private TaskBoardRepository underTest;
    private UserRepository userRepository;

    private UserEntity user;

    @Autowired
    public TaskBoardEntityRepositoryIntegrationTest(TaskBoardRepository underTest,
                                                    UserRepository userRepository){
        this.underTest = underTest;
        this.userRepository = userRepository;
    }

    @BeforeEach
    public void setUp(){
        user = userRepository.save(
            TestUserData.createTestUserEntityA()
        );
    }

    @Test
    @Transactional
    public void testThatTaskBoardCanBeCreatedAndRecalled(){
        TaskBoardEntity testTaskBoardA = TestTaskBoardData.createTestTaskBoardEntityA(user);
        underTest.save(testTaskBoardA);
        Optional<TaskBoardEntity> result = underTest.findById(1L);
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testTaskBoardA);
    }

    @Test
    @Transactional
    public void testThatMultipleTaskBoardsCanBeCreatedAndRecalled(){
        TaskBoardEntity testTaskBoardA = TestTaskBoardData.createTestTaskBoardEntityA(user);
        underTest.save(testTaskBoardA);
        TaskBoardEntity testTaskBoardB = TestTaskBoardData.createTestTaskBoardEntityB(user);
        underTest.save(testTaskBoardB);
        TaskBoardEntity testTaskBoardC = TestTaskBoardData.createTestTaskBoardEntityC(user);
        underTest.save(testTaskBoardC);

        Iterable<TaskBoardEntity> result = underTest.findAll();
        assertThat(result)
                .hasSize(3)
                .containsExactly(testTaskBoardA, testTaskBoardB, testTaskBoardC);
    }

    @Test
    @Transactional
    public void testThatTaskBoardCanBeUpdated(){
        TaskBoardEntity testTaskBoardA = TestTaskBoardData.createTestTaskBoardEntityA(user);
        underTest.save(testTaskBoardA);
        Optional<TaskBoardEntity> result = underTest.findById(testTaskBoardA.getId());
        assertThat(result.get()).isEqualTo(testTaskBoardA);

        underTest.deleteById(testTaskBoardA.getId());
        result = underTest.findById(testTaskBoardA.getId());
        assertThat(result).isEmpty();
    }
}
