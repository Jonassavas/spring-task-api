package com.jonassavas.spring_task_api.domain.dto.task_group;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReorderTaskGroupRequestDto {

    @NotNull
    private Long id;

    @NotNull
    @PositiveOrZero
    private Integer position;
}
