package org.example.expert.domain.user.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.example.expert.config.AuthUserArgumentResolver;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.service.UserService;
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

@WebMvcTest(UserController.class)
class UserControllerTest {
	@MockBean
	private UserService userService;

	@Mock
	private AuthUserArgumentResolver authUserArgumentResolver;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private final UserResponse userResponse = new UserResponse(1L, "test@test");

	private final UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest("test",
		"newTest1!");

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService))
			.setCustomArgumentResolvers(authUserArgumentResolver)
			.build();
	}

	@Test
	@DisplayName("유저 조회 성공")
	void success_getUser() throws Exception {
		// Given
		when(userService.getUser(anyLong()))
			.thenReturn(userResponse);

		// When
		ResultActions perform = mockMvc.perform(get("/users/{userId}", 1L));

		// Then
		perform.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.id")
					.value(1L),
				jsonPath("$.email")
					.value("test@test")
			);

	}

	@Test
	@DisplayName("비밀번호 변경 성공")
	void success_changePassword() throws Exception {
		// Given
		when(authUserArgumentResolver.supportsParameter(any()))
			.thenReturn(true);
		when(authUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
			.thenReturn(new AuthUser(1L, "test@test", UserRole.ADMIN));

		// When
		ResultActions perform = mockMvc.perform(put("/users")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(userChangePasswordRequest)));

		// Then
		perform.andDo(print())
			.andExpectAll(
				status().isOk()
			);

	}

}