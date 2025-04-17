package org.example.expert.domain.todo.dto.response;

import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.dto.response.UserResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TodoSaveResponse {

	private final Long id;
	private final String title;
	private final String contents;
	private final String weather;
	private final UserResponse user;

	public static TodoSaveResponse from(Todo todo) {
		return TodoSaveResponse.builder()
			.id(todo.getId())
			.title(todo.getTitle())
			.contents(todo.getContents())
			.weather(todo.getWeather())
			.user(UserResponse.from(todo.getUser()))
			.build();
	}

}
