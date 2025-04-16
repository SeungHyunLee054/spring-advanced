package org.example.expert.domain.todo.dto.response;

import java.time.LocalDateTime;

import org.example.expert.domain.user.dto.response.UserResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TodoResponse {

	private final Long id;
	private final String title;
	private final String contents;
	private final String weather;
	private final UserResponse user;
	private final LocalDateTime createdAt;
	private final LocalDateTime modifiedAt;

}
