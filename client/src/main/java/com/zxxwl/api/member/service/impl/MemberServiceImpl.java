package com.zxxwl.api.member.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zxxwl.api.member.entity.Member;
import com.zxxwl.api.member.mapper.MemberMapper;
import com.zxxwl.api.member.service.MemberService;
import com.zxxwl.common.annotation.HeaderTokenCheck;
import com.zxxwl.common.api.redis.RedisService;
import com.zxxwl.common.constants.SysConstants;
import com.zxxwl.common.crypto.Hash;
import com.zxxwl.common.utils.jwt.JwtUtilV2;
import com.zxxwl.common.utils.sys.SysHttpRequestUtil;
import com.zxxwl.common.web.http.Response;
import com.zxxwl.exception.UnauthorizedException;
import com.zxxwl.web.core.db.BaseServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static com.zxxwl.common.constants.RedisConstants.JWT_TOKEN_INFO;

/**
 * 用户信息表
 *
 * @author zhouxin
 * @since 2024-04-28 17:44:17
 */
@Service
@RequiredArgsConstructor
public class MemberServiceImpl extends BaseServiceImpl<MemberMapper, Member> implements MemberService {

    private final HttpServletRequest request;

    private static final int EXPIRE = 30 * 24 * 60 * 60;

    private final RedisService<String> redisService;

    @HeaderTokenCheck
    @Override
    public Response login(JSONObject params) {
        String account = params.getString("account");
        String password = params.getString("password");

        // 如果为空，则返回错误信息
        if (StrUtil.isBlank(account) || StrUtil.isBlank(password)) {
            return Response.fail().message("账号或密码不能为空");
        }

        // 将获取的账号+password 拼接，sha256编码 匹配数据库的密码
        String encryptPassword = Hash.build(SysConstants.SAFETY_VAL + account + password, "SHA-256");

        QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Member::getAccount, account).eq(Member::getPassword, encryptPassword);

        Member member = this.baseMapper.selectOne(queryWrapper);
        if (ObjectUtil.isNull(member)) {
            return Response.fail().message("账号或密码错误");
        }

        String token = this.updateToken(member.getId());

        return Response.ok().content(new JSONObject() {{
            put("token", token);
        }});
    }

    public static void main(String[] args) {
        System.out.println(Hash.build(SysConstants.SAFETY_VAL + "root" + "zhouxin1218..", "SHA-256"));
    }

    public String updateToken(String memberId) {
        String token = JwtUtilV2.create(memberId);
        try {
            // 缓存
            redisService.set(JWT_TOKEN_INFO + memberId, token, EXPIRE);
        } catch (Exception e) {
            log.error("【用户短信登录，缓存存入失败】", e);
        }
        return token;
    }

    @Override
    public String getMemberIdByToken(String token) {
        return JwtUtilV2.getMemberId(token);
    }

    @Override
    public String getMemberId() {
        String headerToken = SysHttpRequestUtil.getUserToken(request);

        return this.getMemberIdByToken(headerToken);
    }

}
