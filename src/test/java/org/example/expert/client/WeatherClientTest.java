package org.example.expert.client;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.example.expert.client.dto.WeatherDto;
import org.example.expert.domain.common.exception.ServerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class WeatherClientTest {
	@Mock
	private RestTemplate restTemplate;

	private WeatherClient weatherClient;

	public static final String TODAY = LocalDate.now().format(DateTimeFormatter.ofPattern("MM-dd"));

	private final WeatherDto[] weatherDtos = {new WeatherDto(TODAY, "맑음"),
		new WeatherDto("date", "weather")};

	@BeforeEach
	void setUp() {
		RestTemplateBuilder builder = mock(RestTemplateBuilder.class);
		given(builder.build())
			.willReturn(restTemplate);

		weatherClient = new WeatherClient(builder);
	}

	@Nested
	@DisplayName("오늘 날씨 가져오기 테스트")
	class GetTodayWeatherTest {
		@Test
		@DisplayName("오늘 날씨 가져오기 성공")
		void success_getTodayWeather() {
			// Given
			given(restTemplate.getForEntity(any(), any()))
				.willReturn(ResponseEntity.ok(weatherDtos));

			// When
			String todayWeather = weatherClient.getTodayWeather();

			// Then
			assertAll(
				() -> assertEquals("맑음", todayWeather)
			);

		}

		@Test
		@DisplayName("오늘 날씨 가져오기 실패 - 정상 응답이 아님")
		void fail_getTodayWeather_responseNotOk() {
			// Given
			given(restTemplate.getForEntity(any(), any()))
				.willReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(weatherDtos));

			// When
			ServerException exception = assertThrows(ServerException.class,
				() -> weatherClient.getTodayWeather());

			// Then
			assertAll(
				() -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getHttpStatus()),
				() -> assertEquals("날씨 데이터를 가져오는데 실패했습니다. 상태 코드: 400 BAD_REQUEST",
					exception.getMessage())
			);

		}

		@Test
		@DisplayName("오늘 날씨 가져오기 실패 - 응답 값이 null")
		void fail_getTodayWeather_nullResponseBody() {
			// Given
			given(restTemplate.getForEntity(any(), any()))
				.willReturn(ResponseEntity.ok(null));

			// When
			ServerException exception = assertThrows(ServerException.class,
				() -> weatherClient.getTodayWeather());

			// Then
			assertAll(
				() -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getHttpStatus()),
				() -> assertEquals("날씨 데이터가 없습니다.", exception.getMessage())
			);

		}

		@Test
		@DisplayName("오늘 날씨 가져오기 실패 - 응답 값이 비어있음")
		void fail_getTodayWeather_emptyResponseBody() {
			// Given
			given(restTemplate.getForEntity(any(), any()))
				.willReturn(ResponseEntity.ok(new WeatherDto[]{}));

			// When
			ServerException exception = assertThrows(ServerException.class,
				() -> weatherClient.getTodayWeather());

			// Then
			assertAll(
				() -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getHttpStatus()),
				() -> assertEquals("날씨 데이터가 없습니다.", exception.getMessage())
			);

		}

		@Test
		@DisplayName("오늘 날씨 가져오기 실패 -오늘 날씨를 찾을 수 없음")
		void fail_getTodayWeather_todayWeatherNotFound() {
			// Given
			given(restTemplate.getForEntity(any(), any()))
				.willReturn(ResponseEntity.ok(new WeatherDto[]{new WeatherDto("date", "weather")}));

			// When
			ServerException exception = assertThrows(ServerException.class,
				() -> weatherClient.getTodayWeather());

			// Then
			assertAll(
				() -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getHttpStatus()),
				() -> assertEquals("오늘에 해당하는 날씨 데이터를 찾을 수 없습니다.", exception.getMessage())
			);

		}
	}

}