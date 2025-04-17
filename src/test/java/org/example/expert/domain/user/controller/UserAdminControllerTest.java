package org.example.expert.domain.user.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.service.UserAdminService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = UserAdminController.class)
class UserAdminControllerTest {
	@MockBean
	private UserAdminService userAdminService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private final UserRoleChangeRequest userRoleChangeRequest = new UserRoleChangeRequest(UserRole.ADMIN.name());

	@Test
	@DisplayName("유저 권한 변경 성공")
	void success_changeUserRole() throws Exception {
		// Given

		// When
		ResultActions perform = mockMvc.perform(patch("/admin/users/{userId}", 1L)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(userRoleChangeRequest)));

		// Then
		perform.andDo(print())
			.andExpectAll(
				status().isOk()
			);

	}
}