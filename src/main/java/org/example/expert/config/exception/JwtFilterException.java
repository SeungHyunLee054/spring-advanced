package org.example.expert.config.exception;

import org.example.expert.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class JwtFilterException extends BaseException {
	private final HttpStatus httpStatus;
	private final String message;

	public JwtFilterException(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}
}
