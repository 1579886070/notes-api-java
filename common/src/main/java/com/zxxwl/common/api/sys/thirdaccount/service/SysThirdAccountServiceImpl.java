package com.zxxwl.common.api.sys.thirdaccount.service;

import com.zxxwl.common.api.sys.thirdaccount.entity.SysThirdAccount;
import com.zxxwl.common.api.sys.thirdaccount.entity.dto.SysThirdAccountAddDto;
import com.zxxwl.common.api.sys.thirdaccount.entity.dto.SysThirdAccountDto;
import com.zxxwl.common.api.sys.thirdaccount.mapper.SysThirdAccountMapper;
import com.zxxwl.common.random.IdUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.Map;

import static com.zxxwl.common.constants.SysThirdAccountContents.*;

/**
 * 第三方 openApi 服务
 *
 * @author qingyu
 * @apiNote 第三方服务配置  基础增删改查
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SysThirdAccountServiceImpl implements SysThirdAccountService {
    private final SysThirdAccountMapper baseMapper;

    @Override
    public boolean add(SysThirdAccountAddDto dto) {
        if (!TYPE_SUPPORT.contains(dto.getType())) {
            log.warn("不支持该类型：{}", dto.getType());
            return false;
        }
        if (!CATEGORY_SUPPORT.contains(dto.getCategory())) {
            log.warn("不支持该类目：{}", dto.getCategory());
            return false;
        }
        SysThirdAccount account = new SysThirdAccount();
        account.setId(IdUtils.getSnowFlakeId());
        account.setDataId(dto.getDataId());
        account.setContent(dto.getContent());
        account.setType(dto.getType());
        account.setCategory(dto.getCategory());
        account.setBrief(dto.getBrief());
        account.setExpiresTime(dto.getExpiresTime());
        account.setDf(false);
        account.setCreateTime(Instant.now().toEpochMilli());
        account.setUpdateTime(Instant.now().toEpochMilli());
        int row = baseMapper.add(account);
        return row == 1;
    }

    @Override
    public SysThirdAccountDto queryByDataId(String dataId) {
        if (!SOURCE_IDS_SUPPORT.contains(dataId)) {
            // 不支持
            throw new RuntimeException("不支持的第三方账号 dataId:" + dataId);
        }
        return baseMapper.queryByDataId(dataId);
    }

    @Override
    public JsonNode queryJsonContentByDataId(String dataId) {
        if (!SOURCE_IDS_SUPPORT.contains(dataId)) {
            // 不支持
            throw new RuntimeException("不支持的第三方账号 dataId:" + dataId);
        }
        Map<String, JsonNode> jsonNodeMap = baseMapper.queryJsonContentByDataId(dataId);
        if (CollectionUtils.isEmpty(jsonNodeMap) || Boolean.FALSE.equals(jsonNodeMap.containsKey("content"))) {
            return NullNode.getInstance();
        }
        return jsonNodeMap.get("content");
    }

    @Override
    public String queryWxAppIdByPlatformId(String platformId) {
        return baseMapper.queryWxAppIdByPlatformId(platformId);
    }
}
