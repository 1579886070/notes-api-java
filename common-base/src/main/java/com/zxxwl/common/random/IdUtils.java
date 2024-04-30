package com.zxxwl.common.random;

import java.util.Random;
/**
 * ID生成器工具类
 *
 * @author ruoyi
 */
public class IdUtils {
    // 小它数据中心编码1，机器编码1
    public static SnowFlake snowFlake = new SnowFlake(1, 1);

    /**
     * 获取随机UUID
     *
     * @return 随机UUID
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 简化的UUID，去掉了横线
     *
     * @return 简化的UUID，去掉了横线
     */
    public static String simpleUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 获取随机UUID，使用性能更好的ThreadLocalRandom生成UUID
     *
     * @return 随机UUID
     */
    public static String fastUUID() {
        return UUID.fastUUID().toString();
    }

    /**
     * 简化的UUID，去掉了横线，使用性能更好的ThreadLocalRandom生成UUID
     *
     * @return 简化的UUID，去掉了横线
     */
    public static String fastSimpleUUID() {
        return UUID.fastUUID().toString(true);
    }

    /**
     * Duojunrui
     * 生成雪花算法ID
     * 2020-04-18 14:50
     *
     * @return R
     */
    public static String getSnowFlakeId() {
        if (snowFlake == null) {
            snowFlake = new SnowFlake(1, 1);
        }
        long snowId = snowFlake.nextId();
        return Long.toString(snowId);
    }
    public static Long getSnowFlakeNextId() {
        if (snowFlake == null) {
            snowFlake = new SnowFlake(1, 1);
        }
        return snowFlake.nextId();
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            System.out.println(IdUtils.getSnowFlakeId());
        }
    }

    /**
     * 获取指定长度随机数字
     *
     * @param len 长度
     * @return R
     */
    public static String getRandom(int len) {
        StringBuffer code = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < len; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    /**
     * 根据指定长度生成字母和数字的随机数 0~9的ASCII为48~57；；A~Z的ASCII为65~90；a~z的ASCII为97~122
     *
     * @param length 需要的字符长度
     * @return R
     */
    public static String getRandomCharData(int length) {
        StringBuilder sb = new StringBuilder();
        Random rand = new Random();
        Random randData = new Random();
        int data = 0;
        for (int i = 0; i < length; i++) {
            int index = rand.nextInt(2);
            //目的是随机选择生成数字，大小写字母
            switch (index) {
                case 0:
                    //仅仅会生成0~9
                    data = randData.nextInt(10);
                    sb.append(data);
                    break;
                case 1:
                    //保证只会产生65~90之间的整数
                    data = randData.nextInt(26) + 65;
                    sb.append((char) data);
                    break;
            }
        }
        String result = sb.toString();
        return result;
    }
}
