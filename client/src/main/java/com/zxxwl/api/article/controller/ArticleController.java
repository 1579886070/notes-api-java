package com.zxxwl.api.article.controller;
import com.zxxwl.api.article.service.ArticleService;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import com.zxxwl.web.core.mvc.BaseController;

/**
* 文章表
*
* @author zhouxin
* @since 2024-04-28 17:44:43
*/
@RequiredArgsConstructor
@RestController
@RequestMapping("/article/article")
public class ArticleController extends  BaseController<ArticleService> {
    private final ArticleService baseService;

}
