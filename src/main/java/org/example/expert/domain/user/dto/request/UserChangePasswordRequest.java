package org.example.expert.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserChangePasswordRequest {

	@NotBlank(message = "비밀번호은 필수 입력값이며 공백이 아니어야 합니다.")
	private String oldPassword;

	@Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_\\-+={\\[}\\]:;\"'<,>.?/]).{8,}$",
		message = "새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.")
	@NotBlank(message = "새 비밀번호은 필수 입력값이며 공백이 아니어야 합니다.")
	private String newPassword;
}
