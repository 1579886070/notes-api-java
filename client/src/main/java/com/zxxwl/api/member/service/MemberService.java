package com.zxxwl.api.member.service;

import com.alibaba.fastjson.JSONObject;
import com.zxxwl.api.member.entity.Member;
import com.zxxwl.common.web.http.Response;
import com.zxxwl.web.core.db.BaseService;
/**
 * 用户信息表
 *
 * @author zhouxin
 * @since 2024-04-28 17:44:17
 */
public interface MemberService extends BaseService<Member> {

    Response login(JSONObject params);

    String getMemberIdByToken(String token);

    String getMemberId();
}
