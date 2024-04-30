package com.zxxwl.web.core.db;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zxxwl.web.core.mvc.Data;

import java.io.Serializable;
import java.util.Map;

public interface BaseService<T> extends IService<T> {
    Page<Map<String, Object>> doQuery(QueryBuilder builder, String... columns);

    /**
     * 根据ID获取详情，getById别名
     * @param id Serializable 表主键
     * @return T
     */
    T detail(Serializable id);

    QueryBuilder getBuilder();

    T add(Map<String, Data> entity);

    T update(Serializable id, Map<String, Data> entity);

    T add(Map<String, Data> entity, JSONObject user);

    T update(Serializable id, Map<String, Data> entity, JSONObject user);

    boolean remove(Serializable id, boolean trueDelete);

    T createEntity() throws NoSuchMethodException;

    void setLang(String lang);

    int copy(String lang, Serializable id);
    int rcopy(String lang, Serializable id);
}