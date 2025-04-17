package org.example.expert.domain.comment.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.example.expert.config.AuthUserArgumentResolver;
import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.comment.service.CommentService;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = CommentController.class)
class CommentControllerTest {
	@MockBean
	private CommentService commentService;

	@Mock
	private AuthUserArgumentResolver authUserArgumentResolver;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private final AuthUser authUser = new AuthUser(1L, "test@test", UserRole.ADMIN);

	private final CommentSaveRequest commentSaveRequest = new CommentSaveRequest("contents");

	private final UserResponse userResponse = UserResponse.builder()
		.id(1L)
		.email("test@test")
		.build();

	private final CommentSaveResponse commentSaveResponse = new CommentSaveResponse(1L, "test", userResponse);

	private final CommentResponse commentResponse = new CommentResponse(1L, "test", userResponse);

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(new CommentController(commentService))
			.setCustomArgumentResolvers(authUserArgumentResolver)
			.build();
	}

	@Test
	@DisplayName("댓글 저장 성공")
	void success_saveComment() throws Exception {
		// Given
		when(authUserArgumentResolver.supportsParameter(any()))
			.thenReturn(true);
		when(authUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
			.thenReturn(authUser);
		when(commentService.saveComment(any(), anyLong(), any()))
			.thenReturn(commentSaveResponse);

		// When
		ResultActions perform = mockMvc.perform(post("/todos/{todoId}/comments", 1L)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(commentSaveRequest)));

		// Then
		perform.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.id")
					.value(1L),
				jsonPath("$.contents")
					.value("test"),
				jsonPath("$.user.id")
					.value(1L),
				jsonPath("$.user.email")
					.value("test@test")
			);

	}

	@Test
	@DisplayName("댓글 전체 조회 성공")
	void success_getComments() throws Exception {
	    // Given
		when(commentService.getComments(anyLong()))
			.thenReturn(List.of(commentResponse));

	    // When
		ResultActions perform = mockMvc.perform(get("/todos/{todoId}/comments", 1L));

		// Then
		perform.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.[0].id")
					.value(1L),
				jsonPath("$.[0].contents")
					.value("test"),
				jsonPath("$.[0].user.id")
					.value(1L),
				jsonPath("$.[0].user.email")
					.value("test@test")
			);

	}

}