package com.jonassavas.spring_task_api.domain.entities;

import java.util.ArrayList;
import java.util.List;

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

@Entity
@Table(name = "task_boards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskBoardEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String taskBoardName;

    //Later: owner
    // private UserEntity owner;

    @OneToMany(
        mappedBy = "taskBoard",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @Builder.Default
    private List<TaskGroupEntity> taskGroups = new ArrayList<>();

    public void addTaskGroup(TaskGroupEntity taskGroup) {
        taskGroups.add(taskGroup);
        //taskGroup.setTaskBoard(this);
    }

    public void removeTaskGroup(TaskGroupEntity task) {
        taskGroups.remove(task);
        //task.setTaskGroup(null);
    }
}
