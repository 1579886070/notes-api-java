package com.zxxwl.common.web.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.ModelAndView;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class Response {

    private Object body = null;

    private ResponseEntity.BodyBuilder builder;

    private int seconds = 0;
    private String ETag = "";

    private Response(int status) {
        this.builder = ResponseEntity.status(status);
        this.builder.header("x-author-by", "www.zxxwl.com");
        this.builder.header("x-api-version", "1.0.0");
        this.cache(0, "");
    }

    public static Response ok() {
        return status(HttpStatus.OK.value());
    }

    public static Response ok(boolean isAPIResponse) {
        return status(HttpStatus.OK.value(), isAPIResponse);
    }

    public static Response fail() {
        return new Response(HttpStatus.BAD_REQUEST.value());
    }

    public static Response status(int status) {
        return new Response(status);
    }

    public static Response status(int status, boolean isAPIResponse) {
        Response response = status(status);
        if (isAPIResponse)
            response.isAPIResponse();

        return response;
    }

    public static Response status(int status, boolean isAPIResponse, String frame) {
        Response response = status(status);
        if (isAPIResponse)
            response.isAPIResponse(frame);

        return response;
    }

    public Response setHeader(String key, String message) {
        this.builder.header(key, message);
        return this;
    }

    public Response pagination(int total, int page, int size) {
        this.builder.header("x-api-page", page + "");
        this.builder.header("x-api-size", size + "");
        this.builder.header("x-api-total", total + "");

        return this;
    }

    public Response pagination(long total, long page, long size) {
        this.builder.header("x-api-page", page + "");
        this.builder.header("x-api-size", size + "");
        this.builder.header("x-api-total", total + "");

        return this;
    }

    public Response pagination(Page page) {
        return this.pagination(page.getTotal(), page.getCurrent(), page.getSize());
    }

    public Response AllowOrigin() {
        this.builder.header("Access-Control-Allow-Origin", "*");
        return this;
    }

    public Response AllowOrigin(String... domains) {
        this.builder.header("Access-Control-Allow-Origin", domains);
        return this;
    }

    public Response cache(int seconds, String etag) {
        this.seconds = seconds;
        this.ETag = etag;

        return this;
    }

    private Response isAPIResponse(String frame) {
        this.builder.header("X-Frame-Options", frame);
        this.builder.header("X-Xss-Protection", "1; mode=block");
        this.builder.header("X-Content-Type-Options", "nosniff");
        this.builder.header("Access-Control-Allow-Headers",
                "Access-Control-Request-Method,Cache-control,Origin,Token,X-Requested-With,Content-Type,Accept");
        this.builder.header("Access-Control-Expose-Headers",
                "x-api-page,x-api-total,x-api-size,x-api-message,x-api-token,x-api-extra");
        this.builder.header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        return this;
    }

    private Response isAPIResponse() {
        return this.isAPIResponse("deny");
    }

    public Response message(String info) {
        String coded = URLEncoder.encode(info, Charset.defaultCharset());
        this.builder.header("x-api-message", Base64.getEncoder().encodeToString(coded.getBytes()));
        return this;
    }

    public Response content(JSONObject data) {
        this.content(JSON.toJSONString(data, SerializerFeature.DisableCircularReferenceDetect),
                "application/json; charset=UTF-8");
        return this;
    }

    public Response content(String data) {
        if (data == null)
            return this.content(new JSONArray());
        else if (data.equals(""))
            return this.content(new JSONObject());

        this.content(data, "application/json; charset=UTF-8");
        return this;
    }

    public Response content(Object data) {
        if (data == null)
            return this.content(new JSONArray());
        else if (data.equals(""))
            return this.content(new JSONObject());

        this.content(data, "application/json; charset=UTF-8");
        return this;
    }
    public Response content(Object data, boolean object) {
        if (data == null && object)
            return this.content(new JSONObject());
        this.content(data, "application/json; charset=UTF-8");
        return this;
    }
    public Response content(JSONArray data) {
        this.content(JSON.toJSONString(data, SerializerFeature.DisableCircularReferenceDetect),
                "application/json; charset=UTF-8");
        return this;
    }

    public ResponseEntity media(byte[] bytes, String contentType, long size, String hash) {
        this.builder.header("Content-Type", contentType).contentLength(size);
        this.builder.header("Connection", "close");

        this.cache(8640000, hash).SetCacheInResponse(); // cache for 100 days

        return this.builder.body(bytes);
    }

    public Response content(String content, String contentType) {
        this.builder.header("Content-Type", contentType);
        this.body = content;
        return this;
    }

    public Response content(Object content, String contentType) {
        this.builder.header("Content-Type", contentType);
        this.body = content;
        return this;
    }

    public Response html(String content) {
        this.builder.header("Content-Type", "text/html; charset=UTF-8");
        this.body = content;
        return this;
    }

    public Response xml(String content) {
        this.builder.header("Content-Type", "text/xml; charset=UTF-8");
        this.body = content;
        return this;
    }

    private void SetCacheInResponse() {
        if (seconds < 1) {
            this.builder.header("Cache-Control", "no-store, no-cache, must-revalidate");
            this.builder.header("Pragma", "no-cache");
            this.builder.header("Expires", "Thu, 01 Jan 1970 08:00:00 GMT");
        } else {
            this.builder.header("Pragma", "cache");
            this.builder.header("Cache-Control", "public, max-age=" + seconds);
            this.builder.header("Expires", LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'")));
        }

        if (this.ETag != null && !this.ETag.equals(""))
            this.builder.header("Etag",
                    new StringBuilder().append("\"").append(this.ETag).append("\"").toString());
    }

    public ResponseEntity send() {
        if (body == null)
            return this.builder.build();

        this.SetCacheInResponse();

        return this.builder.body(body);
    }

    public ResponseEntity view(ModelAndView mv) {
        this.builder.header("Content-Type", "text/html; charset=UTF-8");
        return this.builder.body(mv);
    }

    public ResponseEntity redirect(String url) {
        return this.redirect(url, null);
    }

    public ResponseEntity redirect(String url, HttpHeaders headers) {
        this.builder = ResponseEntity.status(302);
        if (headers != null)
            this.builder.headers(headers);

        this.setHeader("Location", url);

        return this.send();
    }

    public static ResponseEntity sendResponse(String data) {
        if (data == null)
            return Response.status(HttpStatus.OK.value()).isAPIResponse().content(new JSONArray())
                    .setHeader("x-api-message", "success").send();
        else if (data.equals(""))
            return Response.status(HttpStatus.OK.value()).isAPIResponse().content(new JSONObject())
                    .setHeader("x-api-message", "success").send();
        else
            return Response.status(HttpStatus.OK.value()).isAPIResponse().html(data).send();
    }

    public static ResponseEntity sendResponse(boolean isArray) {
        if (isArray)
            return Response.status(HttpStatus.OK.value()).isAPIResponse().content(new JSONArray())
                    .setHeader("x-api-message", "success").send();
        else
            return Response.status(HttpStatus.OK.value()).isAPIResponse().content(new JSONObject())
                    .setHeader("x-api-message", "success").send();
    }

    public static ResponseEntity sendResponse(JSONObject data) {
        return Response.status(HttpStatus.OK.value()).isAPIResponse().content(data).setHeader("x-api-message", "success").send();
    }

    public static ResponseEntity sendResponse(JSONArray data) {
        return Response.status(HttpStatus.OK.value()).isAPIResponse().content(data).setHeader("x-api-message", "success").send();
    }

    public static ResponseEntity sendResponse(JSONObject data, String message) {
        return Response.status(HttpStatus.OK.value()).isAPIResponse().content(data).message(message).send();
    }

    public static ResponseEntity sendResponse(JSONArray data, String message) {
        return Response.status(HttpStatus.OK.value()).isAPIResponse().content(data).message(message).send();
    }

    public static ResponseEntity sendResponse(JSONArray data, String message, int code) {
        return Response.status(code).isAPIResponse().content(data).message(message).send();
    }

    public static ResponseEntity sendResponse(JSONObject data, String message, int code) {
        return Response.status(code).isAPIResponse().content(data).message(message).send();
    }

    public static ResponseEntity sendResponse(boolean isArray, String message, int code) {
        if (isArray)
            return Response.status(code).isAPIResponse().content(new JSONArray()).message(message).send();
        else
            return Response.status(code).isAPIResponse().content(new JSONObject()).message(message).send();
    }
}
