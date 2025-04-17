package org.example.expert.domain.manager.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
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
class ManagerServiceTest {

	@Mock
	private ManagerRepository managerRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private TodoRepository todoRepository;

	@InjectMocks
	private ManagerService managerService;

	@Spy
	private User user;

	@Spy
	private User userAdmin;

	@Spy
	private Todo todo;

	@Spy
	private Manager manager;

	private final AuthUser authUser = new AuthUser(2L, "a@a.com", UserRole.USER);

	private final ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest(2L);

	@BeforeEach
	void setUp() {
		user = User.builder()
			.id(2L)
			.email("test@test")
			.password("encodedTestPassword")
			.userRole(UserRole.USER)
			.build();

		userAdmin = User.builder()
			.id(1L)
			.email("test@test")
			.password("encodedTestPassword")
			.userRole(UserRole.ADMIN)
			.build();

		todo = Todo.builder()
			.id(1L)
			.title("Test Title")
			.contents("Test Contents")
			.user(user)
			.build();

		manager = Manager.builder()
			.id(1L)
			.user(todo.getUser())
			.todo(todo)
			.build();
	}

	@Nested
	class SaveManagerTest {
		@Test
		@DisplayName("매니저 저장 성공")
		void success_saveManager() {
			// given
			given(todoRepository.findById(anyLong()))
				.willReturn(Optional.of(todo));
			given(userRepository.findById(anyLong()))
				.willReturn(Optional.of(userAdmin));
			given(managerRepository.save(any(Manager.class)))
				.willAnswer(invocation -> invocation.getArgument(0));

			// when
			ManagerSaveResponse response = managerService.saveManager(authUser, 1L, managerSaveRequest);

			// then
			assertAll(
				() -> assertNotNull(response),
				() -> assertEquals(1L, response.getUser().getId()),
				() -> assertEquals("test@test", response.getUser().getEmail())
			);
		}

		@Test
		@DisplayName("할일 저장 실패 - 할일의 user가 null")
		void fail_saveManager_userIsNull() {
			// given
			given(todoRepository.findById(anyLong()))
				.willReturn(Optional.of(todo.toBuilder()
					.user(null)
					.build()));

			// when
			InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
				managerService.saveManager(authUser, 1L, managerSaveRequest)
			);

			// then
			assertAll(
				() -> assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus()),
				() -> assertEquals("담당자를 등록하려고 하는 유저가 일정을 만든 유저가 유효하지 않습니다.",
					exception.getMessage())
			);

		}

		@Test
		@DisplayName("할일 저장 실패 - 할일의 작성자와 유저가 불일치 ")
		void fail_saveManager_notEqualsUser() {
			// given
			given(todoRepository.findById(anyLong()))
				.willReturn(Optional.of(todo.toBuilder()
					.user(user.toBuilder()
						.id(3L)
						.build())
					.build()));

			// when
			InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
				managerService.saveManager(authUser, 1L, managerSaveRequest)
			);

			// then
			assertAll(
				() -> assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus()),
				() -> assertEquals("담당자를 등록하려고 하는 유저가 일정을 만든 유저가 유효하지 않습니다.",
					exception.getMessage())
			);
		}

		@Test
		@DisplayName("일정 저장 실패 - 유저를 찾을 수 없음")
		void fail_saveManager_userNotFound() {
			// Given
			given(todoRepository.findById(anyLong()))
				.willReturn(Optional.of(todo));
			given(userRepository.findById(anyLong()))
				.willReturn(Optional.empty());

			// When
			InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
				managerService.saveManager(authUser, 1L, managerSaveRequest)
			);

			// then
			assertAll(
				() -> assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus()),
				() -> assertEquals("등록하려고 하는 담당자 유저가 존재하지 않습니다.", exception.getMessage())
			);

		}

		@Test
		@DisplayName("매니저 저장 실패 - 유저 본인과 등록하려는 매니저가 같음")
		void fail_saveManager_sameUser() {
			// Given
			given(todoRepository.findById(anyLong()))
				.willReturn(Optional.of(todo));
			given(userRepository.findById(anyLong()))
				.willReturn(Optional.of(user));

			// When
			InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
				managerService.saveManager(authUser, 1L, managerSaveRequest)
			);

			// then
			assertAll(
				() -> assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus()),
				() -> assertEquals("일정 작성자는 본인을 담당자로 등록할 수 없습니다.", exception.getMessage())
			);

		}
	}

	@Nested
	class GetManagersTest {
		@Test // 테스트코드 샘플
		@DisplayName("매니저 전체 조회 성공")
		public void success_getManagers() {
			List<Manager> managerList = List.of(manager);

			given(todoRepository.findById(anyLong()))
				.willReturn(Optional.of(todo));
			given(managerRepository.findWithUserByTodoId(anyLong()))
				.willReturn(managerList);

			// when
			List<ManagerResponse> managerResponses = managerService.getManagers(1L);

			// then
			for (ManagerResponse managerResponse : managerResponses) {
				assertAll(
					() -> assertEquals(1L, managerResponse.getId()),
					() -> assertEquals(2L, managerResponse.getUser().getId()),
					() -> assertEquals("test@test", managerResponse.getUser().getEmail())
				);
			}

		}

		@Test
		@DisplayName("매니저 전체 조회 실패 - 일정을 찾을 수 없음")
		void fail_getManagers_todoNotFound() {
			// Given
			given(todoRepository.findById(anyLong()))
				.willReturn(Optional.empty());

			// When
			InvalidRequestException exception = assertThrows(InvalidRequestException.class,
				() -> managerService.getManagers(1L));

			// Then
			assertAll(
				() -> assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus()),
				() -> assertEquals("Todo not found", exception.getMessage())
			);

		}

	}

	@Nested
	@DisplayName("매니저 삭제 테스트")
	class DeleteManagerTest {
		@Test
		@DisplayName("매니저 삭제 성공")
		void success_deleteManager() {
			// Given
			given(userRepository.findById(anyLong()))
				.willReturn(Optional.of(user));
			given(todoRepository.findById(anyLong()))
				.willReturn(Optional.ofNullable(todo));
			given(managerRepository.findById(anyLong()))
				.willReturn(Optional.ofNullable(manager));

			// When
			managerService.deleteManager(authUser, 1L, 1L);

			// Then
			verify(managerRepository, times(1)).delete(any(Manager.class));

		}

		@Test
		@DisplayName("매니저 삭제 실패 - 유저를 찾을 수 없음")
		void fail_deleteManager_userNotFound() {
			// Given
			given(userRepository.findById(anyLong()))
				.willReturn(Optional.empty());

			// When
			InvalidRequestException exception = assertThrows(InvalidRequestException.class,
				() -> managerService.deleteManager(authUser, 1L, 1L));

			// Then
			assertAll(
				() -> assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus()),
				() -> assertEquals("User not found", exception.getMessage())
			);

		}

		@Test
		@DisplayName("매니저 삭제 실패 - 할일을 찾을 수 없음")
		void fail_deleteManager_todoNotFound() {
			// Given
			given(userRepository.findById(anyLong()))
				.willReturn(Optional.of(userAdmin));
			given(todoRepository.findById(anyLong()))
				.willReturn(Optional.empty());

			// When
			InvalidRequestException exception = assertThrows(InvalidRequestException.class,
				() -> managerService.deleteManager(authUser, 1L, 1L));

			// Then
			assertAll(
				() -> assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus()),
				() -> assertEquals("Todo not found", exception.getMessage())
			);

		}

		@Test
		@DisplayName("매니저 삭제 실패 - 할일의 작성자가 없음")
		void fail_deleteManager_todosUserIsNull() {
			// Given
			given(userRepository.findById(anyLong()))
				.willReturn(Optional.of(userAdmin));
			given(todoRepository.findById(anyLong()))
				.willReturn(Optional.ofNullable(todo.toBuilder()
					.user(null)
					.build()));

			// When
			InvalidRequestException exception = assertThrows(InvalidRequestException.class,
				() -> managerService.deleteManager(authUser, 1L, 1L));

			// Then
			assertAll(
				() -> assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus()),
				() -> assertEquals("해당 일정을 만든 유저가 유효하지 않습니다.", exception.getMessage())
			);

		}

		@Test
		@DisplayName("매니저 삭제 실패 - 할일의 작성자와 유저가 일치하지 않음")
		void fail_deleteManager_notEqualsUser() {
			// Given
			given(userRepository.findById(anyLong()))
				.willReturn(Optional.of(userAdmin));
			given(todoRepository.findById(anyLong()))
				.willReturn(Optional.ofNullable(todo.toBuilder()
					.user(user)
					.build()));

			// When
			InvalidRequestException exception = assertThrows(InvalidRequestException.class,
				() -> managerService.deleteManager(authUser, 1L, 1L));

			// Then
			assertAll(
				() -> assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus()),
				() -> assertEquals("해당 일정을 만든 유저가 유효하지 않습니다.", exception.getMessage())
			);

		}

		@Test
		@DisplayName("매니저 삭제 실패 - 매니저를 찾을 수 없음")
		void fail_deleteManager_managerNotFound() {
			// Given
			given(userRepository.findById(anyLong()))
				.willReturn(Optional.of(user));
			given(todoRepository.findById(anyLong()))
				.willReturn(Optional.ofNullable(todo));
			given(managerRepository.findById(anyLong()))
				.willReturn(Optional.empty());

			// When
			InvalidRequestException exception = assertThrows(InvalidRequestException.class,
				() -> managerService.deleteManager(authUser, 1L, 1L));

			// Then
			assertAll(
				() -> assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus()),
				() -> assertEquals("Manager not found", exception.getMessage())
			);

		}

		@Test
		@DisplayName("매니저 삭제 실패 - 할일의 매니저가 아님")
		void fail_deleteManager_notEqualsManager() {
			// Given
			given(userRepository.findById(anyLong()))
				.willReturn(Optional.of(user));
			given(todoRepository.findById(anyLong()))
				.willReturn(Optional.ofNullable(todo));
			given(managerRepository.findById(anyLong()))
				.willReturn(Optional.ofNullable(manager.toBuilder()
					.todo(todo.toBuilder()
						.id(2L)
						.build())
					.build()));

			// When
			InvalidRequestException exception = assertThrows(InvalidRequestException.class,
				() -> managerService.deleteManager(authUser, 1L, 1L));

			// Then
			assertAll(
				() -> assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus()),
				() -> assertEquals("해당 일정에 등록된 담당자가 아닙니다.", exception.getMessage())
			);

		}
	}

}
