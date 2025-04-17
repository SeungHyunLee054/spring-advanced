package org.example.expert.domain.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
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
class UserServiceTest {
	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserService userService;

	@Spy
	private User user;

	private final UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest("testPassword",
		"newPassword");

	@BeforeEach
	void setUp() {
		user = User.builder()
			.id(1L)
			.email("test@test")
			.password("encodedTestPassword")
			.userRole(UserRole.USER)
			.build();

	}

	@Nested
	@DisplayName("유저 조회 테스트")
	class getUserTest {
		@Test
		@DisplayName("유저 조회 성공")
		void success_getUser() {
			// Given
			given(userRepository.findById(anyLong()))
				.willReturn(Optional.ofNullable(user));

			// When
			UserResponse response = userService.getUser(1L);

			// Then
			assertAll(
				() -> assertEquals(1L, response.getId()),
				() -> assertEquals("test@test", response.getEmail())
			);

		}

		@Test
		@DisplayName("유저 조회 실패 - 유저를 찾을 수 없음")
		void fail_getUser_userNotFound() {
			// Given
			given(userRepository.findById(anyLong()))
				.willReturn(Optional.empty());

			// When
			InvalidRequestException exception = assertThrows(InvalidRequestException.class,
				() -> userService.getUser(1L));

			// Then
			assertAll(
				() -> assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus()),
				() -> assertEquals("User not found", exception.getMessage())
			);

		}

	}

	@Nested
	@DisplayName("비밀번호 변경 테스트")
	class changePasswordTest {
		@Test
		@DisplayName("비밀번호 변경 성공")
		void success_changePassword() {
			// Given
			given(userRepository.findById(anyLong()))
				.willReturn(Optional.ofNullable(user));
			given(passwordEncoder.matches(anyString(), anyString()))
				.willReturn(true)
				.willReturn(false);
			given(passwordEncoder.encode(anyString()))
				.willReturn("encodedNewPassword");

			// When
			userService.changePassword(1L, userChangePasswordRequest);

			// Then
			verify(userRepository, times(1)).findById(anyLong());
			verify(passwordEncoder, times(2)).matches(anyString(), anyString());
			assertEquals("encodedNewPassword", user.getPassword());

		}

		@Test
		@DisplayName("비밀번호 변경 실패 - 유저를 찾을 수 없음")
		void fail_changePassword_userNotFound() {
			// Given
			given(userRepository.findById(anyLong()))
				.willReturn(Optional.empty());

			// When
			InvalidRequestException exception = assertThrows(InvalidRequestException.class,
				() -> userService.changePassword(1L, userChangePasswordRequest));

			// Then
			assertAll(
				() -> assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus()),
				() -> assertEquals("User not found", exception.getMessage())
			);

		}

		@Test
		@DisplayName("비밀번호 변경 실패 - 비밀번호 오류")
		void fail_changePassword_WrongPassword() {
			// Given
			given(userRepository.findById(anyLong()))
				.willReturn(Optional.ofNullable(user));
			given(passwordEncoder.matches(anyString(), anyString()))
				.willReturn(false);

			// When
			InvalidRequestException exception = assertThrows(InvalidRequestException.class,
				() -> userService.changePassword(1L, userChangePasswordRequest));

			// Then
			assertAll(
				() -> assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus()),
				() -> assertEquals("잘못된 비밀번호입니다.", exception.getMessage())
			);

		}

		@Test
		@DisplayName("비밀번호 변경 실패 - 기존 비밀번호와 동일한 비밀번호")
		void fail_changePassword_samePassword() {
			// Given
			given(userRepository.findById(anyLong()))
				.willReturn(Optional.ofNullable(user));
			given(passwordEncoder.matches(anyString(), anyString()))
				.willReturn(true)
				.willReturn(true);

			// When
			InvalidRequestException exception = assertThrows(InvalidRequestException.class,
				() -> userService.changePassword(1L, userChangePasswordRequest));

			// Then
			assertAll(
				() -> assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus()),
				() -> assertEquals("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.", exception.getMessage())
			);

		}
	}

}