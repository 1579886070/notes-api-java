package com.zxxwl.web.core.db;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxxwl.common.utils.Console;
import com.zxxwl.web.core.mvc.Data;
import com.zxxwl.web.core.mvc.BaseEntity;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public abstract class BaseServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements BaseService<T> {
    private String lang = null;

    public Page<Map<String, Object>> doQueryNoCount(QueryBuilder builder, String... columns) {
        Page<Map<String, Object>> pagination = new Page<>(builder.page + 1, builder.size);

        pagination.setRecords(builder.execute(baseMapper, columns));
        pagination.setTotal(pagination.getRecords().size());

        return pagination;
    }

    public Page<Map<String, Object>> doQuery(QueryBuilder builder, String... columns) {
        long total = builder.count(baseMapper);

        Page<Map<String, Object>> pagination = new Page<>(builder.page + 1, builder.size);
        pagination.setTotal(total);

        if (Math.ceil(total * 1.0 / builder.size) > builder.page)
            pagination.setRecords(builder.execute(baseMapper, columns));
        else
            pagination.setRecords(new ArrayList<>());

        if (builder.size > 999 || builder.size < 1)
            pagination.setSize(pagination.getRecords().size());

        return pagination;
    }

    public T detail(Serializable id) {
        return getById(id);
    }

    //    public abstract Class<? extends BaseEntity> self();
    public Class<? extends BaseEntity> self() {
        return (Class<? extends BaseEntity>) ReflectionKit.getSuperClassGenericType(this.getClass(), BaseServiceImpl.class, 1);
    }

    public String getTableName() {
        return BaseEntity.table(self());
    }

    public boolean isSupport(String lang) {
        return BaseEntity.isSupportLang(self(), lang);
    }

    public T createEntity() {
        Class<? extends BaseEntity> clazz = self();
        Constructor ct;
        try {
            ct = clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            return null;
        }
        try {
            return (T) ct.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    public QueryBuilder getBuilder() {
        QueryBuilder builder = new QueryBuilder();
        builder.from(getTableName());
        return builder;
    }

    public T add(Map<String, Data> params, JSONObject user) {
        T entity = createEntity();
        if (entity == null)
            return null;

        T item = (T) Data.bind(entity, params);

        item = beforeSave(item, user);
        item = beforeAdd(item, user);

        return save(item) ? item : null;
    }

    public T add(Map<String, Data> params) {
        try {
            return add(params, null);
        } catch (Exception e) {
            Console.log(e.getMessage());
            return null;
        }
    }

    public T update(Serializable id, Map<String, Data> params) {
        try {
            return update(id, params, null);
        } catch (Exception e) {
            Console.log(e.getMessage());
            return null;
        }
    }

    public T update(Serializable id, Map<String, Data> params, JSONObject user) {
        Object entity = detail(id);
        if (entity == null)
            return null;

        T item = (T) Data.bind(entity, params);
        item = beforeSave(item, user);
        item = beforeUpdate(item, user);
        return updateById(item) ? item : null;
    }

    public T beforeUpdate(T item, JSONObject user) {
        return item;
    }

    public T beforeAdd(T item, JSONObject user) {
        return item;
    }

    public T beforeSave(T item, JSONObject user) {
        return item;
    }

    public boolean remove(Serializable id, boolean trueDelete) {
        if (trueDelete)
            return removeById(id);

        int status = 0;

        Map<String, Data> params = new HashMap<>();
        params.put("status", new Data(status));

        T result = update(id, params);

        return result != null;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getLang() {
        return this.lang;
    }

    @Override
    public int copy(String lang, Serializable id) {
        Class<? extends BaseEntity> entity = self();
        if (!BaseEntity.isSupportLang(self(), lang))
            return 0;

        lang = lang.toUpperCase();
        String table = BaseEntity.table(entity).replaceAll("_" + lang, "");
        try {
            return baseMapper.copy(table, lang, id);
        } catch (SQLSyntaxErrorException e) {
            return 0;
        }
    }

    public int rcopy(String lang, Serializable id) {
        Class<? extends BaseEntity> entity = self();
        if (!BaseEntity.isSupportLang(self(), lang))
            return 0;

        lang = lang.toUpperCase();
        String table = BaseEntity.table(entity).replaceAll("_" + lang, "");
        try {
            return baseMapper.rcopy(table, lang, id);
        } catch (SQLSyntaxErrorException e) {
            return 0;
        }
    }
}