package com.porapipat.porapipat_api.service.util;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static com.porapipat.porapipat_api.constant.Constants.*;

@Service
public class DateUtilService {
    public long convertNanosecondToMillisecond(long nanosecond){
        return TimeUnit.MILLISECONDS.convert(nanosecond, TimeUnit.NANOSECONDS);
    }

    public LocalDateTime convertNanosecondToLocalDateTime(long nanosecond){
        long millisecond = convertNanosecondToMillisecond(nanosecond);
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millisecond), ZoneId.of("+07:00")).truncatedTo(ChronoUnit.SECONDS);
    }


    public String setLocalDateTimeFormat(LocalDateTime localDateTime) {
        return localDateTime == null ? null: localDateTime.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
    }

    public String setLocalDateFormat(LocalDate localDateTime) {
        return localDateTime == null ? null: localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public boolean isInvalidFormatDate(String dateStr) {
        try {
            LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            return true;
        }
        return false;
    }
}
