package com.jonassavas.spring_task_api.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester.MockMvcRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jonassavas.spring_task_api.domain.dto.task_board.TaskBoardRequestDto;
import com.jonassavas.spring_task_api.domain.entities.TaskBoardEntity;
import com.jonassavas.spring_task_api.repositories.TaskBoardRepository;
import com.jonassavas.spring_task_api.services.TaskBoardService;
import com.jonassavas.util.TestTaskBoardData;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class TaskBoardControllerIntegrationTests {

    private TaskBoardService taskBoardService;
    
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;
    private TaskBoardRepository taskBoardRepository;

    // Add user here?

    @Autowired
    public TaskBoardControllerIntegrationTests(TaskBoardService taskBoardService,
                                                MockMvc mockMvc,
                                                ObjectMapper objectMapper,
                                                TaskBoardRepository taskBoardRepository){
        this.taskBoardService = taskBoardService;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.taskBoardRepository = taskBoardRepository;
    }

    // CREATE -----------------------------------------------------------

    @Test
    public void testThatCreateTaskReturnsHttp201Create() throws Exception{
       TaskBoardRequestDto testTaskBoardDtoA = TestTaskBoardData.createTestTaskBoardRequestDtoA();

       String taskBoardJson = objectMapper.writeValueAsString(testTaskBoardDtoA);

       mockMvc.perform(
        MockMvcRequestBuilders.post("/boards")
        .contentType(MediaType.APPLICATION_JSON)
        .content(taskBoardJson)
       ).andExpect(
        MockMvcResultMatchers.status().isCreated() 
       );
    }
    
    @Test
    public void testThatCreateTaskReturnsSavedTask() throws Exception{
       TaskBoardRequestDto testTaskBoardDtoA = TestTaskBoardData.createTestTaskBoardRequestDtoA();

       String taskBoardJson = objectMapper.writeValueAsString(testTaskBoardDtoA);

       mockMvc.perform(
        MockMvcRequestBuilders.post("/boards")
        .contentType(MediaType.APPLICATION_JSON)
        .content(taskBoardJson)
       ).andExpect(
        MockMvcResultMatchers.jsonPath("$.id").isNumber() 
       ).andExpect(
        MockMvcResultMatchers.jsonPath("$.taskBoardName").value("Task Board A") 
       );
    }

    // DELETE -----------------------------------------------------------

    @Test
    public void testThatDeleteTaskBoardReturnsHttp204() throws Exception{
        TaskBoardEntity testTaskBoardEntityA = TestTaskBoardData.createTestTaskBoardEntityA();
        taskBoardRepository.save(testTaskBoardEntityA);

        assertThat(taskBoardRepository
                    .findById(testTaskBoardEntityA.getId())
                    .equals(testTaskBoardEntityA));

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/boards/" + testTaskBoardEntityA.getId())
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.status().isNoContent());

        assertThat(taskBoardRepository
                    .findById(testTaskBoardEntityA.getId())
                    .isEmpty());
    }

    @Test
    public void testThatDeleteTaskBoardDeletesCorrectBoard(){

    }

    @Test
    public void testThatDeleteTaskBoardDeletesTaskGroupsAndTasks() throws Exception{

        
    }

    @Test
    public void testThatDeleteTaskBoardOnlyDeletesOwnTaskGroupsAndTasks() throws Exception{

        
    }

    // LIST -----------------------------------------------------------

    @Test
    public void testThatListTaskBoardsReturnsHttpStatus200() throws Exception{

        
    }


    // UPDATE -----------------------------------------------------------

    @Test
    public void testThatUpdateTaskBoardReturnsHttp200() throws Exception{

        
    }

    @Test
    public void testThatUpdateTaskBoardKeepsItsTaskGroups() throws Exception{

        
    }    
}
