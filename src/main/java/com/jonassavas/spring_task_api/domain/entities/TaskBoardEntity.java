package com.jonassavas.spring_task_api.domain.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @Column(nullable = false, length = 50)
    private String taskBoardName;

    @OneToMany(
        mappedBy = "taskBoard",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @Builder.Default
    @JsonManagedReference
    private List<TaskGroupEntity> taskGroups = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonBackReference
    @Schema(hidden = true) // tells Swagger/OpenAPI to ignore this field in the UI
    private UserEntity owner;

    public void addTaskGroup(TaskGroupEntity taskGroup) {
        taskGroups.add(taskGroup);
        taskGroup.setTaskBoard(this);
    }

    public void removeTaskGroup(TaskGroupEntity taskGroup) {
        taskGroups.remove(taskGroup);
    }
}
