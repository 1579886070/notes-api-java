package com.zxxwl.test.base;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
public class BigDecimalTest {
    @Test
    public void t() {
        BigDecimal a1 = BigDecimal.valueOf(1.1D);
        BigDecimal a2 = BigDecimal.valueOf(1.2D);
        BigDecimal a3 = BigDecimal.valueOf(0.0D);
        log.info("{}", a1.compareTo(a2));
        log.info("{}", a1.compareTo(a3));
    }

    @Test
    public void t2() {
        BigDecimal divide = BigDecimal.valueOf(76).divide(BigDecimal.valueOf(10), 1, RoundingMode.DOWN);
        log.info("{}", divide);
        BigDecimal divide1 = divide.divide(BigDecimal.valueOf(2), 1, RoundingMode.DOWN);
        log.info("{}", divide1);
    }

}
