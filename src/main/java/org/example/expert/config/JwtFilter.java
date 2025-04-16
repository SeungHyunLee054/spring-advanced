package org.example.expert.config;

import java.io.IOException;

import org.example.expert.config.exception.JwtFilterException;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
		@NonNull FilterChain chain) throws ServletException, IOException {
		String url = request.getRequestURI();

		if (url.startsWith("/swagger") || url.startsWith("/v3/api-docs") || url.startsWith("/swagger-resources")) {
			chain.doFilter(request, response);  // Swagger 요청은 필터를 거치지 않고 바로 지나감
			return;
		}

		if (url.startsWith("/auth")) {
			chain.doFilter(request, response);
			return;
		}

		String bearerJwt = request.getHeader("Authorization");

		if (bearerJwt == null) {
			// 토큰이 없는 경우 400을 반환합니다.
			throw new JwtFilterException(HttpStatus.BAD_REQUEST, "JWT 토큰이 필요합니다.");
		}

		String jwt = jwtUtil.substringToken(bearerJwt);

		// JWT 유효성 검사와 claims 추출
		Claims claims = jwtUtil.extractClaims(jwt);
		if (claims == null) {
			throw new JwtFilterException(HttpStatus.BAD_REQUEST, "잘못된 JWT 토큰입니다.");
		}

		UserRole userRole = UserRole.valueOf(claims.get("userRole", String.class));

		request.setAttribute("userId", Long.parseLong(claims.getSubject()));
		request.setAttribute("email", claims.get("email"));
		request.setAttribute("userRole", claims.get("userRole"));

		if (url.startsWith("/admin") && !UserRole.ADMIN.equals(userRole)) {
			// 관리자 권한이 없는 경우 403을 반환합니다.
			throw new JwtFilterException(HttpStatus.FORBIDDEN, "관리자 권한이 없습니다.");
		}

		chain.doFilter(request, response);
	}

}
