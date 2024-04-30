package com.zxxwl.web.core.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zxxwl.SessionContext;
import com.zxxwl.common.utils.Console;
import com.zxxwl.config.JsonConfig;
import com.zxxwl.web.core.mvc.Data;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author zhouxin
 * @author qingyu
 */
@Slf4j
public class Request {
    private final HttpServletRequest httpServletRequest;
    private Map<String, Object> params = getEmptyParams();
    private Map<String, Object> paramsUrl = getEmptyParams();

    private MultipartHttpServletRequest formData;

    @Getter
    private String method = "GET";

    @Getter
    private String lang = "";

    public final static String LANG_KEY = "_lang";
    public final static String SAVE_LANG_KEY = "_ns";

    private boolean _mobile = false;

    private String UserAgent = "";
    @Getter
    private String prefix;

    private String _raw = null;
    private Map<String, Data> queryData = null;
    /*
     * multipart/form-data;
     * application/x-www-form-urlencoded
     * application/json
     * text/plain|text/html
     */
//    private String contentType = "application/json";


    public Request(HttpServletRequest request) {
        this.httpServletRequest = request;
        initRequest(prefix);
    }

    public Request(HttpServletRequest request, String prefix) {
        this.httpServletRequest = request;
        initRequest(prefix);
    }

    public static Request init(HttpServletRequest req) {
        return new Request(req);
    }

    public static Request init(HttpServletRequest req, String prefix) {
        return new Request(req, prefix);
    }

    public void initRequest(String prefix) {
        this.prefix = prefix;
        this.method = this.httpServletRequest.getMethod().toUpperCase();
        String contentType = Optional.ofNullable(this.httpServletRequest.getContentType()).orElse("");
        paramsUrl = this.parseQueryString();
        if (contentType.startsWith(MediaType.APPLICATION_JSON_VALUE)) {
            this.params = this.parseJson();
        } else if (contentType.startsWith(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
            this.params = this.parseForm();
        } else if (contentType.startsWith(MediaType.MULTIPART_FORM_DATA_VALUE)) {
            // 非完整支持
            MultipartResolver resolver = new StandardServletMultipartResolver();
            MultipartHttpServletRequest formData = null;
            try {
                formData = resolver.resolveMultipart(httpServletRequest);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            if (formData != null) {
                this.formData = formData;
                this.params = this.parseForm(this.formData.getParameterMap());
            }
        }
        Optional.ofNullable(params).orElse(getEmptyParams())
                .putAll(Optional.ofNullable(paramsUrl).orElse(getEmptyParams()));

        this.parseUserAgent();

        this.setShared();
    }


    public boolean isGet() {
        return this.method.equals("GET");
    }

    public boolean isPost() {
        return this.method.equals("POST");
    }

    public boolean isPut() {
        return this.method.equals("PUT");
    }

    public boolean isDelete() {
        return this.method.equals("DELETE");
    }

    // FIXME @dev
    public boolean isMobile() {
        // return true;
        return this._mobile;
    }


    public boolean isAjax() {
        String header = httpServletRequest.getHeader("X-Requested-With");
        if (header == null || header.isEmpty())
            header = httpServletRequest.getHeader("HTTP_X_REQUESTED_WITH");
        if (header == null || header.isEmpty())
            return false;

        return header.equalsIgnoreCase("xmlhttprequest");
    }

    public boolean isSecure() {
        return getScheme().equals("https");
    }

    public String getScheme() {
        return httpServletRequest.getScheme().toLowerCase();
    }

    public String getFullURL() {
        StringBuffer buffer = httpServletRequest.getRequestURL();
        if (isGet()) {
            String params = httpServletRequest.getQueryString();
            if (params != null && params.isEmpty()) {
                buffer.append("?").append(params);
            }
        }
        return buffer.toString();
    }

    public String getURI() {
        return httpServletRequest.getRequestURI();
    }

    public String getHost() {
        return httpServletRequest.getServerName();
    }

    public HttpSession getSession() {
        return SessionContext.getSession(httpServletRequest);
    }

    public HttpSession getSession(boolean rs) {
        return httpServletRequest.getSession();
    }

    public String ip() {
        return httpServletRequest.getRemoteAddr();
    }

    public int getPort() {
        return httpServletRequest.getServerPort();
    }

    public Data getHeader(String name) {
        return new Data(httpServletRequest.getHeader(name));
    }

    public boolean has(String name) {
        return hasQuery(name) || hasPost(name);
    }

    public boolean hasQuery(String name) {
        return this.params.containsKey(name);
    }

    public boolean hasPost(String name) {
        if (formData != null && formData.getParameter(name) != null) {
            return true;
        }

        if (params != null && params.containsKey(name)) {
            return true;
        }

        return false;
    }

    public boolean hasPut(String name) {
        return this.params.containsKey(name);
    }

    public Data get(String name) {
        if (isGet()) {
            return getQuery(name);
        } else {
            return getPost(name);
        }
    }


    private void buildQueryData() {
        if (queryData == null) {
            Map<String, Data> queryData = new HashMap<>();
            if (this.params != null) {
                for (String key : this.params.keySet()) {
                    if (LANG_KEY.equals(key)) {
                        continue;
                    }

                    if (isPost()) {
                        queryData.put(key, this.getPost(key));
                    } else if (isGet()) {
                        queryData.put(key, this.getQuery(key));
                    } else {
                        queryData.put(key, this.getPut(key));
                    }
                }
            }

            this.queryData = queryData;
        }
    }


    public Map<String, Data> getQuery() {
        if (!isGet())
            return null;

        if (this.queryData == null)
            buildQueryData();

        return this.queryData;
    }

    public Data getQuery(String name) {
        if (!isGet())
            return null;

        if (!this.params.containsKey(name))
            return new Data();

        Object values = this.params.get(name);
        return new Data(values);
    }

    public Map<String, Data> getPost() {
        if (!isPost())
            return null;

        if (this.queryData == null)
            buildQueryData();

        return this.queryData;
    }

    public Data getPost(String name) {
        if (!isPost())
            return null;

        if (!this.params.containsKey(name))
            return new Data();

        Object values = this.params.get(name);
        return new Data(values);
    }

    /**
     * @param name keyName
     * @return data
     * @author qingyu
     */
    public Data getData(String name) {
        if (!this.params.containsKey(name))
            return new Data();

        Object values = this.params.get(name);
        return new Data(values);
    }


    public Map<String, Data> getPut() {
        if (!isPut() && !isDelete())
            return null;

        if (this.queryData == null)
            buildQueryData();

        return this.queryData;
    }

    public Data getPut(String name) {
        if (!this.params.containsKey(name))
            return new Data();

        Object values = this.params.get(name);
        if (values == null)
            return new Data();
        else
            return new Data(values);
    }

    public String getModule() {
        return "";
    }

    public MultiValueMap<String, MultipartFile> getFiles() {
        if (formData == null)
            return null;

        return formData.getMultiFileMap();
    }

    public String getRawBody() {
        if (_raw != null)
            return _raw;

        BufferedReader reader = null;
        try {
            reader = httpServletRequest.getReader();
        } catch (IOException e) {
            _raw = "";
            return "";
        }
        if (reader == null) {
            _raw = "";
            return "";
        }

        StringBuilder builder = new StringBuilder();

        int length = 0;
        char[] buffer = new char[1024];
        int size = 0;
        while (true) {
            try {
                length = reader.read(buffer, 0, 1024);
            } catch (IOException e) {
                length = -1;
            }

            if (length == -1)
                break;

            size += length;
            builder.append(buffer, 0, length);
        }

        try {
            reader.close();
        } catch (IOException e) {
            Console.log(e.getMessage());
        }

        if (!builder.isEmpty())
            _raw = builder.substring(0, size);
        else
            _raw = "";

        return _raw;
    }

    public JSON getJsonRawBody(boolean isArray) {
        String raw = getRawBody();

        if (isArray) {
            if (JSONArray.isValidArray(raw))
                return JSONArray.parseArray(raw);
            else
                return new JSONArray();
        } else {
            if (JSONObject.isValidObject(raw))
                return JSONObject.parseObject(raw);
            else
                return new JSONObject();
        }
    }

    public void parseUserAgent() {
        String ua = this.getUserAgent().toLowerCase();
        String[] keywords = new String[]{
                "mobile",
                "micromessenger",
                "iphone",
                "ipad",
                "android",
                "uc",
                "wp",
        };

        for (String keyword : keywords)
            if (ua.contains(keyword)) {
                this._mobile = true;
                break;
            }
    }

    public String getUserAgent() {
        if (this.UserAgent == null || this.UserAgent.isEmpty())
            this.UserAgent = this.getHeader("User-Agent").toString();

        return this.UserAgent;
    }

    public void setShared() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        RequestContextHolder.setRequestAttributes(attr, true);
    }

    /**
     * url ? a=1&b=2
     *
     * @return R
     */
    private Map<String, Object> parseQueryString() {
        String queryString = httpServletRequest.getQueryString();
        Map<String, Object> rParams = getEmptyParams();
        try {
            if (queryString != null && !queryString.isEmpty()) {
                String[] params = queryString.split("&");
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2) {
                        rParams.put(keyValue[0], keyValue[1]);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("parse error queryString:{},error:", queryString, e);
            rParams = getEmptyParams();
        }
        return rParams;
    }

    /**
     * 处理application/x-www-form-urlencoded参数
     * httpServletRequest.getParameterMap()
     *
     * @param paramsOld params
     * @return R
     */
    private Map<String, Object> parseForm(Map<String, String[]> paramsOld) {
        Map<String, Object> rParams = getEmptyParams();
        try {
            rParams = paramsOld.entrySet().stream()
                    .filter(entry -> entry.getValue() != null && entry.getValue().length > 0)
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue()[0]));
            log.debug("parseForm:{}", rParams);
        } catch (Exception e) {
            log.warn("parse error params:{},error:", paramsOld, e);
            rParams = getEmptyParams();
        }
        return rParams;
    }

    private Map<String, Object> parseForm() {
        Map<String, String[]> paramsOld = httpServletRequest.getParameterMap();
        Map<String, Object> rParams = getEmptyParams();
        try {
            rParams = paramsOld.entrySet().stream()
                    .filter(entry -> entry.getValue() != null && entry.getValue().length > 0)
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue()[0]));
            log.debug("parseForm:{}", rParams);
        } catch (Exception e) {
            log.warn("parse error params:{},error:", paramsOld, e);
            rParams = getEmptyParams();
        }
        return rParams;
    }

    /**
     * 处理application/json参数
     * httpServletRequest.getInputStream()
     *
     * @return R
     */
    private Map<String, Object> parseJson() {
        Map<String, Object> rParams = null;
        try {
            rParams = JsonConfig.getInstance().readValue(httpServletRequest.getInputStream(), new TypeReference<Map<String, Object>>() {
            });
            log.debug("parseJson:{}", rParams);
        } catch (MismatchedInputException em) {
            // do nothing
        } catch (IOException e) {
            log.warn("parse  error:", e);
        }
        return Optional.ofNullable(rParams).orElse(getEmptyParams());
    }


    private static Map<String, Object> getEmptyParams() {
        return new HashMap<>();
    }
}
