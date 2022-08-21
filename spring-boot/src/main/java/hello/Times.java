package hello;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Objects;

/** Trying out flexible time parsing with Java 8 */
public class Times {
	/**
	 * Pattern that can read a variety of UTC formats with optional parts.
	 * Has support for numbers with padding or not.
	 */
	private static final DateTimeFormatter MEGA_UTC_PARSER = DateTimeFormatter.ofPattern("[y-M-d]['T']['t'][' '][H:m[:s[.SSS]]][X]['z']");

	/**
	 * Attempts to parse date and/or time from the given string. Assumes UTC if not provided.
	 * @throws DateTimeParseException if unable to parse the input string
	 */
	public static Instant parseUtc(CharSequence dateTimeText) {
		Objects.requireNonNull(dateTimeText, "dateTimeText must not be null");

		// The temporal queries must be ordered from best (most complete) to worst
		TemporalAccessor accessor = MEGA_UTC_PARSER.parseBest(dateTimeText, ZonedDateTime::from, LocalDateTime::from, LocalTime::from, LocalDate::from);

		if (accessor instanceof ZonedDateTime) {
			return ((ZonedDateTime)accessor).toInstant();
		} else if (accessor instanceof LocalDateTime) {
			return ((LocalDateTime)accessor).toInstant(ZoneOffset.UTC);
		} else if (accessor instanceof LocalTime) {
			return ((LocalTime)accessor).atDate(LocalDate.now()).toInstant(ZoneOffset.UTC);
		} else if (accessor instanceof LocalDate) {
			return ((LocalDate)accessor).atStartOfDay().toInstant(ZoneOffset.UTC);
		} else if (accessor != null) {
			// We do not expect to get here
			throw new DateTimeParseException("Unexpected temporal type " + accessor.getClass().getCanonicalName() + " parsed", dateTimeText, 0);
		}

		throw new DateTimeParseException("Failed to parse date/time against expected patterns", dateTimeText, 0);
	}
}
