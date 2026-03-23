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
public class TaskGroupEntityRepositoryIntegrationTests {
    
    TaskGroupRepository underTest;
    private TaskBoardRepository taskBoardRepository;
    
    private TaskBoardEntity taskBoard; 

    @Autowired
    public TaskGroupEntityRepositoryIntegrationTests(TaskGroupRepository underTest,
                                                    TaskBoardRepository taskBoardRepository){
        this.underTest = underTest;
        this.taskBoardRepository = taskBoardRepository;
    }

    @BeforeEach
    public void setUp(){
        taskBoard = taskBoardRepository.save(
            TestTaskBoardData.createTestTaskBoardEntityA()
        ); 
    }

    @Test
    @Transactional
    public void testThatEmptyTaskGroupCanBeCreatedAndRecalled(){
        TaskGroupEntity testTaskGroup = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        underTest.save(testTaskGroup);
        Optional<TaskGroupEntity> result = underTest.findById(testTaskGroup.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testTaskGroup);
    }

    @Test
    @Transactional
    public void testThatMultipleEmptyTaskGroupsCanBeCreatedAndRecalled(){
        TaskGroupEntity testTaskGroupA = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        underTest.save(testTaskGroupA);
        TaskGroupEntity testTaskGroupB = TestTaskGroupData.createTaskGroupEntityB(taskBoard);
        underTest.save(testTaskGroupB);
        TaskGroupEntity testTaskGroupC = TestTaskGroupData.createTaskGroupEntityB(taskBoard);
        underTest.save(testTaskGroupC);
        Iterable<TaskGroupEntity> result = underTest.findAll();
        assertThat(result)
                .hasSize(3)
                .containsExactly(testTaskGroupA, testTaskGroupB, testTaskGroupC);
    }

    @Test
    @Transactional
    public void testThatTaskGroupWithTasksCanBeCreatedAndRecalled(){
        TaskGroupEntity testTaskGroup = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        TaskEntity testTaskEntityA = TestTaskData.createTestTaskEntityA(testTaskGroup);
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
        TaskGroupEntity testTaskGroupA = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        underTest.save(testTaskGroupA);
        testTaskGroupA.setTaskGroupName("UPDATED");
        underTest.save(testTaskGroupA);

        Optional<TaskGroupEntity> result = underTest.findById(testTaskGroupA.getId());
        assertThat(result).isPresent();
        assertThat(result.get())
                .extracting(TaskGroupEntity::getTaskGroupName)
                .isEqualTo("UPDATED");
    }

    @Test
    @Transactional
    public void testThatTaskGroupCanBeDeleted(){
        TaskGroupEntity testTaskGroupA = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        underTest.save(testTaskGroupA);
        Optional<TaskGroupEntity> result = underTest.findById(testTaskGroupA.getId());
        assertThat(result.get()).isEqualTo(testTaskGroupA);

        underTest.deleteById(testTaskGroupA.getId());
        result = underTest.findById(testTaskGroupA.getId());
        assertThat(result).isEmpty();
    }
}
