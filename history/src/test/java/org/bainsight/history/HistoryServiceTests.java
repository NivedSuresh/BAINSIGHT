package org.bainsight.history;

import org.bainsight.history.Models.Dto.CandleStickDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@AutoConfigureWebTestClient
class HistoryServiceTests {

	@Autowired
	WebTestClient webTestClient;

	@Test
	void testCurrentDay(){

		LocalDateTime now = LocalDateTime.now();
		if(now.getHour() < 9) now = now.minusDays(1);
		if(now.getDayOfWeek() == DayOfWeek.SUNDAY) now = now.minusDays(1);
		LocalDateTime todayOpen = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 9, 0, 0, 0);
		LocalDateTime todayClose = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 15, 30, 0, 0);

		LocalDateTime finalNow = now;
		makeRequest("1d", "aapl").consumeWith(res -> {
			List<CandleStickDto> response = res.getResponseBody();

			LocalDateTime prev = LocalDateTime.of(finalNow.getYear(), finalNow.getMonth(), finalNow.getDayOfMonth(), 9, 0, 0, 0);

			Assertions.assertNotNull(response);

			for(CandleStickDto dto : response){
				LocalDateTime timeStamp = dto.getTimeStamp();
				this.assertTrue(todayClose, todayOpen, timeStamp);
				this.checkDif(prev, timeStamp, 5);
				prev = timeStamp;
			}

		});


	}


	@Test
	void testCurrentWeek(){

		LocalDateTime now = LocalDateTime.now();

		LocalDateTime open = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 9, 0, 0, 0).minusDays(7);
		LocalDateTime close = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 15, 30, 0, 0);

		makeRequest("1w", "aapl").consumeWith(res -> {
			List<CandleStickDto> response = res.getResponseBody();

			LocalDateTime prev = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 8, 30, 0, 0).minusDays(7);

			Assertions.assertNotNull(response);

			for(CandleStickDto dto : response){
				LocalDateTime timeStamp = dto.getTimeStamp();

				if(timeStamp.getDayOfMonth() != prev.getDayOfMonth()){
					prev = LocalDateTime.of(timeStamp.getYear(), timeStamp.getMonth(),  timeStamp.getDayOfMonth(), 8, 30, 0, 0);
				}

				System.out.println(prev);
				System.out.println(dto.getTimeStamp());

				this.assertTrue(close, open, timeStamp);
				this.checkDif(prev, timeStamp, 30);
				prev = timeStamp;
			}

		});

	}



	@Test
	void testCurrentMonth(){

		LocalDateTime now = LocalDateTime.now();

		LocalDateTime open = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 9, 0, 0, 0).minusDays(28);
		LocalDateTime close = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 15, 30, 0, 0);

		makeRequest("1m", "aapl").consumeWith(res -> {
			List<CandleStickDto> response = res.getResponseBody();

			LocalDateTime prev = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 7, 0, 0, 0).minusDays(28);

			Assertions.assertNotNull(response);

			for(CandleStickDto dto : response){
				LocalDateTime timeStamp = dto.getTimeStamp();
				if(timeStamp.getDayOfMonth() != prev.getDayOfMonth()){
					prev = LocalDateTime.of(timeStamp.getYear(), timeStamp.getMonth(),  timeStamp.getDayOfMonth(), 7, 0, 0, 0);
				}
				this.assertTrue(close, open, timeStamp);
				this.checkDif(prev, timeStamp, 120);
				prev = timeStamp;
			}

		});

	}


	@Test
	void testCurrentYear(){

		LocalDateTime now = LocalDateTime.now();

		LocalDateTime open = LocalDateTime.of(
				now.getYear(),
				now.getMonth(),
				now.getDayOfMonth(),
				15,
				30,
				0,
				0).minusYears(1);


		LocalDateTime close = LocalDateTime.of(
				now.getYear(),
				now.getMonth(),
				now.getDayOfMonth(),
				15,
				30,
				0,
				0);

		makeRequest("1y", "aapl").consumeWith(res -> {
			List<CandleStickDto> response = res.getResponseBody();

			LocalDateTime prev = open.minusDays(3);

			Assertions.assertNotNull(response);

			for(CandleStickDto dto : response)
			{

				LocalDateTime timeStamp = dto.getTimeStamp();

				this.assertTrue(close, open, timeStamp);
				this.checkDifInDays(prev, timeStamp, 3);
				prev = timeStamp;
			}

		});

	}

	@Test
	void testThreeYears() {

		LocalDateTime now = LocalDateTime.now();

		LocalDateTime open = LocalDateTime.of(
				now.getYear(),
				now.getMonth(),
				now.getDayOfMonth(),
				15,
				30,
				0,
				0).minusYears(3);


		LocalDateTime close = LocalDateTime.of(
				now.getYear(),
				now.getMonth(),
				now.getDayOfMonth(),
				15,
				30,
				0,
				0);

		makeRequest("3y", "aapl").consumeWith(res -> {
			List<CandleStickDto> response = res.getResponseBody();

			LocalDateTime prev = open.minusDays(15);

			Assertions.assertNotNull(response);


			for (CandleStickDto dto : response) {

				LocalDateTime timeStamp = dto.getTimeStamp();

				this.assertTrue(close, open, timeStamp);
				this.checkDifInDays(prev, timeStamp, 15);
				prev = timeStamp;
			}

		});
	}

	private void checkDifInDays(LocalDateTime prev, LocalDateTime timeStamp, int difIndays) {
		System.out.println("Prev: " + prev);
		System.out.println("Current: " + timeStamp);
		System.out.println();
		Assertions.assertTrue(prev.plusDays(difIndays).isEqual(timeStamp));
	}


	private void checkDif(LocalDateTime prev, LocalDateTime timeStamp, int differenceInMinutes) {
		System.out.println("Prev: " + prev);
		System.out.println("Current: " + timeStamp);
		System.out.println();
		Assertions.assertTrue(prev.plusMinutes(differenceInMinutes).isEqual(timeStamp));
	}

	private void assertTrue(LocalDateTime close, LocalDateTime open, LocalDateTime timeStamp) {
		System.out.println(open);
		System.out.println(timeStamp);
		System.out.println(close + " " + (close.isAfter(timeStamp) || close.isEqual(timeStamp)));
		System.out.println();
		Assertions.assertTrue(timeStamp.isBefore(close) || timeStamp.isEqual(close));
		Assertions.assertTrue(timeStamp.isAfter(open) || timeStamp.isEqual(open));
	}

	WebTestClient.ListBodySpec<CandleStickDto> makeRequest(String timespace, String symbol){
		return webTestClient.get()
				.uri("/api/bainsight/history/{symbol}/{timeSpace}", symbol, timespace)
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(CandleStickDto.class);
	}


}
