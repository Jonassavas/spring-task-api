package com.jonassavas.spring_task_api.domain.dto.task_board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateTaskBoardRequestDto {
    private String taskBoardName;   
}