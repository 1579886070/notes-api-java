package com.zxxwl.common.utils.sms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.Common;
import com.aliyun.teautil.models.RuntimeOptions;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.zxxwl.common.random.IdUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.zxxwl.common.constants.ALYConstants.*;
import static com.zxxwl.common.constants.SmsConstants.*;


/**
 * 短信服务
 *
 * @author zhouxin
 * @author qingyu 2023.09.06
 * @apiNote <a href="https://help.aliyun.com/document_detail/419272.html?spm=a2c4g.419273.0.0.436979besgjloA">发送短信</a>
 * @since 2020/11/2 17:12
 */
@Slf4j
public class SendSmsUtil {
    private static final String ENDPOINT = "dysmsapi.aliyuncs.com";
    private static final String REGION_ID = "cn-hangzhou";
    private static final String SYS_DOMAIN = "dysmsapi.aliyuncs.com";
    private static final String SYS_VERSION = "2017-05-25";
    private static final String SYS_ACTION_SEND_SMS = "SendSms";

    /**
     * 使用AK&SK初始化账号Client
     * @apiNote FIXME 后续可从数据库获取 sysThirdAccount
     * @return Client
     * @throws Exception
     */
    private static Client createClient() throws Exception {
        Config config = new Config()
                // 必填，您的 AccessKey ID
                .setAccessKeyId(ALIBABA_CLOUD_ACCESS_KEY_ID)
                // 必填，您的 AccessKey Secret
                .setAccessKeySecret(ALIBABA_CLOUD_ACCESS_KEY_SECRET)
                .setRegionId(REGION_ID)
                .setEndpoint(ENDPOINT);
        // Endpoint 请参考 https://api.aliyun.com/product/Dysmsapi
        return new Client(config);
    }

    /**
     * @param accessKeyId     accessKeyId
     * @param accessKeySecret accessKeySecret
     * @return Client
     * @throws Exception
     */
    private static Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        Config config = new Config()
                // 必填，您的 AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 必填，您的 AccessKey Secret
                .setAccessKeySecret(accessKeySecret)
                .setRegionId(REGION_ID);
        // Endpoint 请参考 https://api.aliyun.com/product/Dysmsapi
        config.endpoint = ENDPOINT;
        return new Client(config);
    }

    /**
     * 发送短信
     *
     * @param phone         对象手机
     * @param templateCode  templateCode
     * @param templateParam templateParam jsonString
     * @return success
     * @apiNote <a href="https://help.aliyun.com/document_detail/419273.html?spm=a2c4g.419272.0.i0">短信发送</a><a href="https://help.aliyun.com/document_detail/101346.html">短信发送-状态码</a>
     */
    public static boolean send(String phone, String templateCode, String templateParam) {
        return send(phone, SMS_SIGN_NAME_XT, templateCode, templateParam);
    }

    /**
     * 发送短信
     *
     * @param phone         接收短信的手机号码。手机号码格式：
     *                      <p>国内短信：+/+86/0086/86或无任何前缀的11位手机号码，例如1390000****。
     *                      国际/港澳台消息：国际区号+号码，例如852000012****。
     *                      支持对多个手机号码发送短信，手机号码之间以半角逗号（,）分隔。上限为1000个手机号码。批量调用相对于单条调用及时性稍有延迟。
     *                      <p/>
     * @param signName      短信签名
     * @param templateCode  短信模板CODE
     * @param templateParam 短信模板变量对应的实际值。Json String 支持传入多个参数，示例：{"name":"张三","number":"1390000****"}
     * @return success
     * @apiNote <a href="https://help.aliyun.com/document_detail/419273.html?spm=a2c4g.419272.0.i0">短信发送</a><a href="https://help.aliyun.com/document_detail/101346.html">短信发送-状态码</a>
     */
    public static boolean send(String phone, String signName, String templateCode, String templateParam) {
        // 请确保代码运行环境设置了环境变量 ALIBABA_CLOUD_ACCESS_KEY_ID 和 ALIBABA_CLOUD_ACCESS_KEY_SECRET。
        // 工程代码泄露可能会导致 AccessKey 泄露，并威胁账号下所有资源的安全性。以下代码示例使用环境变量获取 AccessKey 的方式进行调用，仅供参考，建议使用更安全的 STS 方式，更多鉴权访问方式请参见：https://help.aliyun.com/document_detail/378657.html
        Client client = null;
        try {
            client = SendSmsUtil.createClient();
        } catch (Exception e) {
            log.error("", e);
        }
        if (client == null) {
            return false;
        }
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setSignName(signName)
                .setPhoneNumbers(phone)
                .setTemplateCode(templateCode)
                .setTemplateParam(templateParam);
        SendSmsResponse sendSmsResponse = null;
        AtomicBoolean success = new AtomicBoolean(false);
        try {
            // 复制代码运行请自行打印 API 的返回值
            sendSmsResponse = client.sendSmsWithOptions(sendSmsRequest, new RuntimeOptions());
        } catch (TeaException error) {
            // 如有需要，请打印 error
            log.error("send sms error:{}", Common.assertAsString(error.message));
            success.set(false);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            // 如有需要，请打印 error
            log.error("send sms error:{}", Common.assertAsString(error.message));
            success.set(false);
        } finally {
            if (sendSmsResponse != null) {
                // 短信发送 请求成功
                success.set(SMS_SEND_SUCCESS.equals(sendSmsResponse.getBody().code));
            }
        }
        return success.get();
    }

    /**
     * 发送短信
     *
     * @param phone 手机号
     * @param type  类型
     * @return R
     */
    @Deprecated
    public static String send(String phone, int type, String templateCode) {
        DefaultProfile profile = DefaultProfile.getProfile(REGION_ID, SMS_ACCESS_KEY, SMS_ACCESS_SECRET);
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain(SYS_DOMAIN);
        request.setSysVersion(SYS_VERSION);
        request.setSysAction(SYS_ACTION_SEND_SMS);
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("RegionId", SMS_REGION_ID);

        if (type == NEARBY_GROUP || type == WITHDRAW_FAIL_NOTICE || type == NEARBY_NA_AI_SI_NOTICE) {
            request.putQueryParameter("SignName", SMS_SIGN_NAME_LLT);
        } else {
            request.putQueryParameter("SignName", SMS_SIGN_NAME_XT);
        }

        request.putQueryParameter("TemplateCode", templateCode);
        CommonResponse response = new CommonResponse();

        if (type == PHONE) {
            Map<String, String> map = new HashMap<>(1);
            String code = IdUtils.getRandom(6);
            map.put("code", code);
            request.putQueryParameter("TemplateParam", JSON.toJSONString(map));

            try {
                response = client.getCommonResponse(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
            JSONObject json = JSONObject.parseObject(response.getData());

            if (json.getString("Code").equals("OK")) {
                return code;
            }

            log.error("【验证码发送失败，手机号：[{}]，templateCode：[{}]，异常信息：[{}]】", phone, templateCode, response.getData());

            return null;
        } else if (type == WITHDRAW_FAIL_NOTICE) {
            try {
                response = client.getCommonResponse(request);

                JSONObject json = JSONObject.parseObject(response.getData());

                if (json.getString("Code").equals("OK")) {
                    return "";
                }

                log.error("【遛遛它提现通知发送失败，手机号：[{}]，templateCode：[{}]，异常信息：[{}]】", phone, templateCode, response.getData());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        } else if (type == NEARBY_GROUP) {
            try {
                response = client.getCommonResponse(request);

                JSONObject json = JSONObject.parseObject(response.getData());

                if (json.getString("Code").equals("OK")) {
                    return "";
                }

                log.error("【遛遛它队伍提醒通知失败，手机号：[{}]，templateCode：[{}]，异常信息：[{}]】", phone, templateCode, response.getData());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        } else if (type == NEARBY_NA_AI_SI_NOTICE) {
            try {
                response = client.getCommonResponse(request);

                JSONObject json = JSONObject.parseObject(response.getData());

                if (json.getString("Code").equals("OK")) {
                    return "";
                }

                log.error("【遛遛它&纳爱斯活动推广通知失败，手机号：[{}]，templateCode：[{}]，异常信息：[{}]】", phone, templateCode, response.getData());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        } else if (type == NEARBY_ME_WA_NOTICE) {
            try {
                response = client.getCommonResponse(request);

                JSONObject json = JSONObject.parseObject(response.getData());

                if (json.getString("Code").equals("OK")) {
                    return "";
                }

                log.error("【遛遛它&萌娃宠物村推广通知失败，手机号：[{}]，templateCode：[{}]，异常信息：[{}]】", phone, templateCode, response.getData());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        } else if (type == NEARBY_YC_Z_NOTICE) {
            try {
                response = client.getCommonResponse(request);

                JSONObject json = JSONObject.parseObject(response.getData());

                if (json.getString("Code").equals("OK")) {
                    return "";
                }

                log.error("【遛遛它&淘豆王国亚宠展推广通知失败，手机号：[{}]，templateCode：[{}]，异常信息：[{}]】", phone, templateCode, response.getData());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        } else if (type == NEARBY_XianLin_NOTICE) {
            try {
                response = client.getCommonResponse(request);

                JSONObject json = JSONObject.parseObject(response.getData());

                if (json.getString("Code").equals("OK")) {
                    return "";
                }

                log.error("【遛遛它&闲林萌宠节推广通知失败，手机号：[{}]，templateCode：[{}]，异常信息：[{}]】", phone, templateCode, response.getData());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        } else if (type == NEARBY_TAO_DAO) {
            try {
                response = client.getCommonResponse(request);

                JSONObject json = JSONObject.parseObject(response.getData());

                if (json.getString("Code").equals("OK")) {
                    return "";
                }

                log.error("【淘豆推广通知失败，手机号：[{}]，templateCode：[{}]，异常信息：[{}]】", phone, templateCode, response.getData());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        return null;
    }

    /**
     * 号码认证
     *
     * @param token app端SDK获取的登录token
     * @param outId 外部流水号
     * @return R
     */
    public static String phoneAuth(String token, String outId) {
       /* DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", PHONE_AUTH_ACCESS_KEY, PHONE_AUTH_ACCESS_SECRET);
        IAcsClient client = new DefaultAcsClient(profile);

        GetMobileRequest request = new GetMobileRequest();
        request.setRegionId("cn-hangzhou");
        request.setAccessToken(token);
        request.setOutId(outId);

        GetMobileResponse response = null;
        try {
            response = client.getAcsResponse(request);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject json = JSONObject.parseObject(new Gson().toJson(response));

        if (json.getString("Code").equals("OK")) {
            return json.getString("Mobile");
        }*/

        return null;
    }

    public static String onlineNoticeSend(String phone, String templateCode, String name) {
        /*DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", SMS_ACCESS_KEY, SMS_ACCESS_SECRET);
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("RegionId", SMS_REGION_ID);

        request.putQueryParameter("SignName", SMS_SIGN_NAME_LLT);


        request.putQueryParameter("TemplateCode", templateCode);
        CommonResponse response = new CommonResponse();

        Map<String, String> map = new HashMap<>(1);
        //活动名称

        map.put("name", name);
        request.putQueryParameter("TemplateParam", JSON.toJSONString(map));

        try {
            response = client.getCommonResponse(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject json = JSONObject.parseObject(response.getData());

        if (json.getString("Code").equals("OK")) {
            return name;
        }

        log.error("【验证码发送失败，手机号：[{}]，templateCode：[{}]，异常信息：[{}]】", name, templateCode, response.getData());*/

        return null;
    }

    public static String activityExchangeFinish(String phone, String templateCode, String name) {
        /*DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", SMS_ACCESS_KEY, SMS_ACCESS_SECRET);
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("RegionId", SMS_REGION_ID);

        request.putQueryParameter("SignName", SMS_SIGN_NAME_LLT);


        request.putQueryParameter("TemplateCode", templateCode);
        CommonResponse response = new CommonResponse();

        Map<String, String> map = new HashMap<>(1);
        //活动名称

        map.put("name", name);
        request.putQueryParameter("TemplateParam", JSON.toJSONString(map));

        try {
            response = client.getCommonResponse(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject json = JSONObject.parseObject(response.getData());

        if (json.getString("Code").equals("OK")) {
            return name;
        }

        log.error("【验证码发送失败，手机号：[{}]，templateCode：[{}]，异常信息：[{}]】", name, templateCode, response.getData());*/

        return null;
    }

    /**
     * 积分清零短信通知
     *
     * @param phone        手机号
     * @param templateCode 阿里云短信 sms_code
     * @param name         名称
     * @param integral     积分
     * @return R
     */
    public static String integralClearNotification(String phone, String templateCode, String name, int integral) {
        /*DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", SMS_ACCESS_KEY, SMS_ACCESS_SECRET);
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("RegionId", SMS_REGION_ID);

        request.putQueryParameter("SignName", SMS_SIGN_NAME_LLT);


        request.putQueryParameter("TemplateCode", templateCode);
        CommonResponse response = new CommonResponse();

        Map<String, String> map = new HashMap<>(1);
        //活动名称

        map.put("name", name);
        map.put("integral", String.valueOf(integral));
        request.putQueryParameter("TemplateParam", JSON.toJSONString(map));

        try {
            response = client.getCommonResponse(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject json = JSONObject.parseObject(response.getData());

        if (json.getString("Code").equals("OK")) {
            return name;
        }

        log.error("【验证码发送失败，手机号：[{}]，templateCode：[{}]，异常信息：[{}]】", name, templateCode, response.getData());*/

        return null;
    }


    /**
     * 积分清零短信通知
     *
     * @param phone       手机号
     * @param gameTitle   赛事名称
     * @param awardsTitle 奖项名称
     * @return R
     */
    public static boolean gameHonorNotify(String phone, String gameTitle, String awardsTitle) {
        /*DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", SMS_ACCESS_KEY, SMS_ACCESS_SECRET);
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("RegionId", SMS_REGION_ID);

        request.putQueryParameter("SignName", SMS_SIGN_NAME_LLT);


        request.putQueryParameter("TemplateCode", SMS_CODE_GAME_HONOR);
        CommonResponse response = new CommonResponse();

        Map<String, String> map = new HashMap<>(1);
        //活动名称

        map.put("title", gameTitle);
        map.put("name", awardsTitle);
        request.putQueryParameter("TemplateParam", JSON.toJSONString(map));

        try {
            response = client.getCommonResponse(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject json = JSONObject.parseObject(response.getData());

        if (json.getString("Code").equals("OK")) {
            return true;
        }

        log.error("【短信发送失败，手机号：[{}]，templateCode：[{}]，异常信息：[{}]】", phone, SMS_CODE_GAME_HONOR, response.getData());*/

        return false;
    }

    /**
     * 入团审核结果通知
     *
     * @param phone     申请人手机号
     * @param teamTitle 团名
     * @param isPass    是否通过
     * @return ok
     */
    public static boolean teamJoinAuditNotify(String phone, String teamTitle, Boolean isPass) {
        /*DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", SMS_ACCESS_KEY, SMS_ACCESS_SECRET);
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("RegionId", SMS_REGION_ID);
        request.putQueryParameter("SignName", SMS_SIGN_NAME_LLT);

        request.putQueryParameter("TemplateCode", isPass ? SMS_CODE_TEAM_JOIN_AUDIT_PASS : SMS_CODE_TEAM_JOIN_AUDIT_NOT_PASS);
        CommonResponse response = new CommonResponse();

        Map<String, String> map = new HashMap<>(1);
        //活动名称
        map.put("name", teamTitle);
        request.putQueryParameter("TemplateParam", JSON.toJSONString(map));

        try {
            response = client.getCommonResponse(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject json = JSONObject.parseObject(response.getData());

        if (json.getString("Code").equals("OK")) {
            return true;
        }

        log.error("【短信发送失败，手机号：[{}]，templateCode：[{}]，异常信息：[{}]】", phone, SMS_CODE_GAME_HONOR, response.getData());*/

        return false;
    }
}
