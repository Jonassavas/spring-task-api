package com.jonassavas.taskmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TaskApplication {
   public TaskApplication() {
   }

   public static void main(String[] args) {
      SpringApplication.run(TaskApplication.class, args);
   }
}
