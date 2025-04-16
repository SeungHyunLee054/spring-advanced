package org.example.expert.config.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class JwtUtilException extends RuntimeException {
	private final HttpStatus httpStatus;

	public JwtUtilException(HttpStatus httpStatus, String message) {
		super(message);
		this.httpStatus = httpStatus;
	}
}
