package org.example.expert.domain.todo.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.example.expert.config.AuthUserArgumentResolver;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.service.TodoService;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(TodoController.class)
class TodoControllerTest {
	@MockBean
	private TodoService todoService;

	@Mock
	private AuthUserArgumentResolver authUserArgumentResolver;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Spy
	private User user;

	@Spy
	private Todo todo;

	private final AuthUser authUser = new AuthUser(1L, "test@test", UserRole.ADMIN);

	private final TodoSaveRequest todoSaveRequest = new TodoSaveRequest("test", "test");

	private TodoSaveResponse todoSaveResponse;

	private TodoResponse todoResponse;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(new TodoController(todoService))
			.setCustomArgumentResolvers(authUserArgumentResolver)
			.build();

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

		todoSaveResponse = TodoSaveResponse.from(todo);

		todoResponse = TodoResponse.from(todo);
	}

	@Test
	@DisplayName("할일 저장 성공")
	void success_saveTodo() throws Exception {
		// Given
		when(authUserArgumentResolver.supportsParameter(any()))
			.thenReturn(true);
		when(authUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
			.thenReturn(authUser);

		when(todoService.saveTodo(any(), any()))
			.thenReturn(todoSaveResponse);

		// When
		ResultActions perform = mockMvc.perform(post("/todos")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(todoSaveRequest)));

		// Then
		perform.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.id")
					.value(1L),
				jsonPath("$.title")
					.value("test"),
				jsonPath("$.contents")
					.value("test"),
				jsonPath("$.weather")
					.value("weather")
			);

	}

	@Test
	@DisplayName("할일 전체 조회 성공")
	void success_getTodos() throws Exception {
		// Given
		Page<Todo> todos = new PageImpl<>(List.of(todo), PageRequest.of(1, 10), 1);
		Page<TodoResponse> todoResponses = todos.map(TodoResponse::from);

		when(todoService.getTodos(anyInt(), anyInt()))
			.thenReturn(todoResponses);

		// When
		ResultActions perform = mockMvc.perform(get("/todos")
			.param("page", "1")
			.param("size", "10"));

		// Then
		perform.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.content.[0].id")
					.value(1L),
				jsonPath("$.content.[0].title")
					.value("test"),
				jsonPath("$.content.[0].contents")
					.value("test"),
				jsonPath("$.content.[0].weather")
					.value("weather")
			);

	}

	@Test
	@DisplayName("할일 단건 조회 성공")
	void success_getTodo() throws Exception {
		// Given
		when(todoService.getTodo(anyLong()))
			.thenReturn(todoResponse);

		// When
		ResultActions perform = mockMvc.perform(get("/todos/{todoId}", 1L));

		// Then
		perform.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.id")
					.value(1L),
				jsonPath("$.title")
					.value("test"),
				jsonPath("$.contents")
					.value("test"),
				jsonPath("$.weather")
					.value("weather")
			);


	}

}