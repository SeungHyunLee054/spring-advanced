package org.example.expert.config;

import java.util.List;
import java.util.Map;

import org.example.expert.common.exception.BaseException;
import org.example.expert.common.util.ErrorResponseUtil;
import org.example.expert.common.util.LogUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BaseException.class)
	public ResponseEntity<Map<String, Object>> handleBaseException(BaseException ex) {
		LogUtils.logError(ex);

		return ErrorResponseUtil.getErrorResponse(ex.getHttpStatus(), ex.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<List<Map<String, Object>>> inputValidationExceptionHandler(
		MethodArgumentNotValidException ex) {
		LogUtils.logError(ex);

		return ErrorResponseUtil.getErrorResponses(ex.getBindingResult());
	}

}
