package org.example.expert.domain.auth.exception;

import org.example.expert.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class AuthException extends BaseException {
	private final HttpStatus httpStatus;
	private final String message;

	public AuthException(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}
}
