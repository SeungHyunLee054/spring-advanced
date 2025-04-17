package org.example.expert.domain.todo.service;

import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

	private final TodoRepository todoRepository;
	private final WeatherClient weatherClient;

	@Transactional
	public TodoSaveResponse saveTodo(AuthUser authUser, TodoSaveRequest todoSaveRequest) {
		User user = User.fromAuthUser(authUser);

		String weather = weatherClient.getTodayWeather();

		Todo newTodo = Todo.builder()
			.title(todoSaveRequest.getTitle())
			.contents(todoSaveRequest.getContents())
			.weather(weather)
			.user(user)
			.build();

		Todo savedTodo = todoRepository.save(newTodo);

		return TodoSaveResponse.from(savedTodo);
	}

	public Page<TodoResponse> getTodos(int page, int size) {
		Pageable pageable = PageRequest.of(page - 1, size);

		Page<Todo> todos = todoRepository.findAllWithUserByOrderByModifiedAtDesc(pageable);

		return todos.map(TodoResponse::from);
	}

	public TodoResponse getTodo(long todoId) {
		Todo todo = todoRepository.findWithUserById(todoId)
			.orElseThrow(() -> new InvalidRequestException(HttpStatus.BAD_REQUEST, "Todo not found"));

		return TodoResponse.from(todo);
	}
}
