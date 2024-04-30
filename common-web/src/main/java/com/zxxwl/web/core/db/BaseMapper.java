package com.zxxwl.web.core.db;

import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.sql.SQLSyntaxErrorException;
import java.util.HashMap;
import java.util.List;

public interface BaseMapper<T> extends com.baomidou.mybatisplus.core.mapper.BaseMapper<T> {
    List<HashMap<String, Object>> paginationQuery(
            @Param("columns") String columns,
            @Param("tables") List<QueryTable> tables,
            @Param("joins") List<QueryTable> joins,
            @Param("params") String query,
            @Param("orderBy") String orderBy,
            @Param("groupBy") String groupBy,
            @Param("having") String having,
            @Param("offset") int offset,
            @Param("size") int size
    );

    int copy(@Param("table") String table, @Param("lang") String lang, @Param("id") Serializable id)
            throws SQLSyntaxErrorException;

    int rcopy(@Param("table") String table, @Param("lang") String lang, @Param("id") Serializable id)
            throws SQLSyntaxErrorException;
}