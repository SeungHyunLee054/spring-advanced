package org.example.expert.common.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogUtils {
	public static void logError(Exception ex) {
		log.error("exception : {}", ex.getMessage(), ex);
	}
}
