package org.example.expert.domain.todo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {
	@Mock
	private TodoRepository todoRepository;

	@Mock
	private WeatherClient weatherClient;

	@InjectMocks
	private TodoService todoService;

	@Spy
	private User user;

	@Spy
	private Todo todo;

	private final AuthUser authUser = new AuthUser(1L, "test@test", UserRole.ADMIN);

	private final TodoSaveRequest todoSaveRequest = new TodoSaveRequest("test", "test");

	@BeforeEach
	void setUp() {
		user = User.builder()
			.id(1L)
			.email("test@test")
			.password("encodedTestPassword")
			.userRole(UserRole.USER)
			.build();

		todo = Todo.builder()
			.id(1L)
			.title("test")
			.contents("test")
			.weather("weather")
			.user(user)
			.build();
	}

	@Nested
	@DisplayName("할일 저장 테스트")
	class SaveTodoTest {
		@Test
		@DisplayName("할일 저장 성공")
		void success_saveTodo() {
			// Given
			given(weatherClient.getTodayWeather())
				.willReturn("weather");
			given(todoRepository.save(any()))
				.willAnswer(invocation -> invocation.getArgument(0));

			// When
			TodoSaveResponse response = todoService.saveTodo(authUser, todoSaveRequest);

			// Then
			assertAll(
				() -> assertNotNull(response),
				() -> assertEquals("test", response.getTitle()),
				() -> assertEquals("test", response.getContents()),
				() -> assertEquals("weather", response.getWeather())
			);

		}

	}

	@Nested
	@DisplayName("할일 전체 조회 테스트")
	class GetTodosTest {
		@Test
		@DisplayName("할일 전체 조회 성공")
		void success_getTodos() {
			// Given
			Page<Todo> todos =
				new PageImpl<>(List.of(todo), PageRequest.of(0, 10), 1);

			given(todoRepository.findAllWithUserByOrderByModifiedAtDesc(any()))
				.willReturn(todos);

			// When
			Page<TodoResponse> responses = todoService.getTodos(1, 10);

			// Then
			List<TodoResponse> content = responses.getContent();

			for (TodoResponse response : content) {
				assertAll(
					() -> assertEquals(1L, response.getId()),
					() -> assertEquals("test", response.getTitle()),
					() -> assertEquals("test", response.getContents()),
					() -> assertEquals("weather", response.getWeather())
				);
			}

		}

	}

	@Nested
	@DisplayName("할일 단건 조회 테스트")
	class GetTodoTest {
		@Test
		@DisplayName("할일 단건 조회 성공")
		void success_getTodo() {
			// Given
			given(todoRepository.findWithUserById(anyLong()))
				.willReturn(Optional.ofNullable(todo));

			// When
			TodoResponse response = todoService.getTodo(1L);

			// Then
			assertAll(
				() -> assertEquals(1L, response.getId()),
				() -> assertEquals("test", response.getTitle()),
				() -> assertEquals("test", response.getContents()),
				() -> assertEquals("weather", response.getWeather())
			);

		}

		@Test
		@DisplayName("할일 조회 실패 - 할일을 찾을 수 없음")
		void fail_getTodo_todoNotFound() {
			// Given
			given(todoRepository.findWithUserById(anyLong()))
				.willReturn(Optional.empty());

			// When
			InvalidRequestException exception = assertThrows(InvalidRequestException.class,
				() -> todoService.getTodo(1L));

			// Then
			assertAll(
				() -> assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus()),
				() -> assertEquals("Todo not found", exception.getMessage())
			);

		}
	}

}