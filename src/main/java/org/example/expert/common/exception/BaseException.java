package org.example.expert.common.exception;

import org.springframework.http.HttpStatus;

public abstract class BaseException extends RuntimeException {
	public abstract HttpStatus getHttpStatus();

	public abstract String getMessage();
}
