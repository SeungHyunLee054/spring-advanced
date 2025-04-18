package org.example.expert.domain.comment.service;

import java.util.List;

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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final TodoRepository todoRepository;
	private final CommentRepository commentRepository;

	@Transactional
	public CommentSaveResponse saveComment(AuthUser authUser, long todoId, CommentSaveRequest commentSaveRequest) {
		User user = User.fromAuthUser(authUser);
		Todo todo = todoRepository.findById(todoId)
			.orElseThrow(() -> new InvalidRequestException(HttpStatus.BAD_REQUEST, "Todo not found"));

		Comment newComment = Comment.builder()
			.contents(commentSaveRequest.getContents())
			.user(user)
			.todo(todo)
			.build();

		Comment savedComment = commentRepository.save(newComment);

		return CommentSaveResponse.from(savedComment);
	}

	@Transactional(readOnly = true)
	public List<CommentResponse> getComments(long todoId) {
		List<Comment> commentList = commentRepository.findAllWithUserByTodoId(todoId);

		return commentList.stream()
			.map(CommentResponse::from)
			.toList();
	}
}
