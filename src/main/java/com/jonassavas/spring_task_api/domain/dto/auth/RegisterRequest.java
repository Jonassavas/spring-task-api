package com.jonassavas.spring_task_api.domain.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {
   @NotBlank
   private String username;
   
   @NotBlank
   private String password;

   @Email
   private String email;
}
