package org.example.expert.domain.comment.dto.response;

import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.user.dto.response.UserResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CommentResponse {

	private final Long id;
	private final String contents;
	private final UserResponse user;

	public static CommentResponse from(Comment comment) {
		return CommentResponse.builder()
			.id(comment.getId())
			.contents(comment.getContents())
			.user(UserResponse.from(comment.getUser()))
			.build();
	}

}
