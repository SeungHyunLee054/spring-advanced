package org.example.expert.domain.comment.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

	@Mock
	private CommentRepository commentRepository;
	@Mock
	private TodoRepository todoRepository;
	@InjectMocks
	private CommentService commentService;

	@Test
	@DisplayName("comment 등록 중 할일을 찾지 못해 에러가 발생한다")
	public void fail_saveComment_InvalidRequestException() {
		// given
		long todoId = 1;
		CommentSaveRequest request = new CommentSaveRequest("contents");
		AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);

		given(todoRepository.findById(anyLong())).willReturn(Optional.empty());

		// when
		InvalidRequestException exception = assertThrows(InvalidRequestException.class,
			() -> commentService.saveComment(authUser, todoId, request)
		);

		// then
		assertEquals("Todo not found", exception.getMessage());
	}

	@Test
	@DisplayName("comment를 정상적으로 등록한다")
	public void success_saveComment() {
		// given
		long todoId = 1;
		CommentSaveRequest request = new CommentSaveRequest("contents");
		AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);
		User user = User.fromAuthUser(authUser);
		Todo todo = new Todo("title", "title", "contents", user);
		Comment comment = new Comment(request.getContents(), user, todo);

		given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));
		given(commentRepository.save(any())).willReturn(comment);

		// when
		CommentSaveResponse result = commentService.saveComment(authUser, todoId, request);

		// then
		assertNotNull(result);
	}
}
