package org.example.expert.config;

import java.io.IOException;

import org.example.expert.common.util.ErrorResponseUtil;
import org.example.expert.common.util.LogUtils;
import org.example.expert.config.exception.JwtFilterException;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {
	private final ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
		@NonNull FilterChain chain) throws ServletException, IOException {
		try {
			chain.doFilter(request, response);
		} catch (JwtFilterException jwtFilterException) {
			LogUtils.logError(jwtFilterException);

			if (jwtFilterException.getHttpStatus().equals(HttpStatus.UNAUTHORIZED)) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			} else if (jwtFilterException.getHttpStatus().equals(HttpStatus.BAD_REQUEST)) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			objectMapper.writeValue(response.getWriter(),
				ErrorResponseUtil.getErrorResponse(jwtFilterException.getHttpStatus(),
					jwtFilterException.getMessage()));
		}
	}

}
