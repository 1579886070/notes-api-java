package com.zxxwl.web.core.mvc;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zxxwl.SessionContext;
import com.zxxwl.common.annotation.HeaderTokenCheck;
import com.zxxwl.common.annotation.SafetyTokenCheck;
import com.zxxwl.common.constants.SysConstants;
import com.zxxwl.common.utils.jwt.JwtUtilV2;
import com.zxxwl.common.utils.sys.SysHttpRequestUtil;
import com.zxxwl.common.web.http.Response;
import com.zxxwl.exception.UnauthorizedException;
import com.zxxwl.web.core.db.BaseService;
import com.zxxwl.web.core.db.QueryBuilder;
import com.zxxwl.web.core.http.Request;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static com.zxxwl.common.constants.SysLoginConstants.ACCOUNT_MSG_NOTLOGIN;
import static com.zxxwl.common.constants.SysLoginConstants.ACCOUNT_STATUS_NOTLOGIN;

@Slf4j
@RestController
public abstract class BaseController<S extends BaseService<?>> {
    @Autowired
    private HttpServletRequest httpServletRequest;
    //    public abstract String getModuleName();
    @Getter
    @Value("${custom.common-web.module-name}")
    private String moduleName;
    @Getter
    @Autowired
    protected S baseService;
    @Autowired
    public Request request;

    public BaseController() {
        this.initialize();
    }

    protected void initialize() {
    }

    public String title() {
        return null;
    }

    /**
     * 查询
     *
     * @return R
     * @apiNote id||query
     * @apiNote $eq, $exp, $exists, $nexists, $neq, $lte, $lt, $gte, $gt, $null, $regex, $nregex, $btw, $nbtw, $in, $nin, $and, $or
     */
    @SafetyTokenCheck
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET},
            value = {
                    "/index", "/"
            })
    public ResponseEntity<?> index() {
        if (request.hasQuery("id")) {
            Object detail = baseService.detail(request.getQuery("id").toString());
            return Response.ok(true).content(detail, true).send();
        }

        Page<Map<String, Object>> page = baseService.doQuery(initBuilder(request));

        return Response.ok(true).pagination(page).content(page.getRecords()).send();

    }

    /**
     * 新增
     *
     * @return R
     */
    @HeaderTokenCheck
    @SafetyTokenCheck
    @RequestMapping(method = {RequestMethod.POST},
            value = "/add")
    public ResponseEntity<?> add() {

        if (!addAble())
            return Response.sendResponse(false);

        if (request.isPost()) {
            Map<String, Data> params = request.getPost();

            params.remove("id");

            params.remove(Request.SAVE_LANG_KEY);
            params.remove(Request.LANG_KEY);

            BaseEntity<?> entity;
            try {
                params = this.beforeAddHook(params, request);
                entity = (BaseEntity) baseService.add(params, getUserInfo(request));
            } catch (Exception e) {
                log.error("add error", e);
                return Response.sendResponse(false, e.getMessage(), 500);
            }

            if (entity.returnFullData()) {
                return this.addHook(entity.toJSON(), request);
            }

            JSONObject result = new JSONObject();
            result.put("id", entity.pkVal());
            return this.addHook(result, request);
        }

        return Response.sendResponse("");
    }

    /**
     * 修改|更新|编辑
     *
     * @return R
     * @apiNote id||uuid
     */
    @SafetyTokenCheck
    @HeaderTokenCheck
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT},
            value = "/update")
    public ResponseEntity<?> update() {
        if (!editAble())
            return Response.sendResponse(false);

        if (request.isPut()) {
            Map<String, Data> params = request.getPut();

            params.remove(Request.SAVE_LANG_KEY);
            params.remove(Request.LANG_KEY);

            BaseEntity<?> entity;
            if (params.containsKey("id") || params.containsKey("uuid")) {
                String id = params.containsKey("id") ? params.get("id").toString("") : params.get("uuid").toString("");
                params.remove("id");
                params.remove("uuid");
                try {
                    params = this.beforeUpdateHook(params, request);
                    entity = (BaseEntity) baseService.update(id, params, getUserInfo(request));
                } catch (Exception e) {

                    log.error("update error", e);
                    return Response.sendResponse(false, e.getMessage(), 500);
                }
            } else
                return Response.sendResponse(false, "未找到对应的更新对象", 404);

            return this.updateHook(entity == null ? null : entity.toJSON(), request);
        }

        return Response.sendResponse(false);
    }

    /**
     * 修改状态
     *
     * @return R
     * @apiNote status(int 0, 1)||df (boolean true,false)
     */
    @SafetyTokenCheck
    @HeaderTokenCheck
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT},
            value = "/status")
    public ResponseEntity<?> status() {
        if (!removeAble())
            return Response.sendResponse("");


        String id = request.getPut("id").toString("");
        if (request.isPut()) {
            Map<String, Data> params = new HashMap<>();
            if (Boolean.TRUE.equals(request.hasPut("status"))) {
                int status = request.getPut("status").toInt(1);
                params.put("status", new Data(status));
            } else if (Boolean.TRUE.equals(request.hasPut("df"))) {
                boolean df = request.getPut("df").toBool();
                params.put("df", new Data(df));
            }
            baseService.update(id, params, getUserInfo(request));

            return Response.sendResponse("");
        } else if (request.isDelete()) {
            boolean canDelete = false;
            if (canDelete())
                canDelete = request.getPut("t_d").toInt(0) > 0;

            baseService.remove(id, canDelete);

            return Response.sendResponse("");
        }

        return Response.sendResponse("");
    }

    /**
     * 兼容处理 不需要调用
     *
     * @return Request
     * @deprecated 不需要调用
     */

    @Deprecated
    protected Request getRequest() {
        return request;
         /*if (request == null) {
            request = Request.init(this.request, getSessionNamespace());
        }
        return request;*/
    }


    protected String getSessionNamespace() {
        return this.getModuleName();
    }

    protected boolean canDelete() {
        return false;
    }

    public HttpSession getSession() {
        return SessionContext.getSession(httpServletRequest);
    }

    protected QueryBuilder initBuilder(Request request) {
        QueryBuilder builder = baseService.getBuilder();

        if (request != null)
            builder.useRequest(request);

        return builder;
    }

    protected QueryBuilder initBuilder() {
        QueryBuilder builder = baseService.getBuilder();

        if (request != null)
            builder.useRequest(request);

        return builder;
    }

    public JSONObject getUserInfo() {
        return getUserInfo(request);
    }

    public Long getUserId(Request request) {
        JSONObject user = this.getUserInfo(request);
        return user.getLong("id");
    }

    public JSONObject getUserInfo(Request request) {
        return (JSONObject) request.getSession().getAttribute("API_LOGIN");
    }

    public String getMemberId() {
        String headerToken = SysHttpRequestUtil.getUserToken(httpServletRequest);
        String memberId = JwtUtilV2.getMemberId(headerToken);
        if (!StringUtils.hasText(memberId)) {
            throw new UnauthorizedException(ACCOUNT_MSG_NOTLOGIN, ACCOUNT_STATUS_NOTLOGIN);
        }
        return memberId;
    }

    public String getMemberId(Request request) {
        String headerToken = SysHttpRequestUtil.getUserToken(httpServletRequest);
        String memberId = JwtUtilV2.getMemberId(headerToken);
        if (!StringUtils.hasText(memberId)) {
            throw new UnauthorizedException(ACCOUNT_MSG_NOTLOGIN, ACCOUNT_STATUS_NOTLOGIN);
        }
        return memberId;
    }


    public String getShopId(Request request) {
        String headerToken = request.getHeader(SysConstants.REQ_HEAD_USER_TOKEN).toString("");
        return JwtUtilV2.getShopId(headerToken);
    }

    protected Map<String, Data> beforeUpdateHook(Map<String, Data> params, Request request) {
        return params;
    }

    protected Map<String, Data> beforeAddHook(Map<String, Data> params, Request request) {
        return params;
    }

    protected ResponseEntity<?> addHook(JSONObject record, Request request) {
        if (record == null)
            return Response.sendResponse(false);

        return Response.sendResponse(record);
    }

    protected ResponseEntity<?> updateHook(JSONObject record, Request request) {
        if (record == null)
            return Response.sendResponse(false);

        return Response.sendResponse(record);
    }

    protected boolean addAble() {
        return true;
    }

    protected boolean editAble() {
        return true;
    }

    protected boolean removeAble() {
        return true;
    }
}
