package com.jonassavas.spring_task_api.domain.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "task_groups")
@ToString(exclude = {"tasks", "taskBoard"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TaskGroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 50)
    private String taskGroupName;

    // Hex color:
    // Nullable so frontend can decide default styling
    @Column(length = 7)
    private String color;

    // User for ordering task groups in the UI
    // Lower numbers appear first
    @Column(nullable = false)
    private Integer position;

    @OneToMany(mappedBy = "taskGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    @Builder.Default
    @JsonManagedReference
    private List<TaskEntity> tasks = new ArrayList<>(); 

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "task_board_id", nullable = false)
    @JsonBackReference
    private TaskBoardEntity taskBoard;

    public void addTask(TaskEntity task) {
        tasks.add(task);
        task.setTaskGroup(this);
    }

    public void removeTask(TaskEntity task) {
        tasks.remove(task);
    }
}
