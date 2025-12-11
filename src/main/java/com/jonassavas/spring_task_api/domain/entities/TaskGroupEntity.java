package com.jonassavas.spring_task_api.domain.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "task_groups")
public class TaskGroupEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String taskGroupName;

    @Builder.Default // "If the builder doesn't set this field, use this default value"
    @OneToMany(mappedBy = "taskGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Prevent recursion
    private List<TaskEntity> tasks = new ArrayList<>();

    public void addTask(TaskEntity task) {
        tasks.add(task);
        task.setTaskGroup(this);
    }

    public void removeTask(TaskEntity task) {
        tasks.remove(task);
        task.setTaskGroup(null);
    }
}
