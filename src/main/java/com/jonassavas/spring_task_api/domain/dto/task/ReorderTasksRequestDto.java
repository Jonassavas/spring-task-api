package com.jonassavas.spring_task_api.domain.dto.task;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReorderTasksRequestDto {

    @NotNull private Long sourceGroupId;

    @NotNull private Long destinationGroupId;

    @NotNull private List<Long> sourceTaskIds;

    @NotNull private List<Long> destinationTaskIds;
}
