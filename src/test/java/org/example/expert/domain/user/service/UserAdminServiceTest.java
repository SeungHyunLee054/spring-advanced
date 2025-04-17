package org.example.expert.domain.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
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
class UserAdminServiceTest {
	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserAdminService userAdminService;

	@Spy
	private User user;

	private final UserRoleChangeRequest userRoleChangeRequest = new UserRoleChangeRequest(UserRole.ADMIN.name());

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
	@DisplayName("유저 권한 변경 테스트")
	class changeUserRoleTest {
		@Test
		@DisplayName("유저 권한 변경 성공")
		void success_changeUserRole() {
			// Given
			given(userRepository.findById(anyLong()))
				.willReturn(Optional.ofNullable(user));

			// When
			userAdminService.changeUserRole(1L, userRoleChangeRequest);

			// Then
			verify(userRepository, times(1)).findById(anyLong());
			assertEquals(UserRole.ADMIN, user.getUserRole());

		}

		@Test
		@DisplayName("유저 권한 변경 실패 - 유저를 찾을 수 없음")
		void fail_changeUserRole_userNotFound() {
			// Given
			given(userRepository.findById(anyLong()))
				.willReturn(Optional.empty());

			// When
			InvalidRequestException exception = assertThrows(InvalidRequestException.class,
				() -> userAdminService.changeUserRole(1L, userRoleChangeRequest));

			// Then
			assertAll(
				() -> assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus()),
				() -> assertEquals("User not found", exception.getMessage())
			);

		}

	}

}