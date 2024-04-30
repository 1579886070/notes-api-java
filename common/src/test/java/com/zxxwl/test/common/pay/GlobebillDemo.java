package com.zxxwl.test.common.pay;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
@Slf4j
//本项目只依赖2个库：bcprov-jdk15on-1.65、fastjson-1.2.76
public class GlobebillDemo {


    public static void main(String[] args) {
//        payTest();
        queryTest();
    }

    private static void postAndVerify(JSONObject jo, String api) {
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String json = jo.toJSONString();
        String signVal = Utils.sign256(json, Utils.DevPrivateKey);//SHA256withRSA签名
        log.info("data：{}",json);
        log.info("signVal：{}",signVal);
        String url = Utils.server + api;
        HashMap<String, String> headers = new HashMap<>();
        HashMap<String, String> responseHeaders = new HashMap<>();
        String timestamp = sdf2.format(new Date());
        log.info("signVal：{}",timestamp);

        headers.put("AccessId", "3011");
        headers.put("Timestamp", timestamp);
        headers.put("SignType", Utils.SIGNATURE_ALGORITHM);
        headers.put("SignValue", signVal);
        String respStr = Utils.httpPost(url, json, headers, responseHeaders);
//        System.out.println(responseHeaders.toString());
        String sigValRsp = responseHeaders.get("SignValue");
        if(sigValRsp != null && sigValRsp.length() > 0){
            boolean verify = Utils.verify256(respStr, sigValRsp, Utils.QBPublicKey);
            log.info("gb-验签结果：{}" ,verify);
        }
    }

    private static void payTest() {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
        JSONObject jo = new JSONObject();
        jo.put("sn", "222NBC906777");
        jo.put("tradeAmount", 1);
        jo.put("payModeId", 10042);
        jo.put("outTransId", "GBDEMO" + sdf1.format(new Date()));
        jo.put("payCode", "280254649279871542");
        //jo.put("notifyUrl", "");
        String api = "/qrcode/pay";
        postAndVerify(jo, api);
    }

    private static void queryTest() {
        JSONObject jo = new JSONObject();
        jo.put("outTransId", "GBDEMO20230316170401");
        //jo.put("tradeId", "123400014302");
        //jo.put("appAccessId", 0);//必须为数字，否则接口可能报错
        String api = "/qrcode/query";
        postAndVerify(jo, api);
    }

}
