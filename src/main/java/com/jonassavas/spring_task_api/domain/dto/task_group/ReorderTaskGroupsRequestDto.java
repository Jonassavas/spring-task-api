package com.jonassavas.spring_task_api.domain.dto.task_group;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReorderTaskGroupsRequestDto {

    @NotEmpty private List<Long> groupIds;
}
