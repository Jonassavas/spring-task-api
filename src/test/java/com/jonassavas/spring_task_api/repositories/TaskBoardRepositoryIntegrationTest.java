package com.jonassavas.spring_task_api.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.jonassavas.spring_task_api.domain.entities.TaskBoardEntity;
import com.jonassavas.spring_task_api.domain.entities.UserEntity;
import com.jonassavas.util.TestTaskBoardData;
import com.jonassavas.util.TestUserData;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class TaskBoardRepositoryIntegrationTest {

    @Autowired private TaskBoardRepository underTest;

    @Autowired private UserRepository userRepository;

    // Prerequisits for creating TaskBoardEntities
    private UserEntity user;

    @BeforeEach
    public void setUp() {
        user = userRepository.saveAndFlush(TestUserData.createTestUserEntityA());
    }

    @Test
    public void testThatTaskBoardCanBeCreatedAndRecalled() {
        TaskBoardEntity testTaskBoard = TestTaskBoardData.createTestTaskBoardEntityA(user);

        TaskBoardEntity saved = underTest.saveAndFlush(testTaskBoard);

        Optional<TaskBoardEntity> result = underTest.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getTaskBoardName()).isEqualTo(testTaskBoard.getTaskBoardName());
    }

    @Test
    public void testThatMultipleTaskBoardsCanBeCreatedAndRecalled() {
        TaskBoardEntity boardA =
                underTest.saveAndFlush(TestTaskBoardData.createTestTaskBoardEntityA(user));
        TaskBoardEntity boardB =
                underTest.saveAndFlush(TestTaskBoardData.createTestTaskBoardEntityB(user));
        TaskBoardEntity boardC =
                underTest.saveAndFlush(TestTaskBoardData.createTestTaskBoardEntityC(user));

        Iterable<TaskBoardEntity> result = underTest.findAll();

        assertThat(result)
                .hasSize(3)
                .extracting(TaskBoardEntity::getTaskBoardName)
                .containsExactly(
                        boardA.getTaskBoardName(),
                        boardB.getTaskBoardName(),
                        boardC.getTaskBoardName());
    }

    @Test
    public void testThatTaskBoardCanBeUpdated() {
        TaskBoardEntity board =
                underTest.saveAndFlush(TestTaskBoardData.createTestTaskBoardEntityA(user));

        board.setTaskBoardName("UPDATED");
        underTest.saveAndFlush(board);

        Optional<TaskBoardEntity> result = underTest.findById(board.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getTaskBoardName()).isEqualTo("UPDATED");
    }

    @Test
    public void testThatTaskBoardCanBeDeleted() {
        TaskBoardEntity board =
                underTest.saveAndFlush(TestTaskBoardData.createTestTaskBoardEntityA(user));

        underTest.deleteById(board.getId());

        Optional<TaskBoardEntity> result = underTest.findById(board.getId());

        assertThat(result).isEmpty();
    }

    // Custom repository methods -----------------------------------------

    // List<TaskBoardEntity> findByOwnerUsername(String username);
    @Test
    public void testFindByOwnerUsername() {
        TaskBoardEntity boardA =
                underTest.saveAndFlush(TestTaskBoardData.createTestTaskBoardEntityA(user));
        TaskBoardEntity boardB =
                underTest.saveAndFlush(TestTaskBoardData.createTestTaskBoardEntityB(user));

        var result = underTest.findByOwnerUsername(user.getUsername());

        assertThat(result)
                .hasSize(2)
                .extracting(TaskBoardEntity::getTaskBoardName)
                .containsExactly(boardA.getTaskBoardName(), boardB.getTaskBoardName());
    }

    @Test
    public void testFindByOwnerUsernameReturnsEmptyForUnknownUser() {
        underTest.saveAndFlush(TestTaskBoardData.createTestTaskBoardEntityA(user));

        var result = underTest.findByOwnerUsername("wrongUser");

        assertThat(result).isEmpty();
    }

    // Optional<TaskBoardEntity> findByIdAndOwnerUsername(Long id, String username);
    @Test
    public void testFindByIdAndOwnerUsername() {
        TaskBoardEntity board =
                underTest.saveAndFlush(TestTaskBoardData.createTestTaskBoardEntityA(user));

        var result = underTest.findByIdAndOwnerUsername(board.getId(), user.getUsername());

        assertThat(result).isPresent();
        assertThat(result.get().getTaskBoardName()).isEqualTo(board.getTaskBoardName());
    }

    @Test
    public void testFindByIdAndOwnerUsernameReturnsEmptyWhenWrongUser() {
        TaskBoardEntity board =
                underTest.saveAndFlush(TestTaskBoardData.createTestTaskBoardEntityA(user));

        var result = underTest.findByIdAndOwnerUsername(board.getId(), "wrongUser");

        assertThat(result).isEmpty();
    }
}
