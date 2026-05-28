package com.jonassavas.spring_task_api.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.jonassavas.spring_task_api.domain.entities.TaskBoardEntity;
import com.jonassavas.spring_task_api.domain.entities.TaskEntity;
import com.jonassavas.spring_task_api.domain.entities.TaskGroupEntity;
import com.jonassavas.spring_task_api.domain.entities.UserEntity;
import com.jonassavas.util.TestTaskBoardData;
import com.jonassavas.util.TestTaskData;
import com.jonassavas.util.TestTaskGroupData;
import com.jonassavas.util.TestUserData;

@DataJpaTest // Runs each test in a transaction and rolls it back.
public class TaskRepositoryIntegrationTest {

    @Autowired private TaskRepository underTest;

    @Autowired private UserRepository userRepository;
    @Autowired private TaskBoardRepository taskBoardRepository;
    @Autowired private TaskGroupRepository taskGroupRepository;

    // Prerequisits for creating TaskEntities
    private UserEntity user;
    private TaskBoardEntity taskBoard;
    private TaskGroupEntity taskGroup;

    @BeforeEach
    public void setUp() {
        user = userRepository.saveAndFlush(TestUserData.createTestUserEntityA());

        taskBoard =
                taskBoardRepository.saveAndFlush(
                        TestTaskBoardData.createTestTaskBoardEntityA(user));

        taskGroup =
                taskGroupRepository.saveAndFlush(
                        TestTaskGroupData.createTaskGroupEntityA(taskBoard));
    }

    @Test
    public void testThatTaskCanBeCreatedAndRecalled() {
        TaskEntity testTask = TestTaskData.createTestTaskEntityA(taskGroup);

        TaskEntity saved = underTest.saveAndFlush(testTask);

        Optional<TaskEntity> result = underTest.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getTaskName()).isEqualTo(testTask.getTaskName());
    }

    @Test
    public void testThatMultipleTasksCanBeCreatedAndRecalled() {
        TaskEntity taskA = underTest.saveAndFlush(TestTaskData.createTestTaskEntityA(taskGroup));
        TaskEntity taskB = underTest.saveAndFlush(TestTaskData.createTestTaskEntityB(taskGroup));
        TaskEntity taskC = underTest.saveAndFlush(TestTaskData.createTestTaskEntityC(taskGroup));

        Iterable<TaskEntity> result = underTest.findAll();

        assertThat(result)
                .hasSize(3)
                .extracting(TaskEntity::getTaskName)
                .containsExactly(taskA.getTaskName(), taskB.getTaskName(), taskC.getTaskName());
    }

    @Test
    public void testThatTaskCanBeUpdated() {
        TaskEntity task = underTest.saveAndFlush(TestTaskData.createTestTaskEntityA(taskGroup));

        task.setTaskName("UPDATED");
        underTest.saveAndFlush(task);

        Optional<TaskEntity> result = underTest.findById(task.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getTaskName()).isEqualTo("UPDATED");
    }

    @Test
    public void testThatTaskCanBeDeleted() {
        TaskEntity task = underTest.saveAndFlush(TestTaskData.createTestTaskEntityA(taskGroup));

        underTest.deleteById(task.getId());

        Optional<TaskEntity> result = underTest.findById(task.getId());

        assertThat(result).isEmpty();
    }

    // Custom repository methods -----------------------------------------

    // Optional<TaskEntity> findByIdAndTaskGroupTaskBoardOwnerUsername(Long id, String username);
    @Test
    public void testFindByIdAndTaskGroupTaskBoardOwnerUsername() {
        TaskEntity task = underTest.saveAndFlush(TestTaskData.createTestTaskEntityA(taskGroup));

        Optional<TaskEntity> result =
                underTest.findByIdAndTaskGroupTaskBoardOwnerUsername(
                        task.getId(), user.getUsername());

        assertThat(result).isPresent();
        assertThat(result.get().getTaskName()).isEqualTo(task.getTaskName());
    }

    @Test
    public void testFindByIdAndTaskGroupTaskBoardOwnerUsernameReturnsEmptyWhenWrongUser() {
        TaskEntity task = underTest.saveAndFlush(TestTaskData.createTestTaskEntityA(taskGroup));

        Optional<TaskEntity> result =
                underTest.findByIdAndTaskGroupTaskBoardOwnerUsername(task.getId(), "wrongUser");

        assertThat(result).isEmpty();
    }

    // List<TaskEntity> findByTaskGroupIdAndTaskGroupTaskBoardOwnerUsername(Long groupId, String
    // username);
    @Test
    public void testFindByTaskGroupIdAndTaskGroupTaskBoardOwnerUsername() {
        TaskEntity taskA = underTest.saveAndFlush(TestTaskData.createTestTaskEntityA(taskGroup));
        TaskEntity taskB = underTest.saveAndFlush(TestTaskData.createTestTaskEntityB(taskGroup));

        var result =
                underTest.findByTaskGroupIdAndTaskGroupTaskBoardOwnerUsernameOrderByPositionAsc(
                        taskGroup.getId(), user.getUsername());

        assertThat(result)
                .hasSize(2)
                .extracting(TaskEntity::getTaskName)
                .containsExactly(taskA.getTaskName(), taskB.getTaskName());
    }

    @Test
    public void testFindByTaskGroupIdAndTaskGroupTaskBoardOwnerUsernameReturnsEmptyWhenWrongUser() {
        underTest.saveAndFlush(TestTaskData.createTestTaskEntityA(taskGroup));

        var result =
                underTest.findByTaskGroupIdAndTaskGroupTaskBoardOwnerUsernameOrderByPositionAsc(
                        taskGroup.getId(), "wrongUser");

        assertThat(result).isEmpty();
    }
}
