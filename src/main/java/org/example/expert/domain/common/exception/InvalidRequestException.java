package org.example.expert.domain.common.exception;

import org.example.expert.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class InvalidRequestException extends BaseException {
	private final HttpStatus httpStatus;
	private final String message;

	public InvalidRequestException(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}
}
