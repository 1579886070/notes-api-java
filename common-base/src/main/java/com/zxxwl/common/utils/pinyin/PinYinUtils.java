package com.zxxwl.common.utils.pinyin;

import com.hankcs.hanlp.HanLP;

/**
 * 拼音工具类
 * @author yujw
 * @author zhouxin
 * @author qingyu 2023.07.10
 * @since 2021/3/22 14:36
 */
public class PinYinUtils {

    /**
     * 获取汉字对应的拼音
     *
     * @param chinese 汉字串
     * @return R
     */
    /*public static String getFullSpell(String chinese) {
        StringBuffer bf = new StringBuffer();

        char[] arr = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 128) {
                try {
                    bf.append(PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat)[0]);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                bf.append(arr[i]);
            }
        }

        return bf.toString();
    }*/
    public static String getFullSpell(String chinese) {
        return HanLP.convertToPinyinString(chinese, "", false);
    }
    public static String getPinYinHeadChar(String str) {
        return HanLP.convertToPinyinFirstCharString(str, "", false);
    }
    /*    public static String getPinYinHeadChar(String str) {
        StringBuilder builder = new StringBuilder();
        for (int j = 0; j < str.length(); j++) {
            char word = str.charAt(j);

            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);

            if (pinyinArray != null && pinyinArray.length > 0)
                builder.append(pinyinArray[0].charAt(0));
            else
                builder.append(word);
        }

        return builder.toString();
    }*/
}
