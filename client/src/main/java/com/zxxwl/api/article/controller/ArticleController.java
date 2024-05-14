package com.zxxwl.api.article.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zxxwl.api.article.entity.Article;
import com.zxxwl.api.article.service.ArticleService;
import com.zxxwl.api.member.service.MemberService;
import com.zxxwl.common.web.http.Response;
import com.zxxwl.web.core.db.QueryBuilder;
import com.zxxwl.web.core.http.Request;
import com.zxxwl.web.core.mvc.BaseController;
import com.zxxwl.web.core.mvc.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * 文章表
 *
 * @author zhouxin
 * @since 2024-04-28 17:44:43
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/article/article")
public class ArticleController extends BaseController<ArticleService> {
    private final ArticleService baseService;

    private final MemberService memberService;

    @Override
    protected QueryBuilder initBuilder(Request request) {
        return super.initBuilder(request);
    }

    @Override
    public ResponseEntity<?> index() {
        if (request.hasQuery("id")) {
            Object detail = baseService.detail(request.getQuery("id").toString());

            Article article = JSON.parseObject(JSONObject.toJSONString(detail), Article.class);
            if (ObjectUtil.isNotNull(detail)) {
                UpdateWrapper<Article> updateWrapper = new UpdateWrapper<>();
                updateWrapper.lambda().set(Article::getPv, article.getPv() + 1).eq(Article::getId, article.getId());
                baseService.update(updateWrapper);
            }
            return Response.ok(true).content(detail, true).send();
        }

        Page<Map<String, Object>> page = baseService.doQuery(initBuilder(request));

        return Response.ok(true).pagination(page).content(page.getRecords()).send();
    }

    @Override
    protected Map<String, Data> beforeAddHook(Map<String, Data> params, Request request) {

        params.put("memberId", new Data(memberService.getMemberId()));
        params.put("pushTime", new Data(Instant.now().toEpochMilli()));
        return super.beforeAddHook(params, request);
    }

}
