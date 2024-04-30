package com.zxxwl.commonweb.utils;

import com.zxxwl.common.utils.pinyin.PinYinUtils;
import com.hankcs.hanlp.HanLP;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
@Slf4j
public class PingTest {
    @Test
    public void t(){
        String p = PinYinUtils.getPinYinHeadChar("你好");
        log.info(p);
        String p2 = HanLP.convertToPinyinFirstCharString("你好", "", false);
        log.info(p2);
    }
}
