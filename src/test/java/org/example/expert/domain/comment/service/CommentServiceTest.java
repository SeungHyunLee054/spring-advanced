package org.example.expert.domain.comment.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
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
class CommentServiceTest {

	@Mock
	private CommentRepository commentRepository;

	@Mock
	private TodoRepository todoRepository;

	@InjectMocks
	private CommentService commentService;

	@Spy
	private User userAdmin;

	@Spy
	private User userLoggedIn;

	@Spy
	private Todo todo;

	@Spy
	private Comment comment;

	private final AuthUser authUser = new AuthUser(1L, "userAdmin@test", UserRole.USER);

	private final CommentSaveRequest commentSaveRequest = new CommentSaveRequest("contents");

	@BeforeEach
	void setUp() {
		userAdmin = User.builder()
			.id(2L)
			.email("admin@test")
			.password("encodedTestPassword")
			.userRole(UserRole.ADMIN)
			.build();

		userLoggedIn = User.builder()
			.id(1L)
			.email("user@test")
			.password("encodedTestPassword")
			.userRole(UserRole.ADMIN)
			.build();

		todo = Todo.builder()
			.id(1L)
			.title("test")
			.contents("test")
			.weather("weather")
			.user(userAdmin)
			.build();

		comment = Comment.builder()
			.id(1L)
			.contents("test")
			.user(userLoggedIn)
			.todo(todo)
			.build();
	}

	@Nested
	class SaveCommentTest {
		@Test
		@DisplayName("댓글 저장 성공")
		public void success_saveComment() {
			// given
			given(todoRepository.findById(anyLong()))
				.willReturn(Optional.of(todo));
			given(commentRepository.save(any()))
				.willReturn(comment);

			// when
			CommentSaveResponse response = commentService.saveComment(authUser, 1L, commentSaveRequest);

			// then
			assertAll(
				() -> assertEquals(1L, response.getId()),
				() -> assertEquals("test", response.getContents()),
				() -> assertEquals(1L, response.getUser().getId()),
				() -> assertEquals("user@test", response.getUser().getEmail())
			);

		}

		@Test
		@DisplayName("댓글 저장 실패 - 할일을 찾일 수 없음")
		public void fail_saveComment_todoNotFound() {
			// given
			given(todoRepository.findById(anyLong()))
				.willReturn(Optional.empty());

			// when
			InvalidRequestException exception = assertThrows(InvalidRequestException.class,
				() -> commentService.saveComment(authUser, 1L, commentSaveRequest)
			);

			// then
			assertAll(
				() -> assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus()),
				() -> assertEquals("Todo not found", exception.getMessage())
			);

		}
	}

	@Nested
	@DisplayName("댓글 전체 조회 테스트")
	class GetCommentsTest {
		@Test
		@DisplayName("댓글 전체 조회 성공")
		void success_getComments() {
			// Given
			given(commentRepository.findAllWithUserByTodoId(anyLong()))
				.willReturn(List.of(comment));

			// When
			List<CommentResponse> responses = commentService.getComments(1L);

			// Then
			for (CommentResponse response : responses) {
				assertAll(
					() -> assertEquals(1L, response.getId()),
					() -> assertEquals("test", response.getContents()),
					() -> assertEquals(1L, response.getUser().getId()),
					() -> assertEquals("admin@test", response.getUser().getEmail())
				);
			}

		}
	}

}
