package org.example.expert.domain.manager.dto.response;

import org.example.expert.domain.user.dto.response.UserResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ManagerSaveResponse {

	private final Long id;
	private final UserResponse user;

}
