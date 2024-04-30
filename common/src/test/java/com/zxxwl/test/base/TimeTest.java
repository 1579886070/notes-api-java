package com.zxxwl.test.base;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;

@Slf4j
public class TimeTest {
    @Test
    public void t() {
        LocalDate prevDate = LocalDate.now().minusMonths(1);
        LocalDateTime beg = prevDate.with(TemporalAdjusters.firstDayOfMonth()).atTime(0, 0, 0);
        LocalDateTime end = prevDate.with(TemporalAdjusters.lastDayOfMonth()).atTime(23, 59, 59);
        // 转换为 Instant
        Instant prevDateBeg = beg.atZone(ZoneId.of("GMT+8")).toInstant();
        Instant prevDateEnd = end.atZone(ZoneId.of("GMT+8")).toInstant();

        log.info("prevDate:{}", prevDate);
        log.info("prevDateBeg:{}", prevDate.with(TemporalAdjusters.firstDayOfMonth()));
        log.info("prevDateEnd:{}", prevDate.with(TemporalAdjusters.lastDayOfMonth()));
        log.info("beg:{}", beg);
        log.info("end:{}", end);
        log.info("prevDateBeg:{}", prevDateBeg);
        log.info("prevDateEnd:{}", prevDateEnd);
        log.info("ssss:{}", prevDate.with(TemporalAdjusters.firstDayOfMonth()).atTime(0, 0, 0));
        log.info("ZoneId.systemDefault():{}", ZoneId.systemDefault());
        log.info("ZoneId.systemDefault():{}", ZoneId.of("GMT+8"));
    }

    @Test
    public void t2() {

        LocalDateTime now = LocalDateTime.now();
        ZoneId gmt8 = ZoneId.of("GMT+8");
        ZoneId shanghai = ZoneId.systemDefault();
        LocalDateTime gmt8Now = now.atZone(gmt8).toLocalDateTime();
        LocalDateTime shanghaiNow = now.atZone(shanghai).toLocalDateTime();
        log.info("{}\n{}\n{}", now, gmt8Now, shanghaiNow);
        /*
         * 2023-10-16T13:16:34.356724
         * 2023-10-16T13:16:34.356724
         * 2023-10-16T13:16:34.356724
         */
    }

    @Test
    public void t3() {

        LocalDateTime now = LocalDateTime.now();
        ZoneId shanghai = ZoneId.systemDefault();
        LocalDateTime shanghaiNow = now.atZone(shanghai).toLocalDateTime();
        Instant instant = shanghaiNow.atZone(shanghai).toInstant();
        log.info("{}", instant);
        log.info("{}", instant.toEpochMilli());
        log.info("{}", System.currentTimeMillis());
        log.info("{}", Instant.now().toEpochMilli());
    }

    @Test
    public void t4() {
        Instant prevDateBeg = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant prevDateEnd = LocalDate.now().plusMonths(1).with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant();
        log.info("{}", prevDateBeg);
        log.info("{}", prevDateEnd);
        log.info("{}", prevDateBeg.toEpochMilli());
        log.info("{}", prevDateEnd.toEpochMilli());
    }
}
