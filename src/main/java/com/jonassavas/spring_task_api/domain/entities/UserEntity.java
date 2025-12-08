// package com.jonassavas.spring_task_api.domain.entities;

// import java.util.List;

// import jakarta.persistence.CascadeType;
// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.OneToMany;
// import jakarta.persistence.Table;
// import lombok.Data;

// @Entity
// @Table(name = "users")
// @Data
// public class UserEntity {
    
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;
    
//     private String email;
//     private String passwordHash;

//     @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//     private List<TaskGroupEntity> taskGroups;
// }
