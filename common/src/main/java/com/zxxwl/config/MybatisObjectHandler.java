package com.zxxwl.config;

/**
 * MybatisObjectHandler
 * FIXME 若用LocalDateTime 是否有影响
 *
 * @author zhouxin
 * @author qingyu
 * @since 2021/4/7 15:39
 */

public class MybatisObjectHandler {

}
/*@Component("mybatisObjectHandler")
public class MybatisObjectHandler implements MetaObjectHandler {
    private static final String IP = "ip";


    private static final String ID = "id";
    private static final String C_TIME = "createTime";
    private static final String U_TIME = "updateTime";
    *//**
 * 逻辑删除旧
 * <p>
 * 逻辑删除
 *//*
    private static final String STATUS = "status";
    *//**
 * 逻辑删除
 *//*
    private static final String DF = "df";

    private static final String CREATOR = "creator";
    private static final String UPDATER = "updater";
    private static final String DEPARTMENT = "department";


    @Override
    public void insertFill(MetaObject meta) {
        // long time = Clock.systemDefaultZone().millis();
        long time = Instant.now().toEpochMilli();
        this.strictUpdateFill(meta, DF, Boolean.class, false);
        this.strictUpdateFill(meta, STATUS, Integer.class, 1);
        this.strictUpdateFill(meta, C_TIME, Long.class, time);
        this.strictUpdateFill(meta, U_TIME, Long.class, time);
    }


    @Override
    public void updateFill(MetaObject meta) {
        // 起始版本 3.3.3(推荐)
        // 强制设置更新时间（mybatis-plus 仅field为 null 时 才会自动填充）
        meta.setValue(U_TIME, null);
        this.strictUpdateFill(meta, U_TIME, Long.class, Instant.now().toEpochMilli());
    }
}*/
