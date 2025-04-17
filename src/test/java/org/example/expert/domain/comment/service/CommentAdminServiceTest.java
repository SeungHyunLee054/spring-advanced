package org.example.expert.domain.comment.service;

import static org.mockito.Mockito.*;

import org.example.expert.domain.comment.repository.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentAdminServiceTest {
	@Mock
	private CommentRepository commentRepository;

	@InjectMocks
	private CommentAdminService commentAdminService;

	@Nested
	@DisplayName("댓글 삭제 테스트")
	class DeleteCommentTest {
		@Test
		@DisplayName("댓글 삭제 성공")
		void success_deleteComment() {
			// Given

			// When
			commentAdminService.deleteComment(1L);

			// Then
			verify(commentRepository, times(1)).deleteById(anyLong());

		}
	}

}