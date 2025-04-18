package org.example.expert.domain.todo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TodoSaveRequest {

	@NotBlank(message = "제목은 필수 입력값이며 공백이 아니어야 합니다.")
	private String title;

	@NotBlank(message = "내용은 필수 입력값이며 공백이 아니어야 합니다.")
	private String contents;
}
