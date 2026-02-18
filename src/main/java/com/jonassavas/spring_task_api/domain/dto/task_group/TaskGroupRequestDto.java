package com.jonassavas.spring_task_api.domain.dto.task_group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



/*
    USED FOR:
            - CREATE
            - UPDATE

    This class serves as the DTO for incoming and outgoing requests.
    This means that when the user wants to create/change any parameter
    of their TaskGroup they do it via this DTO.
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskGroupRequestDto {
    //private Long id;
    private String taskGroupName;
    private Long taskBoardId;
}
