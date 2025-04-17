package org.example.expert.domain.comment.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.example.expert.domain.comment.service.CommentAdminService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(controllers = CommentAdminController.class)
class CommentAdminControllerTest {
	@MockBean
	private CommentAdminService commentAdminService;

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("댓글 삭제 성공")
	void success_deleteComment() throws Exception {
		// Given

		// When
		ResultActions perform = mockMvc.perform(delete("/admin/comments/{commentId}", 1L));

		// Then
		perform.andDo(print())
			.andExpectAll(
				status().isOk()
			);

	}

}