package com.zxxwl.common.api.sys.log.service;

import com.zxxwl.common.api.sys.log.entity.SysLog;
import com.zxxwl.common.api.sys.log.entity.dto.SysLogAddDTO;
import com.zxxwl.common.api.sys.log.mapper.SysLogMapper;
import com.zxxwl.common.random.IdUtils;
import com.zxxwl.common.utils.http.IpUtil;
import com.zxxwl.common.utils.jwt.JwtUtilV2;
import com.zxxwl.common.utils.sys.SysHttpRequestUtil;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
@Service
public class SysLogServiceImpl implements SysLogService {
    private static final String TYPE_ERROR = "Error";
    private static final String TITLE = "default";
    @Value("${custom.common-web.module-name:Default}")
    private String moduleName;
    private final HttpServletRequest request;
    private final SysLogMapper sysLogMapper;

    @Override
    public boolean add(SysLogAddDTO dto) {
        SysLog sysLog = new SysLog();
        sysLog.setId(dto.getId());
        if (!StringUtils.hasText(dto.getMemberId())) {
            String headerToken = SysHttpRequestUtil.getUserToken(request);
            String memberId = JwtUtilV2.getMemberId(headerToken);
            sysLog.setMemberId(StringUtils.hasText(sysLog.getMemberId()) ? memberId : null);
        } else {
            sysLog.setMemberId(dto.getMemberId());
        }
        sysLog.setTitle(dto.getTitle().substring(0, Math.min(dto.getTitle().length(), 100)));
        sysLog.setType(dto.getType());
        sysLog.setContent(dto.getContent());
        sysLog.setSource(moduleName);

        sysLog.setDf(false);
        sysLog.setCreateTime(Instant.now().toEpochMilli());
        sysLog.setUpdateTime(sysLog.getCreateTime());
        int row = sysLogMapper.add(sysLog);
        return row == 1;
    }

    @Override
    public boolean addApiErrorMsg(String json) {
        SysLogAddDTO sysLogAddDTO = new SysLogAddDTO();
        sysLogAddDTO.setId(IdUtils.getSnowFlakeId());
        sysLogAddDTO.setIp(IpUtil.getIpAddress(request));
        sysLogAddDTO.setContent(json);
        sysLogAddDTO.setTitle(TITLE);
        sysLogAddDTO.setType(TYPE_ERROR);
        return this.add(sysLogAddDTO);
    }

    @Override
    public boolean addApiErrorJsonMsg(JsonNode json) {
        SysLogAddDTO sysLogAddDTO = new SysLogAddDTO();
        sysLogAddDTO.setId(IdUtils.getSnowFlakeId());
        sysLogAddDTO.setIp(IpUtil.getIpAddress(request));
        sysLogAddDTO.setContent(json.toString());
        sysLogAddDTO.setTitle(json.path("msg").asText(TITLE));
        sysLogAddDTO.setType(TYPE_ERROR);
        return this.add(sysLogAddDTO);
    }
}
