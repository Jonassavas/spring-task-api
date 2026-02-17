package com.jonassavas.spring_task_api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jonassavas.spring_task_api.repositories.TaskBoardRepository;
import com.jonassavas.spring_task_api.services.TaskBoardService;

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
    public TaskBoardControllerIntegrationTests(){

    }
    
}
