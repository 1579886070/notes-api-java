package com.zxxwl.web.core.mvc;


import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;

import java.io.Serializable;

public class Model {
    protected final transient Class<?> entityClass = this.getClass();

    public Serializable pkVal() {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
        return (Serializable) tableInfo.getPropertyValue(this, tableInfo.getKeyProperty());
    }
}