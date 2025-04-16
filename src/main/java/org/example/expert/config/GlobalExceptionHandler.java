package org.example.expert.config;

import java.util.List;
import java.util.Map;

import org.example.expert.common.exception.BaseException;
import org.example.expert.common.util.ErrorResponseUtil;
import org.example.expert.common.util.LogUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
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
	public ResponseEntity<List<Map<String, Object>>> inputValidationExceptionHandler(BindingResult result) {
		log.error(result.getFieldErrors().toString());

		return ErrorResponseUtil.getErrorResponses(result);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadableException(
		HttpMessageNotReadableException ex) {
		LogUtils.logError(ex);

		return ErrorResponseUtil.getErrorResponse(HttpStatus.BAD_REQUEST, "입력 ");
	}

}
