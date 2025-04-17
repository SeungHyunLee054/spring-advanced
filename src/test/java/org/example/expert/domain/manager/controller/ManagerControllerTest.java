package org.example.expert.domain.manager.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.example.expert.config.AuthUserArgumentResolver;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.service.ManagerService;
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

@WebMvcTest(controllers = ManagerController.class)
class ManagerControllerTest {
	@MockBean
	private ManagerService managerService;

	@Mock
	private AuthUserArgumentResolver authUserArgumentResolver;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private final AuthUser authUser = new AuthUser(1L, "test@test", UserRole.ADMIN);

	private final ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest(2L);

	private final UserResponse userResponse = UserResponse.builder()
		.id(1L)
		.email("test@test")
		.build();

	private final ManagerSaveResponse managerSaveResponse = new ManagerSaveResponse(1L, userResponse);

	private final ManagerResponse managerResponse = new ManagerResponse(1L, userResponse);

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(new ManagerController(managerService))
			.setCustomArgumentResolvers(authUserArgumentResolver)
			.build();
	}

	@Test
	@DisplayName("매니저 저장 성공")
	void success_saveManager() throws Exception {
		// Given
		when(authUserArgumentResolver.supportsParameter(any()))
			.thenReturn(true);
		when(authUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
			.thenReturn(authUser);
		when(managerService.saveManager(any(), anyLong(), any()))
			.thenReturn(managerSaveResponse);

		// When
		ResultActions perform = mockMvc.perform(post("/todos/{todoId}/managers", 1L)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(managerSaveRequest)));

		// Then
		perform.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.id")
					.value(1L),
				jsonPath("$.user.id")
					.value(1L),
				jsonPath("$.user.email")
					.value("test@test")
			);

	}

	@Test
	@DisplayName("매니저 전체 조회 성공")
	void success_getManagers() throws Exception {
		// Given
		when(managerService.getManagers(anyLong()))
			.thenReturn(List.of(managerResponse));

		// When
		ResultActions perform = mockMvc.perform(get("/todos/{todoId}/managers", 1L));

		// Then
		perform.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.[0].id")
					.value(1L),
				jsonPath("$.[0].user.id")
					.value(1L),
				jsonPath("$.[0].user.email")
					.value("test@test")
			);

	}

	@Test
	@DisplayName("매니저 삭제 성공")
	void success_deleteManager() throws Exception {
		// Given
		when(authUserArgumentResolver.supportsParameter(any()))
			.thenReturn(true);
		when(authUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
			.thenReturn(authUser);

		// When
		ResultActions perform = mockMvc.perform(delete("/todos/{todoId}/managers/{managerId}"
			, 1L, 1L));

		// Then
		perform.andDo(print())
			.andExpectAll(
				status().isOk()
			);

	}
}