package org.example.expert.domain.manager.dto.response;

import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.user.dto.response.UserResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ManagerSaveResponse {

	private final Long id;
	private final UserResponse user;

	public static ManagerSaveResponse from(Manager manager) {
		return ManagerSaveResponse.builder()
			.id(manager.getId())
			.user(UserResponse.from(manager.getUser()))
			.build();
	}

}
