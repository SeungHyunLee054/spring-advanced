package org.example.expert.domain.auth.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = AuthController.class)
class AuthControllerTest {
	@MockBean
	private AuthService authService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private final SignupRequest signupRequest = new SignupRequest("admin@test", "testPassword1!", "admin");

	private final SignupResponse signupResponse = new SignupResponse("token");

	private final SigninRequest signinRequest = new SigninRequest("admin@test", "testPassword1");

	private final SigninResponse signinResponse = new SigninResponse("token");

	@Test
	@DisplayName("가입 성공")
	void success_signup() throws Exception {
		// Given
		when(authService.signup(any()))
			.thenReturn(signupResponse);

		// When
		ResultActions perform = mockMvc.perform(post("/auth/signup")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(signupRequest)));

		// Then
		perform.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.bearerToken")
					.value("token")
			);

	}

	@Test
	void test() throws Exception {
		// Given
		when(authService.signin(any()))
			.thenReturn(signinResponse);

		// When
		ResultActions perform = mockMvc.perform(post("/auth/signin")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(signinRequest)));

		// Then
		perform.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.bearerToken")
					.value("token")
			);


	}

}