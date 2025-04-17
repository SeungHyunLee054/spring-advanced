package org.example.expert.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class PasswordEncoderTest {
	@InjectMocks
	private PasswordEncoder passwordEncoder;

	public static final String RAW_PASSWORD = "testPassword";

	@Test
	@DisplayName("비밀번호 암호화 성공")
	void success_encode() {
		// Given

		// When
		String encodedPassword = passwordEncoder.encode(RAW_PASSWORD);

		// Then
		assertAll(
			() -> assertNotNull(encodedPassword),
			() -> assertNotEquals(RAW_PASSWORD, encodedPassword)
		);

	}

	@Test
	@DisplayName("matches 테스트 성공")
	void success_matches() {
		// given
		String encodedPassword = passwordEncoder.encode(RAW_PASSWORD);

		// when
		boolean matches = passwordEncoder.matches(RAW_PASSWORD, encodedPassword);

		// then
		assertTrue(matches);
	}
}
