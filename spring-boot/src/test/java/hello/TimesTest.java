package hello;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimesTest {
	@Test
	public void testParseFull() {
		Instant result = Times.parseUtc("2021-12-17T13:21:36.476Z");
		ZonedDateTime check = result.atZone(ZoneId.of("UTC"));
		assertEquals(2021, check.getYear());
		assertEquals(Month.DECEMBER, check.getMonth());
		assertEquals(17, check.getDayOfMonth());
		assertEquals(13, check.getHour());
		assertEquals(21, check.getMinute());
		assertEquals(36, check.getSecond());
		assertEquals(476, check.get(ChronoField.MILLI_OF_SECOND));
		assertEquals(ZoneId.of("UTC"), check.getZone());
	}

	@Test
	public void testParseFullLowercase() {
		Instant result = Times.parseUtc("2021-12-17t13:21:36.476z");
		ZonedDateTime check = result.atZone(ZoneId.of("UTC"));
		assertEquals(2021, check.getYear());
		assertEquals(Month.DECEMBER, check.getMonth());
		assertEquals(17, check.getDayOfMonth());
		assertEquals(13, check.getHour());
		assertEquals(21, check.getMinute());
		assertEquals(36, check.getSecond());
		assertEquals(476, check.get(ChronoField.MILLI_OF_SECOND));
		assertEquals(ZoneId.of("UTC"), check.getZone());
	}

	@Test
	public void testParseFullSpace() {
		Instant result = Times.parseUtc("2021-12-17 13:21:36.476");
		ZonedDateTime check = result.atZone(ZoneId.of("UTC"));
		assertEquals(2021, check.getYear());
		assertEquals(Month.DECEMBER, check.getMonth());
		assertEquals(17, check.getDayOfMonth());
		assertEquals(13, check.getHour());
		assertEquals(21, check.getMinute());
		assertEquals(36, check.getSecond());
		assertEquals(476, check.get(ChronoField.MILLI_OF_SECOND));
		assertEquals(ZoneId.of("UTC"), check.getZone());
	}

	@Test
	public void testParseDateOnly() {
		Instant result = Times.parseUtc("2021-12-17");
		ZonedDateTime check = result.atZone(ZoneId.of("UTC"));
		assertEquals(2021, check.getYear());
		assertEquals(Month.DECEMBER, check.getMonth());
		assertEquals(17, check.getDayOfMonth());
		assertEquals(0, check.getHour());
		assertEquals(0, check.getMinute());
		assertEquals(0, check.getSecond());
		assertEquals(0, check.get(ChronoField.MILLI_OF_SECOND));
		assertEquals(ZoneId.of("UTC"), check.getZone());
	}

	@Test
	public void testParseDateOnlySingleDigits() {
		Instant result = Times.parseUtc("2021-2-7");
		ZonedDateTime check = result.atZone(ZoneId.of("UTC"));
		assertEquals(2021, check.getYear());
		assertEquals(Month.FEBRUARY, check.getMonth());
		assertEquals(7, check.getDayOfMonth());
		assertEquals(0, check.getHour());
		assertEquals(0, check.getMinute());
		assertEquals(0, check.getSecond());
		assertEquals(0, check.get(ChronoField.MILLI_OF_SECOND));
		assertEquals(ZoneId.of("UTC"), check.getZone());
	}

	@Test
	public void testParseDateOnlyZeroPadded() {
		Instant result = Times.parseUtc("2021-02-07");
		ZonedDateTime check = result.atZone(ZoneId.of("UTC"));
		assertEquals(2021, check.getYear());
		assertEquals(Month.FEBRUARY, check.getMonth());
		assertEquals(7, check.getDayOfMonth());
		assertEquals(0, check.getHour());
		assertEquals(0, check.getMinute());
		assertEquals(0, check.getSecond());
		assertEquals(0, check.get(ChronoField.MILLI_OF_SECOND));
		assertEquals(ZoneId.of("UTC"), check.getZone());
	}

	@Test
	public void testParseTimeOnlyHoursMinuteSecondMillisecond() {
		Instant result = Times.parseUtc("13:21:36.476");
		ZonedDateTime expectedDate = ZonedDateTime.now();
		ZonedDateTime check = result.atZone(ZoneId.of("UTC"));
		assertEquals(expectedDate.getYear(), check.getYear());
		assertEquals(expectedDate.getMonth(), check.getMonth());
		assertEquals(expectedDate.getDayOfMonth(), check.getDayOfMonth());
		assertEquals(13, check.getHour());
		assertEquals(21, check.getMinute());
		assertEquals(36, check.getSecond());
		assertEquals(476, check.get(ChronoField.MILLI_OF_SECOND));
		assertEquals(ZoneId.of("UTC"), check.getZone());
	}

	@Test
	public void testParseTimeOnlyHoursMinuteSecond() {
		Instant result = Times.parseUtc("13:21:36");
		ZonedDateTime expectedDate = ZonedDateTime.now();
		ZonedDateTime check = result.atZone(ZoneId.of("UTC"));
		assertEquals(expectedDate.getYear(), check.getYear());
		assertEquals(expectedDate.getMonth(), check.getMonth());
		assertEquals(expectedDate.getDayOfMonth(), check.getDayOfMonth());
		assertEquals(13, check.getHour());
		assertEquals(21, check.getMinute());
		assertEquals(36, check.getSecond());
		assertEquals(0, check.get(ChronoField.MILLI_OF_SECOND));
		assertEquals(ZoneId.of("UTC"), check.getZone());
	}

	@Test
	public void testParseTimeOnlyHoursMinute() {
		Instant result = Times.parseUtc("13:21");
		ZonedDateTime expectedDate = ZonedDateTime.now();
		ZonedDateTime check = result.atZone(ZoneId.of("UTC"));
		assertEquals(expectedDate.getYear(), check.getYear());
		assertEquals(expectedDate.getMonth(), check.getMonth());
		assertEquals(expectedDate.getDayOfMonth(), check.getDayOfMonth());
		assertEquals(13, check.getHour());
		assertEquals(21, check.getMinute());
		assertEquals(0, check.getSecond());
		assertEquals(0, check.get(ChronoField.MILLI_OF_SECOND));
		assertEquals(ZoneId.of("UTC"), check.getZone());
	}

	@Test
	public void testParseTimeOnlyHoursMinuteSingleValue() {
		Instant result = Times.parseUtc("3:2");
		ZonedDateTime expectedDate = ZonedDateTime.now();
		ZonedDateTime check = result.atZone(ZoneId.of("UTC"));
		assertEquals(expectedDate.getYear(), check.getYear());
		assertEquals(expectedDate.getMonth(), check.getMonth());
		assertEquals(expectedDate.getDayOfMonth(), check.getDayOfMonth());
		assertEquals(3, check.getHour());
		assertEquals(2, check.getMinute());
		assertEquals(0, check.getSecond());
		assertEquals(0, check.get(ChronoField.MILLI_OF_SECOND));
		assertEquals(ZoneId.of("UTC"), check.getZone());
	}

	@Test
	public void testParseDateAndHoursMinute() {
		Instant result = Times.parseUtc("2021-12-17T13:21Z");
		ZonedDateTime check = result.atZone(ZoneId.of("UTC"));
		assertEquals(2021, check.getYear());
		assertEquals(Month.DECEMBER, check.getMonth());
		assertEquals(17, check.getDayOfMonth());
		assertEquals(13, check.getHour());
		assertEquals(21, check.getMinute());
		assertEquals(0, check.getSecond());
		assertEquals(0, check.get(ChronoField.MILLI_OF_SECOND));
		assertEquals(ZoneId.of("UTC"), check.getZone());
	}

	@Test
	public void testParseDateAndHoursMinuteSpace() {
		Instant result = Times.parseUtc("2021-12-17 13:21");
		ZonedDateTime check = result.atZone(ZoneId.of("UTC"));
		assertEquals(2021, check.getYear());
		assertEquals(Month.DECEMBER, check.getMonth());
		assertEquals(17, check.getDayOfMonth());
		assertEquals(13, check.getHour());
		assertEquals(21, check.getMinute());
		assertEquals(0, check.getSecond());
		assertEquals(0, check.get(ChronoField.MILLI_OF_SECOND));
		assertEquals(ZoneId.of("UTC"), check.getZone());
	}
}
