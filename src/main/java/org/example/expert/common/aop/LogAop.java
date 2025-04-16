package org.example.expert.common.aop;

import java.time.LocalDateTime;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAop {
	private final ObjectMapper objectMapper;

	@Pointcut("execution(* org.example.expert.domain..*AdminController.*(..))")
	private void logPointcut() {

	}

	@Around(value = "logPointcut()")
	public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
		HttpServletRequest currentRequest = getCurrentRequest();

		if (currentRequest == null) {
			return joinPoint.proceed();
		}

		String uri = currentRequest.getRequestURI();
		String methodType = currentRequest.getMethod();
		Long userId = (Long)currentRequest.getAttribute("userId");
		LocalDateTime requestTime = LocalDateTime.now();

		Object[] args = joinPoint.getArgs();

		String requestBodyJson;
		try {
			requestBodyJson = objectMapper.writeValueAsString(args);
		} catch (Exception e) {
			requestBodyJson = "Json 형식이 아닙니다.";
		}

		log.info("메서드 실행 전");
		log.info("사용자 id : {}", userId);
		log.info("요청 시간 : {}", requestTime);
		log.info("요청 URL : {} {}", methodType, uri);
		log.info("요청 본문 : {}", requestBodyJson);

		Object result = joinPoint.proceed();

		String responseBodyJson;
		try {
			responseBodyJson = objectMapper.writeValueAsString(result);
		} catch (Exception e) {
			responseBodyJson = "Json 형식이 아닙니다.";
		}

		log.info("메서드 실행 후");
		log.info("응답 타입 : {}", result != null ? result.getClass().getSimpleName() : "void");
		log.info("응답 본문 : {}", responseBodyJson);

		return result;
	}

	private HttpServletRequest getCurrentRequest() {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

		if (requestAttributes instanceof ServletRequestAttributes) {
			return ((ServletRequestAttributes)requestAttributes).getRequest();
		}

		return null;
	}

}
