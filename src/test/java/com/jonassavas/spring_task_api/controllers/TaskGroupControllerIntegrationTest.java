package com.jonassavas.spring_task_api.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.jonassavas.spring_task_api.domain.dto.task_group.CreateTaskGroupRequestDto;
import com.jonassavas.spring_task_api.domain.dto.task_group.UpdateTaskGroupRequestDto;
import com.jonassavas.spring_task_api.domain.entities.TaskBoardEntity;
import com.jonassavas.spring_task_api.domain.entities.TaskEntity;
import com.jonassavas.spring_task_api.domain.entities.TaskGroupEntity;
import com.jonassavas.spring_task_api.repositories.TaskBoardRepository;
import com.jonassavas.spring_task_api.repositories.TaskGroupRepository;
import com.jonassavas.spring_task_api.repositories.TaskRepository;
import com.jonassavas.util.TestTaskBoardData;
import com.jonassavas.util.TestTaskData;
import com.jonassavas.util.TestTaskGroupData;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskGroupControllerIntegrationTest extends BaseAuthenticatedIntegrationTest {

    // Repositories to save test data to the database:
    @Autowired private TaskRepository taskRepository;
    @Autowired private TaskGroupRepository taskGroupRepository;
    @Autowired private TaskBoardRepository taskBoardRepository;

    /* Prerequisit data:
        A TaskGroup needs a TaskBoard:
            - TaskBoard --> TaskGroup
    */
    private TaskBoardEntity taskBoard;

    @BeforeEach
    public void setUp() {
        // Clear previous test data in reverse FK order
        taskRepository.deleteAll();
        taskGroupRepository.deleteAll();
        taskBoardRepository.deleteAll();

        taskBoard =
                taskBoardRepository.saveAndFlush(
                        TestTaskBoardData.createTestTaskBoardEntityA(user));
    }

    // CREATE -----------------------------------------------------------
    @Test
    public void testThatCreateTaskGroupReturnsHttp201Create() throws Exception {
        CreateTaskGroupRequestDto testTaskGroupDtoA =
                TestTaskGroupData.createTaskGroupRequestDtoA(taskBoard);
        String taskGroupJson = objectMapper.writeValueAsString(testTaskGroupDtoA);

        mockMvc.perform(
                        authenticated(
                                        MockMvcRequestBuilders.post(
                                                "/taskboards/" + taskBoard.getId() + "/groups"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(taskGroupJson))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void testThatCreateTaskGroupReturnsSavedTaskGroup() throws Exception {
        TaskGroupEntity testTaskGroupEntityA = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        String taskGroupJson = objectMapper.writeValueAsString(testTaskGroupEntityA);

        mockMvc.perform(
                        authenticated(
                                        MockMvcRequestBuilders.post(
                                                "/taskboards/" + taskBoard.getId() + "/groups"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(taskGroupJson))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskGroupName").value("Task Group A"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskBoardId").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.position").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.color").value("#FF0000"));
    }

    // READ -----------------------------------------------------------

    @Test
    public void testThatListTaskGroupsReturnsHttpStatus200() throws Exception {
        mockMvc.perform(
                        authenticated(
                                        MockMvcRequestBuilders.get(
                                                "/taskboards/" + taskBoard.getId() + "/groups"))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testThatListTaskGroupsReturnsListOfTaskGroups() throws Exception {
        TaskGroupEntity testTaskGroupEntityA = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        taskGroupRepository.saveAndFlush(testTaskGroupEntityA);

        mockMvc.perform(
                        authenticated(
                                        MockMvcRequestBuilders.get(
                                                "/taskboards/" + taskBoard.getId() + "/groups"))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").isNumber())
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$[0].taskGroupName").value("Task Group A"));
    }

    @Test
    public void testThatListTasksByGroupReturnsCorrectTasks() throws Exception {
        TaskGroupEntity group =
                taskGroupRepository.save(TestTaskGroupData.createTaskGroupEntityA(taskBoard));
        TaskEntity task1 = taskRepository.saveAndFlush(TestTaskData.createTestTaskEntityA(group));
        TaskEntity task2 = taskRepository.saveAndFlush(TestTaskData.createTestTaskEntityB(group));

        mockMvc.perform(
                        authenticated(
                                        MockMvcRequestBuilders.get(
                                                "/groups/" + group.getId() + "/tasks"))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(task1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(task2.getId()))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$[0].taskName").value(task1.getTaskName()))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$[1].taskName").value(task2.getTaskName()));
    }

    @Test
    public void testThatListTaskGroupsReturnsListOfTaskGroupsOrderedByPosition() throws Exception {

        TaskGroupEntity groupA = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        TaskGroupEntity groupC = TestTaskGroupData.createTaskGroupEntityC(taskBoard);
        TaskGroupEntity groupB = TestTaskGroupData.createTaskGroupEntityB(taskBoard);

        taskGroupRepository.saveAndFlush(groupA);
        taskGroupRepository.saveAndFlush(groupB);
        taskGroupRepository.saveAndFlush(groupC);

        mockMvc.perform(
                        authenticated(
                                        MockMvcRequestBuilders.get(
                                                "/taskboards/" + taskBoard.getId() + "/groups"))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())

                
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(groupA.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(groupB.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].id").value(groupC.getId()))

                .andExpect(MockMvcResultMatchers.jsonPath("$[0].position").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].position").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].position").value(2))

                .andExpect(MockMvcResultMatchers.jsonPath("$[0].color").value("#FF0000"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].color").value("#00FF00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].color").value("#0000FF"));
        }

    // DELETE -----------------------------------------------------------

    @Test
    public void testThatDeleteTaskGroupReturnsHttp204() throws Exception {
        TaskGroupEntity testTaskGroupEntityA = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        taskGroupRepository.saveAndFlush(testTaskGroupEntityA);

        assertThat(taskGroupRepository.findAll().size()).isEqualTo(1);

        mockMvc.perform(
                        authenticated(
                                        MockMvcRequestBuilders.delete(
                                                "/taskboards/"
                                                        + taskBoard.getId()
                                                        + "/groups/"
                                                        + testTaskGroupEntityA.getId()))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        assertThat(taskGroupRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    public void testThatDeleteTaskGroupDeletesCorrectGroup() throws Exception {
        TaskGroupEntity testTaskGroupEntityA = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        taskGroupRepository.saveAndFlush(testTaskGroupEntityA);
        TaskGroupEntity testTaskGroupEntityB = TestTaskGroupData.createTaskGroupEntityB(taskBoard);
        taskGroupRepository.saveAndFlush(testTaskGroupEntityB);

        assertThat(taskGroupRepository.findAll().size()).isEqualTo(2);

        mockMvc.perform(
                        authenticated(
                                        MockMvcRequestBuilders.delete(
                                                "/taskboards/"
                                                        + taskBoard.getId()
                                                        + "/groups/"
                                                        + testTaskGroupEntityA.getId()))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        List<TaskGroupEntity> result = taskGroupRepository.findAll();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result)
                .extracting(TaskGroupEntity::getId)
                .containsExactly(testTaskGroupEntityB.getId());
    }

    @Test
    public void testThatDeletingTaskGroupCascadesDeletesTasks() throws Exception {
        // Create a TaskGroup and multiple Tasks under it
        TaskGroupEntity group =
                taskGroupRepository.saveAndFlush(
                        TestTaskGroupData.createTaskGroupEntityA(taskBoard));
        TaskEntity task1 = taskRepository.saveAndFlush(TestTaskData.createTestTaskEntityA(group));
        TaskEntity task2 = taskRepository.saveAndFlush(TestTaskData.createTestTaskEntityB(group));

        // Verify tasks exist
        assertThat(taskRepository.findAll())
                .extracting(TaskEntity::getId)
                .containsExactlyInAnyOrder(task1.getId(), task2.getId());

        // Delete the TaskGroup (simulate cascade from TaskGroupController)
        taskGroupRepository.deleteById(group.getId());

        // Tasks should be removed
        List<TaskEntity> remainingTasks = taskRepository.findAll();
        assertThat(remainingTasks).isEmpty();
    }

    @Test
    public void testThatDeleteTaskGroupDeletesTasks() throws Exception {
        TaskGroupEntity testTaskGroupEntityA = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        taskGroupRepository.saveAndFlush(testTaskGroupEntityA);

        TaskEntity testTaskEntityA = TestTaskData.createTestTaskEntityA(testTaskGroupEntityA);
        taskRepository.saveAndFlush(testTaskEntityA);
        TaskEntity testTaskEntityB = TestTaskData.createTestTaskEntityB(testTaskGroupEntityA);
        taskRepository.saveAndFlush(testTaskEntityB);
        TaskEntity testTaskEntityC = TestTaskData.createTestTaskEntityC(testTaskGroupEntityA);
        taskRepository.saveAndFlush(testTaskEntityC);

        List<TaskGroupEntity> result =
                taskGroupRepository.findAllWithTasksByUsername(user.getUsername());
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.getFirst().getTasks())
                .extracting(TaskEntity::getId)
                .containsExactly(
                        testTaskEntityA.getId(), testTaskEntityB.getId(), testTaskEntityC.getId());
        assertThat(taskRepository.findAll())
                .extracting(TaskEntity::getId)
                .containsExactly(
                        testTaskEntityA.getId(), testTaskEntityB.getId(), testTaskEntityC.getId());

        mockMvc.perform(
                        authenticated(
                                        MockMvcRequestBuilders.delete(
                                                "/taskboards/"
                                                        + taskBoard.getId()
                                                        + "/groups/"
                                                        + testTaskGroupEntityA.getId()))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        assertThat(taskGroupRepository.findAllWithTasksByUsername(user.getUsername()).size())
                .isEqualTo(0);
        assertThat(taskRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    public void testThatDeleteTaskGroupOnlyDeletesOwnTasks() throws Exception {
        TaskGroupEntity testTaskGroupEntityA = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        taskGroupRepository.saveAndFlush(testTaskGroupEntityA);
        TaskEntity testTaskEntityA = TestTaskData.createTestTaskEntityA(testTaskGroupEntityA);
        taskRepository.saveAndFlush(testTaskEntityA);

        TaskGroupEntity testTaskGroupEntityB = TestTaskGroupData.createTaskGroupEntityB(taskBoard);
        taskGroupRepository.saveAndFlush(testTaskGroupEntityB);
        TaskEntity testTaskEntityB = TestTaskData.createTestTaskEntityB(testTaskGroupEntityB);
        taskRepository.saveAndFlush(testTaskEntityB);
        TaskEntity testTaskEntityC = TestTaskData.createTestTaskEntityC(testTaskGroupEntityB);
        taskRepository.saveAndFlush(testTaskEntityC);

        List<TaskGroupEntity> result =
                taskGroupRepository.findAllWithTasksByUsername(user.getUsername());
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getTasks())
                .extracting(TaskEntity::getId)
                .containsExactly(testTaskEntityA.getId());
        assertThat(result.get(1).getTasks())
                .extracting(TaskEntity::getId)
                .containsExactly(testTaskEntityB.getId(), testTaskEntityC.getId());
        assertThat(taskRepository.findAll())
                .extracting(TaskEntity::getId)
                .containsExactly(
                        testTaskEntityA.getId(), testTaskEntityB.getId(), testTaskEntityC.getId());

        mockMvc.perform(
                        authenticated(
                                        MockMvcRequestBuilders.delete(
                                                "/taskboards/"
                                                        + taskBoard.getId()
                                                        + "/groups/"
                                                        + testTaskGroupEntityA.getId()))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        result = taskGroupRepository.findAllWithTasksByUsername(user.getUsername());
        assertThat(result.size()).isEqualTo(1);
        assertThat(result)
                .extracting(TaskGroupEntity::getId)
                .containsExactly(testTaskGroupEntityB.getId());
        assertThat(result.get(0).getTasks())
                .extracting(TaskEntity::getId)
                .containsExactly(testTaskEntityB.getId(), testTaskEntityC.getId());
    }

    // UPDATE -----------------------------------------------------------

    @Test
    public void testThatUpdateTaskGroupReturnsHttp200() throws Exception {
        TaskGroupEntity testTaskGroupEntityA = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        taskGroupRepository.saveAndFlush(testTaskGroupEntityA);

        CreateTaskGroupRequestDto testTaskGroupDtoA =
                TestTaskGroupData.createTaskGroupDtoA(taskBoard);
        testTaskGroupDtoA.setTaskGroupName("UPDATED");
        String taskGroupJson = objectMapper.writeValueAsString(testTaskGroupDtoA);

        mockMvc.perform(
                        authenticated(
                                        MockMvcRequestBuilders.patch(
                                                "/taskboards/"
                                                        + taskBoard.getId()
                                                        + "/groups/"
                                                        + testTaskGroupEntityA.getId()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(taskGroupJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskGroupName").value("UPDATED"));
    }

    @Test
    public void testThatUpdateTaskGroupKeepsItsTasks() throws Exception {
        TaskGroupEntity testTaskGroupEntityA = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        taskGroupRepository.saveAndFlush(testTaskGroupEntityA);

        TaskEntity testTaskEntityA = TestTaskData.createTestTaskEntityA(testTaskGroupEntityA);
        taskRepository.saveAndFlush(testTaskEntityA);

        assertThat(
                        taskGroupRepository
                                .findByIdWithTasksAndUsername(
                                        testTaskGroupEntityA.getId(), user.getUsername())
                                .get()
                                .getTasks())
                .extracting(TaskEntity::getId)
                .containsExactly(testTaskEntityA.getId());

        UpdateTaskGroupRequestDto testTaskGroupDtoA =
                TestTaskGroupData.updateTaskGroupDtoA(taskBoard);
        testTaskGroupDtoA.setTaskGroupName("UPDATED");
        String taskGroupJson = objectMapper.writeValueAsString(testTaskGroupDtoA);

        mockMvc.perform(
                        authenticated(
                                        MockMvcRequestBuilders.patch(
                                                "/taskboards/"
                                                        + taskBoard.getId()
                                                        + "/groups/"
                                                        + testTaskGroupEntityA.getId()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(taskGroupJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskGroupName").value("UPDATED"));

        assertThat(
                        taskGroupRepository
                                .findByIdWithTasksAndUsername(
                                        testTaskGroupEntityA.getId(), user.getUsername())
                                .get()
                                .getTasks())
                .extracting(TaskEntity::getId)
                .containsExactly(testTaskEntityA.getId());
    }

    @Test
    public void testThatDeleteAllTasksDeletesCorrespondingTasks() throws Exception {
        TaskGroupEntity testTaskGroupEntityA = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        taskGroupRepository.saveAndFlush(testTaskGroupEntityA);
        TaskEntity testTaskEntityA = TestTaskData.createTestTaskEntityA(testTaskGroupEntityA);
        taskRepository.saveAndFlush(testTaskEntityA);
        TaskEntity testTaskEntityB = TestTaskData.createTestTaskEntityB(testTaskGroupEntityA);
        taskRepository.saveAndFlush(testTaskEntityB);

        TaskGroupEntity testTaskGroupEntityB = TestTaskGroupData.createTaskGroupEntityB(taskBoard);
        taskGroupRepository.saveAndFlush(testTaskGroupEntityB);
        TaskEntity testTaskEntityC = TestTaskData.createTestTaskEntityB(testTaskGroupEntityB);
        taskRepository.saveAndFlush(testTaskEntityC);

        mockMvc.perform(
                        authenticated(
                                        MockMvcRequestBuilders.delete(
                                                "/taskboards/"
                                                        + taskBoard.getId()
                                                        + "/groups/"
                                                        + testTaskGroupEntityA.getId()
                                                        + "/tasks"))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        List<TaskEntity> result = taskRepository.findAll();
        assertThat(result).extracting(TaskEntity::getId).containsExactly(testTaskEntityC.getId());
    }

    @Test
public void testThatReorderTaskGroupsUpdatesPositionsCorrectly() throws Exception {

    // given: 3 groups in initial order A, B, C
    TaskGroupEntity groupA =
            taskGroupRepository.saveAndFlush(
                    TestTaskGroupData.createTaskGroupEntityA(taskBoard));

    TaskGroupEntity groupB =
            taskGroupRepository.saveAndFlush(
                    TestTaskGroupData.createTaskGroupEntityB(taskBoard));

    TaskGroupEntity groupC =
            taskGroupRepository.saveAndFlush(
                    TestTaskGroupData.createTaskGroupEntityC(taskBoard));

    // sanity check initial state
    assertThat(groupA.getPosition()).isEqualTo(0);
    assertThat(groupB.getPosition()).isEqualTo(1);
    assertThat(groupC.getPosition()).isEqualTo(2);

    // when: reorder to C, A, B
    String reorderJson = objectMapper.writeValueAsString(
            Map.of("groupIds", List.of(groupC.getId(), groupA.getId(), groupB.getId()))
    );

    mockMvc.perform(
                    authenticated(
                                    MockMvcRequestBuilders.patch(
                                            "/taskboards/" + taskBoard.getId() + "/groups/reorder"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(reorderJson))
            .andExpect(MockMvcResultMatchers.status().isNoContent());

    // then: verify persisted order
    List<TaskGroupEntity> result =
            taskGroupRepository.findByTaskBoardIdAndTaskBoardOwnerUsernameOrderByPositionAsc(
                    taskBoard.getId(), user.getUsername());

    assertThat(result)
            .extracting(TaskGroupEntity::getId)
            .containsExactly(groupC.getId(), groupA.getId(), groupB.getId());

    assertThat(result)
            .extracting(TaskGroupEntity::getPosition)
            .containsExactly(0, 1, 2);
}
}
