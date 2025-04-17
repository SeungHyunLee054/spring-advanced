package org.example.expert.domain.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.example.expert.config.JwtUtil;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtUtil jwtUtil;

	@InjectMocks
	private AuthService authService;

	@Spy
	private User user;

	private final SignupRequest signupRequest = new SignupRequest("test@test", "test", "admin");

	private final SigninRequest signinRequest = new SigninRequest("admin@test", "test");

	@BeforeEach
	void setUp() {
		user = User.builder()
			.id(1L)
			.email("admin@test")
			.password("encodedTestPassword")
			.userRole(UserRole.ADMIN)
			.build();
	}

	@Nested
	@DisplayName("가입 테스트")
	class SignupTest {
		@Test
		@DisplayName("가입 성공")
		void success_signup() {
			// Given
			given(userRepository.existsByEmail(anyString()))
				.willReturn(false);
			given(passwordEncoder.encode(anyString()))
				.willReturn("encodedPassword");
			given(userRepository.save(any()))
				.willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
			given(jwtUtil.createToken(any(), anyString(), any()))
				.willReturn("token");

			// When
			SignupResponse response = authService.signup(signupRequest);

			// Then
			verify(userRepository, times(1)).save(any());

			assertAll(
				() -> assertEquals("token", response.getBearerToken())
			);

		}

		@Test
		@DisplayName("가입 실패 - 이미 존재하는 이메일이 있음")
		void fail_signup_existEmail() {
			// Given
			given(userRepository.existsByEmail(anyString()))
				.willReturn(true);

			// When
			InvalidRequestException exception = assertThrows(InvalidRequestException.class,
				() -> authService.signup(signupRequest));

			// Then
			assertAll(
				() -> assertEquals(HttpStatus.UNAUTHORIZED, exception.getHttpStatus()),
				() -> assertEquals( "이미 존재하는 이메일입니다.", exception.getMessage())
			);

		}
	}

	@Nested
	@DisplayName("로그인 테스트")
	class SigninTest {
		@Test
		@DisplayName("로그인 성공")
		void success_signin() {
		    // Given
			given(userRepository.findByEmail(anyString()))
				.willReturn(Optional.ofNullable(user));
			given(passwordEncoder.matches(anyString(), anyString()))
				.willReturn(true);
			given(jwtUtil.createToken(any(), anyString(), any()))
				.willReturn("token");

		    // When
			SigninResponse response = authService.signin(signinRequest);

		    // Then
			assertAll(
				() -> assertEquals("token", response.getBearerToken())
			);

		}

		@Test
		@DisplayName("로그인 실패 - 유저를 찾을 수 없음")
		void fail_signin_userNotFound() {
		    // Given
			given(userRepository.findByEmail(anyString()))
				.willReturn(Optional.empty());

		    // When
			InvalidRequestException exception = assertThrows(InvalidRequestException.class,
				() -> authService.signin(signinRequest));

		    // Then
			assertAll(
				() -> assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus()),
				() -> assertEquals( "가입되지 않은 유저입니다.", exception.getMessage())
			);

		}

		@Test
		@DisplayName("로그인 실패 - 비밀번호 오류")
		void fail_signin_wrongPassword() {
			// Given
			given(userRepository.findByEmail(anyString()))
				.willReturn(Optional.ofNullable(user));
			given(passwordEncoder.matches(anyString(), anyString()))
				.willReturn(false);

			// When
			AuthException exception = assertThrows(AuthException.class,
				() -> authService.signin(signinRequest));

			// Then
			assertAll(
				() -> assertEquals(HttpStatus.UNAUTHORIZED, exception.getHttpStatus()),
				() -> assertEquals( "잘못된 비밀번호입니다.", exception.getMessage())
			);

		}
	}

}