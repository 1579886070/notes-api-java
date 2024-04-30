package com.zxxwl.common.api.wx.service;

import com.zxxwl.common.api.sys.thirdaccount.service.SysThirdAccountService;
import com.zxxwl.common.utils.http.WebClientUtil;
import com.zxxwl.config.JsonConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.zxxwl.common.constants.SysThirdAccountContents.WORKWX_TEAM_BOT;

@Slf4j
@RequiredArgsConstructor
@Service
public class WorkWxTeamBotApiServiceImpl implements WorkWxTeamBotApiService {
    private static String url;

    private final WebClientUtil webClientUtil;
    private static final ObjectMapper objectMapper = JsonConfig.getInstance();
    private final SysThirdAccountService sysThirdAccountService;

    @PostConstruct
    private void init() {
        JsonNode content = sysThirdAccountService.queryJsonContentByDataId(WORKWX_TEAM_BOT);
        try {
            url = content.path("url").asText();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean sendText(String content) {
        ObjectNode bodyValue = objectMapper.createObjectNode();
        ObjectNode text = objectMapper.createObjectNode();
        text.put("content", content)
        //.putPOJO("mentioned_list", Collections.emptyList())
        //.putPOJO("mentioned_mobile_list",Collections.emptyList())
        ;
        bodyValue.put("msgtype", "text")
                .putPOJO("text", text)
        ;
        JsonNode post = webClientUtil.post(url, bodyValue);
        if (post.path("errcode").asInt(-1) == 0) {
            return true;
        }
        log.info("{}", post);
        return false;
    }

    @Override
    public boolean sendMarkdown(String content) {
        ObjectNode bodyValue = objectMapper.createObjectNode();
        ObjectNode markdown = objectMapper.createObjectNode();
        markdown.put("content", content)
        ;
        bodyValue.put("msgtype", "markdown")
                .putPOJO("markdown", markdown)
        ;
        JsonNode post = webClientUtil.post(url, bodyValue);
        if (post.path("errcode").asInt(-1) == 0) {
            return true;
        }
        log.info("{}", post);
        return false;
    }
}
