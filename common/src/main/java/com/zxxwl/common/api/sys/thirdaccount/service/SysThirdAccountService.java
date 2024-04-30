package com.zxxwl.common.api.sys.thirdaccount.service;

import com.zxxwl.common.api.sys.thirdaccount.entity.dto.SysThirdAccountAddDto;
import com.zxxwl.common.api.sys.thirdaccount.entity.dto.SysThirdAccountDto;
import com.fasterxml.jackson.databind.JsonNode;

public interface SysThirdAccountService {

    /**
     * 新增
     *
     * @param dto SysThirdAccountAddDto
     * @return R
     */
    boolean add(SysThirdAccountAddDto dto);

    /**
     * 查询配置 by dataId
     *
     * @param dataId 配置id
     * @return SysThirdAccountDto
     */
    SysThirdAccountDto queryByDataId(String dataId);

    /**
     * 查询json内容 by dataId
     *
     * @param dataId dataId
     * @return JsonNode
     */
    JsonNode queryJsonContentByDataId(String dataId);

    /**
     * 查询微信小程序 appid
     *
     * @param platformId 平台
     * @return appid
     */
    String queryWxAppIdByPlatformId(String platformId);

}
