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

	@Test
	@DisplayName("matches 테스트 성공")
	void success_matches() {
		// given
		String rawPassword = "testPassword";
		String encodedPassword = passwordEncoder.encode(rawPassword);

		// when
		boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);

		// then
		assertTrue(matches);
	}
}
