package com.jonassavas.spring_task_api.domain.dto.task_group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTaskGroupRequestDto {
    private String taskGroupName;
    private Long taskBoardId;
}
