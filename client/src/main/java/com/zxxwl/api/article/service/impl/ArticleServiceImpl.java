package com.zxxwl.api.article.service.impl;

import com.zxxwl.api.article.entity.Article;
import com.zxxwl.api.article.mapper.ArticleMapper;
import com.zxxwl.api.article.service.ArticleService;
import com.zxxwl.web.core.db.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
* 文章表
*
* @author zhouxin
* @since 2024-04-28 17:44:43
*/
@Service
public class ArticleServiceImpl extends  BaseServiceImpl<ArticleMapper, Article> implements ArticleService {

}
