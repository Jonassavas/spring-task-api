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
public class TaskGroupEntityRepositoryIntegrationTests {
    
    @Autowired TaskGroupRepository underTest;
    @Autowired private TaskBoardRepository taskBoardRepository;
    @Autowired private UserRepository userRepository;
    
    // TaskBoardEntities require a user
    private UserEntity user;
    private TaskBoardEntity taskBoard; 

    @BeforeEach
    public void setUp(){
        user = userRepository.saveAndFlush(
            TestUserData.createTestUserEntityA()
        );
       
        taskBoard = taskBoardRepository.saveAndFlush(
            TestTaskBoardData.createTestTaskBoardEntityA(user)
        ); 
    }

    @Test
    public void testThatEmptyTaskGroupCanBeCreatedAndRecalled() {
        TaskGroupEntity testTaskGroup =
                TestTaskGroupData.createTaskGroupEntityA(taskBoard);

        TaskGroupEntity saved = underTest.saveAndFlush(testTaskGroup);

        Optional<TaskGroupEntity> result =
                underTest.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(saved);
    } 

    @Test
    public void testThatMultipleEmptyTaskGroupsCanBeCreatedAndRecalled(){
        TaskGroupEntity groupA =
                underTest.saveAndFlush(
                    TestTaskGroupData.createTaskGroupEntityA(taskBoard)
                );

        TaskGroupEntity groupB =
                underTest.saveAndFlush(
                    TestTaskGroupData.createTaskGroupEntityB(taskBoard)
                );

        TaskGroupEntity groupC =
                underTest.saveAndFlush(
                    TestTaskGroupData.createTaskGroupEntityB(taskBoard)
                );

        Iterable<TaskGroupEntity> result = underTest.findAll();

        assertThat(result)
                .hasSize(3)
                .contains(groupA, groupB, groupC);
    } 

    @Test
    public void testThatTaskGroupWithTasksCanBeCreatedAndRecalled() {
        TaskGroupEntity testTaskGroup =
                TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        TaskEntity testTaskEntityA =
                TestTaskData.createTestTaskEntityA(testTaskGroup);
        testTaskGroup.addTask(testTaskEntityA);
        TaskGroupEntity saved = underTest.saveAndFlush(testTaskGroup);

        Optional<TaskGroupEntity> result =
                underTest.findById(saved.getId());

        assertThat(result).isPresent();

        TaskGroupEntity savedGroup = result.get();

        assertThat(savedGroup.getTaskGroupName())
                .isEqualTo(testTaskGroup.getTaskGroupName());
        assertThat(savedGroup.getTasks())
                .hasSize(1);
    }

    @Test
    public void testThatTaskGroupCanBeUpdated(){
        TaskGroupEntity group =
                underTest.saveAndFlush(
                    TestTaskGroupData.createTaskGroupEntityA(taskBoard)
                );

        group.setTaskGroupName("UPDATED");
        underTest.saveAndFlush(group);

        Optional<TaskGroupEntity> result =
                underTest.findById(group.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getTaskGroupName())
                .isEqualTo("UPDATED");
    }

    @Test
    public void testThatTaskGroupCanBeDeleted(){
        TaskGroupEntity group =
                underTest.saveAndFlush(
                    TestTaskGroupData.createTaskGroupEntityA(taskBoard)
                );

        Optional<TaskGroupEntity> result =
                underTest.findById(group.getId());

        assertThat(result).isPresent();

        underTest.deleteById(group.getId());
        underTest.flush();

        result = underTest.findById(group.getId());

        assertThat(result).isEmpty();
    }

    // Custom repository methods -----------------------------------------

    // List<TaskGroupEntity> findByTaskBoardIdAndTaskBoardOwnerUsername(Long boardId, String username);
    @Test
    public void testFindByTaskBoardIdAndTaskBoardOwnerUsername() {
        TaskGroupEntity groupA =
                underTest.saveAndFlush(
                    TestTaskGroupData.createTaskGroupEntityA(taskBoard)
                );

        TaskGroupEntity groupB =
                underTest.saveAndFlush(
                    TestTaskGroupData.createTaskGroupEntityB(taskBoard)
                );

        var result = underTest.findByTaskBoardIdAndTaskBoardOwnerUsername(
                taskBoard.getId(),
                user.getUsername()
        );

        assertThat(result)
                .hasSize(2)
                .containsExactly(groupA, groupB);
    }

    // Optional<TaskGroupEntity> findByIdAndTaskBoardOwnerUsername(Long id, String username);
    @Test
    public void testFindByIdAndTaskBoardOwnerUsername() {
        TaskGroupEntity group =
                underTest.saveAndFlush(
                    TestTaskGroupData.createTaskGroupEntityA(taskBoard)
                );

        var result = underTest.findByIdAndTaskBoardOwnerUsername(
                group.getId(),
                user.getUsername()
        );

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(group);
    }

    @Test
    public void testFindByIdAndTaskBoardOwnerUsernameReturnsEmptyWhenWrongUser() {
        TaskGroupEntity group = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        underTest.save(group);

        var result = underTest.findByIdAndTaskBoardOwnerUsername(
                group.getId(),
                "wrongUser"
        );

        assertThat(result).isEmpty();
    }

    // @Query("""
    //     SELECT tg FROM TaskGroupEntity tg
    //     LEFT JOIN FETCH tg.tasks
    //     WHERE tg.taskBoard.owner.username = :username
    // """) 
    // List<TaskGroupEntity> findAllWithTasksByUsername(String username);
    @Test
    public void testFindAllWithTasksByUsername() {
        TaskGroupEntity group = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        TaskEntity task = TestTaskData.createTestTaskEntityA(group);
        group.addTask(task);

        underTest.saveAndFlush(group);

        var result = underTest.findAllWithTasksByUsername(user.getUsername());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTasks()).hasSize(1);
        assertThat(result.get(0).getTasks().get(0).getTaskName())
                .isEqualTo(task.getTaskName());
    }


    // @Query("""
    //     SELECT tg FROM TaskGroupEntity tg
    //     LEFT JOIN FETCH tg.tasks
    //     WHERE tg.id = :id AND tg.taskBoard.owner.username = :username
    // """)
    // Optional<TaskGroupEntity> findByIdWithTasksAndUsername(Long id, String username);
    @Test
    public void testFindByIdWithTasksAndUsername() {
        TaskGroupEntity group = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        TaskEntity task = TestTaskData.createTestTaskEntityA(group);
        group.addTask(task);

        underTest.saveAndFlush(group);

        var result = underTest.findByIdWithTasksAndUsername(
                group.getId(),
                user.getUsername()
        );

        assertThat(result).isPresent();
        assertThat(result.get().getTasks()).hasSize(1);
        assertThat(result.get().getTasks().get(0).getTaskName())
                .isEqualTo(task.getTaskName());
    }

}
