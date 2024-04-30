package com.zxxwl.common.api.sys.thirdaccount.mapper;

import com.zxxwl.common.api.sys.thirdaccount.entity.SysThirdAccount;
import com.zxxwl.common.api.sys.thirdaccount.entity.dto.SysThirdAccountDto;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;


public interface SysThirdAccountMapper {
    /**
     * 新增
     *
     * @param data SysThirdAccount
     * @return row
     */
    int add(@Param("data") SysThirdAccount data);

    /**
     * 查询配置 by dataId
     *
     * @param dataId 配置id
     * @return SysThirdAccountDto
     */
    SysThirdAccountDto queryByDataId(@Param("dataId") String dataId);

    Map<String, JsonNode> queryJsonContentByDataId(@Param("dataId") String dataId);

    /**
     * 查询微信小程序 appid
     *
     * @param platformId 平台
     * @return appid
     */
    @Select("SELECT JSON_UNQUOTE(content->>'$.appid') FROM sys_third_account WHERE data_id=#{platformId} AND df=0")
    String queryWxAppIdByPlatformId(@Param("platformId") String platformId);
}
