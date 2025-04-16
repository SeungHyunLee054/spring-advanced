package org.example.expert.common.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogUtils {
	public static void logError(RuntimeException runtimeException) {
		log.error(runtimeException.getMessage());
		StackTraceElement firstStackTrace = runtimeException.getStackTrace()[0];
		log.error("발생 위치: {}:{} - Thread: {}, Method: {}",
			firstStackTrace.getClassName(), firstStackTrace.getLineNumber(),
			Thread.currentThread().getName(), firstStackTrace.getMethodName());
	}
}
