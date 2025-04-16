package org.example.expert.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleChangeRequest {

	@NotBlank(message = "역할은 필수 입력값이며 공백이 아니어야 합니다.")
	private String role;
}
