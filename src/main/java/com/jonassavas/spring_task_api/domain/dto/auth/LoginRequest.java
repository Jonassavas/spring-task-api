package com.jonassavas.spring_task_api.domain.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {

   @NotBlank(message = "Username is required")
   @Size(min = 3, max = 30, message = "Username must be 3-30 characters")
   private String username;
   
   @NotBlank(message = "Password is required")
   @Size(min = 6, max = 100, message = "Password must be 6-100 characters")
   private String password;
}