package com.jonassavas.spring_task_api.domain.dto.auth;

import jakarta.validation.constraints.Email;
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
public class RegisterRequest {

   @NotBlank(message = "Username is required")
   @Size(min = 3, max = 30)
   private String username;
   
   @NotBlank(message = "Password is required")
   @Size(min = 6, max = 100)
   private String password;

   @NotBlank(message = "Email is required")
   @Email(message = "Email must be valid")
   @Size(max = 254)
   private String email;
}
