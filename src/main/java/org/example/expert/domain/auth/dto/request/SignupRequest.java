package org.example.expert.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

	@NotBlank(message = "이메일은 필수 입력값이며 공백이 아니어야 합니다.")
	@Email(message = "이메일 형식이 잘못되었습니다.")
	private String email;

	@Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_\\-+={\\[}\\]:;\"'<,>.?/]).{8,}$",
		message = "비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.")
	@NotBlank(message = "비밀번호는 필수 입력값이며 공백이 아니어야 합니다.")
	private String password;

	@NotBlank(message = "권한은 필수 입력값이며 공백이 아니어야 합니다.")
	private String userRole;
}
