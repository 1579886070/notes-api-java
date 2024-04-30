package com.zxxwl.login;

import com.zxxwl.common.api.sys.thirdaccount.entity.dto.SysThirdAccountDto;
import com.zxxwl.common.api.sys.thirdaccount.service.SysThirdAccountService;
import com.zxxwl.common.api.wx.service.WxOpenApiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.zxxwl.common.constants.SysThirdAccountContents.ID_XT_FW_CLIENT_WX_MINI;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest(classes = LoginMainApp.class)
public class WxTest {

    @Autowired
    private SysThirdAccountService sysThirdAccountService;
    @Autowired
    private WxOpenApiService wxOpenApiService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void t() {
        SysThirdAccountDto sysThirdAccountDto = sysThirdAccountService.queryByDataId(ID_XT_FW_CLIENT_WX_MINI);
        JsonNode wxAccount = objectMapper.convertValue(sysThirdAccountDto.getContent(), JsonNode.class);
        String accessToken = wxOpenApiService.getAccessToken(wxAccount.path("appid").asText(), wxAccount.path("secret").asText());

    }
}

