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
        user = userRepository.save(
            TestUserData.createTestUserEntityA()
        );
       
        taskBoard = taskBoardRepository.save(
            TestTaskBoardData.createTestTaskBoardEntityA(user)
        ); 
    }

    @Test
    public void testThatEmptyTaskGroupCanBeCreatedAndRecalled(){
        TaskGroupEntity testTaskGroup = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        underTest.save(testTaskGroup);
        Optional<TaskGroupEntity> result = underTest.findById(testTaskGroup.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testTaskGroup);
    }

    @Test
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
    public void testThatTaskGroupWithTasksCanBeCreatedAndRecalled() {
        TaskGroupEntity testTaskGroup =
                TestTaskGroupData.createTaskGroupEntityA(taskBoard);

        TaskEntity testTaskEntityA =
                TestTaskData.createTestTaskEntityA(testTaskGroup);

        testTaskGroup.addTask(testTaskEntityA);
        underTest.save(testTaskGroup);

        Optional<TaskGroupEntity> result =
                underTest.findById(testTaskGroup.getId());

        assertThat(result).isPresent();

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
    public void testThatTaskGroupCanBeDeleted(){
        TaskGroupEntity testTaskGroupA = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        underTest.save(testTaskGroupA);
        Optional<TaskGroupEntity> result = underTest.findById(testTaskGroupA.getId());
        assertThat(result.get()).isEqualTo(testTaskGroupA);

        underTest.deleteById(testTaskGroupA.getId());
        result = underTest.findById(testTaskGroupA.getId());
        assertThat(result).isEmpty();
    }

    // Custom repository methods -----------------------------------------

    // List<TaskGroupEntity> findByTaskBoardIdAndTaskBoardOwnerUsername(Long boardId, String username);
    @Test
    public void testFindByTaskBoardIdAndTaskBoardOwnerUsername() {
        TaskGroupEntity groupA = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        TaskGroupEntity groupB = TestTaskGroupData.createTaskGroupEntityB(taskBoard);

        underTest.save(groupA);
        underTest.save(groupB);

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
        TaskGroupEntity group = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        underTest.save(group);

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

        underTest.save(group);

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

        underTest.save(group);

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
