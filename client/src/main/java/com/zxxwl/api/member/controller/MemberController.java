package com.zxxwl.api.member.controller;
import com.alibaba.fastjson.JSONObject;
import com.zxxwl.api.member.service.MemberService;
import com.zxxwl.common.annotation.ApiReqLimit;
import com.zxxwl.common.annotation.HeaderTokenCheck;
import com.zxxwl.common.web.http.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import com.zxxwl.web.core.mvc.BaseController;
import org.springframework.web.multipart.MultipartFile;

/**
* 用户信息表
*
* @author zhouxin
* @since 2024-04-28 17:44:17
*/
@RequiredArgsConstructor
@RestController
@RequestMapping("/member/member")
public class MemberController extends  BaseController<MemberService> {
    private final MemberService baseService;

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody JSONObject params) {
        Response result = baseService.login(params);

        return result.send();
    }

}
