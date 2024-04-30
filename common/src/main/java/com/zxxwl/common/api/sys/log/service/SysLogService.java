package com.zxxwl.common.api.sys.log.service;

import com.zxxwl.common.api.sys.log.entity.dto.SysLogAddDTO;
import com.fasterxml.jackson.databind.JsonNode;

public interface SysLogService {
    boolean add(SysLogAddDTO dto);
    boolean addApiErrorMsg(String json);
    boolean addApiErrorJsonMsg(JsonNode json);
}
