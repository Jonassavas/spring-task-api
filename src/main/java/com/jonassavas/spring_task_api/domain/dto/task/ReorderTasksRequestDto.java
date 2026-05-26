package com.jonassavas.spring_task_api.domain.dto.task;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
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

    @NotEmpty private List<Long> sourceTaskIds;

    @NotEmpty private List<Long> destinationTaskIds;
}
