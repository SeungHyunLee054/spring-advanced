package org.example.expert.domain.comment.dto.response;

import org.example.expert.domain.user.dto.response.UserResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentResponse {

	private final Long id;
	private final String contents;
	private final UserResponse user;

}
