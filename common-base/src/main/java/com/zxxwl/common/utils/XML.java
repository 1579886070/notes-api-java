package com.zxxwl.common.utils;

import com.alibaba.fastjson.JSONObject;

/**
 *工具类
 * 用于处理XML
 * @author 杭州帕奇拉科技有限公司
 * @version 2.1.0
 */
public class XML {
    /**
     *将xml字符串转换为JSONObject类
     * @param xml 需要转换的xml字符串
     * @return {@code JSONObject}
     */
    public static JSONObject toJSON(String xml){
        xml = xml.replaceAll("\n", "");
        if( xml.startsWith("<?xml version=") )
            xml = xml.replaceAll("<\\?xml version=.+\\?>", "");

        return JSONObject.parseObject(org.json.XML.toJSONObject(xml).toString());
    }
}
