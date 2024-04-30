package com.zxxwl.web.core.mvc;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.zxxwl.SessionContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;


@Component
public class DBMetaHandler implements MetaObjectHandler {
    private static final String IP = "ip";


    private static final String ID = "id";
    private static final String C_TIME = "createTime";
    private static final String U_TIME = "updateTime";
    /**
     * 逻辑删除旧
     */
    private static final String STATUS = "status";
    /**
     * 逻辑删除
     */
    private static final String DF = "df";

    private static final String CREATOR = "creator";
    private static final String UPDATER = "updater";
    private static final String DEPARTMENT = "department";


    @Override
    public void insertFill(MetaObject meta) {
        // long time = Clock.systemDefaultZone().millis();
        long time = Instant.now().toEpochMilli();
        this.strictInsertFill(meta, DF, Boolean.class, false);
        this.strictInsertFill(meta, STATUS, Boolean.class, true);
        this.strictInsertFill(meta, C_TIME, Long.class, time);
        this.strictInsertFill(meta, U_TIME, Long.class, time);
        this.strictInsertFill(meta, IP, byte[].class, this.ip());


        // TODO 用户ID 采取Token
        /*HttpSession session = this.getSession();
        long id = getUserId(session);
        long department = getUserDepartment(session);
        if (id > 0) {
            this.strictInsertFill(meta, CREATOR, Long.class, id);
            this.strictInsertFill(meta, UPDATER, Long.class, id);
            this.strictInsertFill(meta, DEPARTMENT, Long.class, department);
        }*/
    }


    @Override
    public void updateFill(MetaObject meta) {
        // 起始版本 3.3.3(推荐)
        // 强制设置更新时间（mybatis-plus 仅field为 null 时 才会自动填充）
        meta.setValue(U_TIME, null);

        this.strictUpdateFill(meta, U_TIME, Long.class, Instant.now().toEpochMilli());
        this.strictUpdateFill(meta, IP, byte[].class, this.ip());

        /*HttpSession session = this.getSession();
        long id = getUserId(session);
        if (id > 0) {
            this.strictUpdateFill(meta, UPDATER, Long.class, id);
        }*/
    }

    public HttpSession getSession() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null)
            return null;

        HttpServletRequest req = attributes.getRequest();
        return SessionContext.getSession(req);
    }

    public byte[] ip() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null)
            return null;

        HttpServletRequest req = attributes.getRequest();
        String ip = req.getRemoteAddr();
        return Data.ip2blob(ip);
    }


    private long getUserId(HttpSession session) {
        JSONObject result = (JSONObject) session.getAttribute("API_LOGIN");
        return result == null ? 0L : result.getLong("id");
    }

    private long getUserDepartment(HttpSession session) {
        return 0L;
    }
    /*  @Override
    public void insertFill(MetaObject meta) {
//         long time = Date.time();
        long time = Clock.systemDefaultZone().millis();
//        BigInteger time = BigInteger.valueOf(Clock.systemDefaultZone().millis());
        this.setData(meta, C_TIME, time, true);
        this.setData(meta, U_TIME, time, true);
        this.setData(meta, STATUS, 1, true);
        this.setData(meta, DF, false, true);
/
        this.setData(meta, IP, this.ip(), true);

        // TODO 用户ID 采取Token
        HttpSession session = this.getSession();
        long id = getUserId(session);
        long department = getUserDepartment(session);
        if (id > 0) {
            this.setData(meta, CREATOR, id, true);
            this.setData(meta, UPDATER, id, true);
            this.setData(meta, DEPARTMENT, department, false);
        }
    }*/
    /*    @Override
    public void updateFill(MetaObject meta) {
        // 起始版本 3.3.3(推荐)
        // 强制设置更新时间（mybatis-plus 仅field为 null 时 才会自动填充）
        meta.setValue("updateTime", null);
        this.setData(meta, U_TIME, (long) Date.time(), true);
        this.setData(meta, IP, this.ip(), true);

        HttpSession session = this.getSession();
        long id = getUserId(session);
        if (id > 0)
            this.setData(meta, UPDATER, id, true);
    }*/

    /*private void setData(MetaObject meta, String field, long value, boolean force) {
        if (meta.hasSetter(field)) {
            Object data = getFieldValByName(field, meta);
            if (force || data == null || (long) data < 1L)
                this.fillStrategy(meta, field, value);
        }
    }

    private void setData(MetaObject meta, String field, BigInteger value, boolean force) {
        if (meta.hasSetter(field)) {
            Object data = getFieldValByName(field, meta);
            if (force || data == null || (long) data < 1L) {

                this.fillStrategy(meta, field, value);
                this.setFieldValByName(field, value, meta);
            }
        }
    }

    private void setData(MetaObject meta, String field, boolean value, boolean force) {
        if (meta.hasSetter(field)) {
            Object data = getFieldValByName(field, meta);
            if (force || data == null || (long) data < 1L)
                this.fillStrategy(meta, field, value);
        }
    }

    private void setData(MetaObject meta, String field, String value, boolean force) {
        if (meta.hasSetter(field)) {
            Object data = getFieldValByName(field, meta);
            if (force || data == null || (data.equals("") && !value.equals("")))
                this.fillStrategy(meta, field, value);
        }
    }

    private void setData(MetaObject meta, String field, int value, boolean force) {
        if (meta.hasSetter(field)) {
            Object data = getFieldValByName(field, meta);
            if (force || data == null || (int) data < 1)
                this.fillStrategy(meta, field, value);
        }
    }

    private void setData(MetaObject meta, String field, byte[] value, boolean force) {
        if (meta.hasSetter(field)) {
            Object data = getFieldValByName(field, meta);
            if (force || data == null)
                this.fillStrategy(meta, field, value);
        }
    }*/
}
