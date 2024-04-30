package com.zxxwl.web.core.mvc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zxxwl.common.utils.EN;
import com.zxxwl.common.utils.Lang;
import lombok.*;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@TableName("")
@EqualsAndHashCode(callSuper = true)
//@KeySequence(clazz = Long.class)
public abstract class BaseEntity<T extends Model> extends Model implements Serializable {


    public BaseEntity bind(Map<String, Data> params) {
        return (BaseEntity) Data.bind(this, params);
    }

    public JSONObject toJSON() {
        return JSONObject.parseObject(JSON.toJSONString(this));
    }

    public static String table(Class<? extends BaseEntity> entityClass) {
        TableName name = entityClass.getAnnotation(TableName.class);
        if (name != null)
            return name.value();

        return "";
    }

    public static String table(Class<? extends BaseEntity> entityClass, String lang) {
        lang = lang.toUpperCase();
        if (isSupportLang(entityClass, lang))
            return table(entityClass) + "_" + lang;

        return table(entityClass);
    }

    public static boolean isSupportLang(Class<? extends BaseEntity> entityClass, String lang) {
        if (lang == null || lang.equals(""))
            return false;

        lang = lang.toUpperCase();

        if (lang.equals("EN")) {
            EN en = entityClass.getAnnotation(EN.class);
            if (en != null)
                return en.support();
        }

        Lang _lang = entityClass.getAnnotation(Lang.class);
        if (_lang == null)
            return false;

        String[] supports = _lang.value();
        if (supports.length < 1)
            return false;

        for (String support : supports)
            if (support.toUpperCase().equals(lang)) {
                return true;
            }

        return false;
    }

    public boolean returnFullData() {
        return false;
    }

    //    public abstract Serializable pkVal();

}
