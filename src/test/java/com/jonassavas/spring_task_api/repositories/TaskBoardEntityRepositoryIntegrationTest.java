package com.jonassavas.spring_task_api.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import jakarta.transaction.Transactional;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TaskBoardEntityRepositoryIntegrationTest {

    //TODO
    private TaskBoardRepository underTest;

    @Autowired
    public TaskBoardEntityRepositoryIntegrationTest(TaskBoardRepository underTest){
        this.underTest = underTest;
    }

    @Test
    @Transactional
    public void testThatTaskBoardCanBeCreatedAndRecalled(){

    }
}
