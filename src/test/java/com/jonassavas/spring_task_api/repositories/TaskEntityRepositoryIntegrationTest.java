package com.jonassavas.spring_task_api.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import com.jonassavas.spring_task_api.domain.entities.TaskBoardEntity;
import com.jonassavas.spring_task_api.domain.entities.TaskEntity;
import com.jonassavas.spring_task_api.domain.entities.TaskGroupEntity;
import com.jonassavas.util.TestTaskBoardData;
import com.jonassavas.util.TestTaskData;
import com.jonassavas.util.TestTaskGroupData;

import jakarta.transaction.Transactional;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TaskEntityRepositoryIntegrationTest {
    
    private TaskRepository underTest;

    private TaskBoardRepository taskBoardRepository;
    private TaskGroupRepository taskGroupRepository;

    private TaskBoardEntity taskBoard; 
    private TaskGroupEntity taskGroup; 

    @BeforeEach
    public void setUp(){
        taskBoard = taskBoardRepository.save(
            TestTaskBoardData.createTestTaskBoardEntityA()
        );
        taskGroup = taskGroupRepository.save(
            TestTaskGroupData.createTaskGroupEntityA(taskBoard)
        );
    }

    @Autowired
    public TaskEntityRepositoryIntegrationTest(TaskRepository underTest, 
                                            TaskBoardRepository taskBoardRepository, 
                                            TaskGroupRepository taskGroupRepository){
        this.underTest = underTest;
        this.taskBoardRepository = taskBoardRepository;
        this.taskGroupRepository = taskGroupRepository;
    }

    @Test
    @Transactional
    public void testThatTaskCanBeCreatedAndRecalled(){
        TaskEntity testTaskA = TestTaskData.createTestTaskEntityA(taskGroup);
        underTest.save(testTaskA);
        Optional<TaskEntity> result = underTest.findById(1L);
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testTaskA);
    }

    @Test
    @Transactional
    public void testThatMultipleTasksCanBeCreatedAndRecalled(){
        TaskEntity testTaskA = TestTaskData.createTestTaskEntityA(taskGroup);
        underTest.save(testTaskA);
        TaskEntity testTaskB = TestTaskData.createTestTaskEntityB(taskGroup);
        underTest.save(testTaskB);
        TaskEntity testTaskC = TestTaskData.createTestTaskEntityC(taskGroup);
        underTest.save(testTaskC);

        Iterable<TaskEntity> result = underTest.findAll();
        assertThat(result)
                .hasSize(3)
                .containsExactly(testTaskA, testTaskB, testTaskC);
    }

    @Test
    @Transactional
    public void testThatTaskCanBeUpdated(){
        TaskEntity taskEntityA = TestTaskData.createTestTaskEntityA(taskGroup);
        underTest.save(taskEntityA);
        taskEntityA.setTaskName("UPDATED");
        underTest.save(taskEntityA);
        
        Optional<TaskEntity> result = underTest.findById(taskEntityA.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(taskEntityA);
    }

    @Test
    @Transactional
    public void testThatTaskCanBeDeleted(){
        TaskEntity taskEntityA = TestTaskData.createTestTaskEntityA(taskGroup);
        underTest.save(taskEntityA);
        Optional<TaskEntity> result = underTest.findById(taskEntityA.getId());
        assertThat(result.get()).isEqualTo(taskEntityA);

        underTest.deleteById(taskEntityA.getId());
        result = underTest.findById(taskEntityA.getId());
        assertThat(result).isEmpty();
    }

}
