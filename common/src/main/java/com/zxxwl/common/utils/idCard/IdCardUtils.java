package com.zxxwl.common.utils.idCard;

import com.alibaba.fastjson.JSONObject;

import com.zxxwl.common.constants.ALYConstants;

import com.zxxwl.common.utils.http.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mxy
 * @since 2021/1/15 18:33
 */
@Slf4j
public class IdCardUtils {

//    private static Logger log = LoggerFactory.getLogger(IdCardUtils.class);

    /**
     * https://market.aliyun.com/products/57126001/cmapi00039906.html?spm=5176.2020520132.101.8.1cc77218dm0E6L#sku=yuncode3390600001
     * 身份证号+姓名识别
     *
     * @param name   姓名
     * @param idCard 身份证号
     * @return R
     */
    public static boolean getStatus(String name, String idCard) {
        log.info("【身份证校验，名称：[{}]，身份证号码：[{}]】", name, idCard);

        Map<String, String> headers = new HashMap<>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
       // headers.put("Authorization", "APPCODE " + ALYConstants.ID_CARD_APPCODE);
        Map<String, String> params = new HashMap<>();
        params.put("idCardNo", idCard);
        params.put("name", name);
        String result = null;

        try {
            //获取response的body
            HttpResponse response = HttpUtils.doGet(ALYConstants.MARKET_ID_CARD_HOST, ALYConstants.MARKET_ID_CARD_PATH, headers, params);
            result = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject jsonData = JSONObject.parseObject(result);
        if (jsonData.getInteger("code") == 200) {
            if (jsonData.getJSONObject("data").getInteger("result") == 0) {
                log.info("【身份证校验返回正确，名称：[{}]，身份证号码：[{}]，返回信息：[{}]】", name, idCard, jsonData);

                return true;
            }
        }

        log.error("【身份证校验返回错误，名称：[{}]，身份证号码：[{}]，错误信息：[{}]】", name, idCard, jsonData);

        return false;
    }




    /**
     * https://market.aliyun.com/products/57126001/cmapi033761.html?spm=5176.2020520132.101.2.1cc77218dm0E6L#sku=yuncode2776100001
     * 图片地址识别
     *
     * @param url 图片地址
     * @return R
     */
    public static JSONObject getIdCardByImage(String url) {
        Map<String, String> headers = new HashMap<>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + "937dd15203f043eb86f4ee327f8f5ad5");

        Map<String, Object> params = new HashMap<>();
        params.put("url", url);

        String result = HttpUtils.sendPostForm("https://idcardocrc.shumaidata.com/getidcardocrc", headers, params);
        if (result == null) {
            return null;
        }

        JSONObject jsonData = JSONObject.parseObject(result);
        if (jsonData.getInteger("code") == 200) {
            JSONObject data = jsonData.getJSONObject("data");
            if (data.getInteger("result") == 0) {
                return data;
            }
            return null;
        }

        return null;
    }

    public static void main(String[] args) {
        System.out.println(getStatus("周信", "430121199912180410"));
    }

}
