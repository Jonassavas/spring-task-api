package com.jonassavas.spring_task_api.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jonassavas.spring_task_api.TestDataUtil;
import com.jonassavas.spring_task_api.domain.entities.TaskGroupEntity;
import com.jonassavas.spring_task_api.services.TaskGroupService;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class TaskGroupControllerIntegrationTests {
   
    private TaskGroupService taskGroupService;
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Autowired
    public TaskGroupControllerIntegrationTests(MockMvc mockMvc, TaskGroupService taskGroupService, ObjectMapper objectMapper){
        this.mockMvc = mockMvc;
        this.taskGroupService = taskGroupService;
        this.objectMapper = objectMapper;
    }

    @Test
    public void testThatCreateTaskGroupReturnsHttp201Create() throws Exception{
        TaskGroupEntity testTaskGroupEntityA = TestDataUtil.createTaskGroupEntityA();
        testTaskGroupEntityA.setId(null);
        String taskGroupJson = objectMapper.writeValueAsString(testTaskGroupEntityA);

        mockMvc.perform(
            MockMvcRequestBuilders.post("/taskgroups")
            .contentType(MediaType.APPLICATION_JSON)
            .content(taskGroupJson)
        ).andExpect(
            MockMvcResultMatchers.status().isCreated()
        );
    }

    @Test
    public void testThatCreateTaskGroupReturnsSavedTaskGroup() throws Exception{
        TaskGroupEntity testTaskGroupEntityA = TestDataUtil.createTaskGroupEntityA();
        testTaskGroupEntityA.setId(null);
        String taskGroupJson = objectMapper.writeValueAsString(testTaskGroupEntityA);

        mockMvc.perform(
            MockMvcRequestBuilders.post("/taskgroups")
            .contentType(MediaType.APPLICATION_JSON)
            .content(taskGroupJson)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.id").isNumber()
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.taskGroupName").value("Task Group A")
        );
    }
}
