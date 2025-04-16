package org.example.expert.domain.common.dto;

import org.example.expert.domain.user.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthUser {

	private final Long id;

	private final String email;

	private final UserRole userRole;
}
