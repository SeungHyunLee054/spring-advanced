package org.example.expert.common.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

public class ErrorResponseUtil {
	public static ResponseEntity<Map<String, Object>> getErrorResponse(HttpStatus status, String message) {
		Map<String, Object> errorResponse = new HashMap<>();
		errorResponse.put("status", status.name());
		errorResponse.put("code", status.value());
		errorResponse.put("message", message);

		return new ResponseEntity<>(errorResponse, status);
	}

	public static ResponseEntity<List<Map<String, Object>>> getErrorResponses(BindingResult result) {
		List<Map<String, Object>> errorResponses = result.getFieldErrors().stream()
			.map(error -> {
				Map<String, Object> errorResponse = new HashMap<>();
				errorResponse.put("field", error.getField());
				errorResponse.put("code", error.getCode());
				errorResponse.put("message", error.getDefaultMessage());
				return errorResponse;
			})
			.toList();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(errorResponses);
	}
}
