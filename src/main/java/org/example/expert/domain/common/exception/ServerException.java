package org.example.expert.domain.common.exception;

import org.example.expert.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ServerException extends BaseException {
    private final HttpStatus httpStatus;
    private final String message;

    public ServerException(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}
}
