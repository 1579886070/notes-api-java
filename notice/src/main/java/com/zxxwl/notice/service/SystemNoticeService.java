package com.zxxwl.notice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * 通知：企业微信机器人-自建
 * https://developer.work.weixin.qq.com/document/path/91770
 */
@Slf4j
@Service
public class SystemNoticeService {

    private static final String WEBHOOK_URL = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=a53913c5-bdc9-4729-bece-6cc4845595d4";
    private static final String NOTIFICATION_TYPE = "小它优选订单新购通知";
    private static final String TIME = "2024-02-22 10:00:00";
    private static final String NOTIFICATION_CONTENT = "这是一个通知内容的示例";

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendNotice(String webhookUrl, String notificationType, String time, String notificationContent) {
        try {
            String requestBody = "{\"msgtype\":\"markdown\",\"markdown\":{\"content\":\"## 小它系统通知\\n\\n" +
                    "- **类型:** <font color=\\\"#0076D7\\\">" + notificationType + "</font>\\n" +
                    "- **时间:** <font color=\\\"#6A737D\\\">" + time + "</font>\\n\\n" +
                    notificationContent + "\"}}";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(webhookUrl, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("Notification sent successfully!");
            } else {
                log.error("Failed to send notification. Status code: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error occurred while sending notification", e);
        }
    }
}