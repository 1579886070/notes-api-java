package com.zxxwl.web.core.mvc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zxxwl.common.crypto.Hash;
import com.zxxwl.common.utils.Console;
import com.zxxwl.common.utils.Constants;
import com.zxxwl.common.utils.Date;
import com.zxxwl.common.utils.FileChecker;
import com.zxxwl.common.utils.media.DocService;
import com.zxxwl.common.utils.media.ImageService;
import com.zxxwl.common.utils.media.VideoService;
import com.zxxwl.common.web.http.Response;
import com.zxxwl.web.core.db.BaseService;
import com.zxxwl.web.core.db.QueryBuilder;
import com.zxxwl.web.core.http.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class FileBaseController<S extends BaseService<?>> extends BaseController<S> {

    @Value("${file.path}")
    String dir;

    @Value("${file.magick}")
    String magick;

    @RequestMapping(method = RequestMethod.GET, value = "/check")
    public ResponseEntity<?> check() {
        Request request = this.getRequest();

        JSONObject result = this.checkIsINDB(request.getQuery("hash").toString());

        if (result != null)
            return Response.sendResponse(result.toJSONString());
        else
            return Response.sendResponse(false, "对应的文件不存在", 404);
    }

    @RequestMapping(method = {
            RequestMethod.POST,
            RequestMethod.PUT
    }, value = "/slice")
    public ResponseEntity<?> slice() {
        Request request = this.getRequest();

        if (request.isPut())
            return this.combine(request);

        MultiValueMap<String, MultipartFile> files = request.getFiles();

        String hash = request.getPost("hash").toString();
        int index = request.getPost("index").toInt();

        String path = new StringBuilder()
                .append(this.dir)
                .append(File.separator)
                .append(hash)
                .append("-")
                .append(index)
                .toString();

        File dest = new File(path);
        MultipartFile file = null;
        for (String key : files.keySet()) {
            file = files.getFirst(key);
            if (file == null) {
                path = null;
                break;
            }

            if (!dest.exists() || !dest.isFile()) {
                try {
                    file.transferTo(dest);
                    break;
                } catch (IOException e) {
                    Console.log(e.getMessage());
                    path = null;
                }
            }
        }

        if (path == null)
            return Response.sendResponse(false, "文件保存失败", 500);

        return Response.sendResponse(new JSONObject() {{
            put("hash", hash);
        }});
    }

    public ResponseEntity<?> combine(Request request) {
        int total = request.getPut("total").toInt();
        String hash = request.getPut("hash").toString();

        StringBuilder builder = new StringBuilder();
        builder.append(this.dir).append(File.separator).append(hash);

        String path = builder.toString();
        File dest = new File(path);
        if (dest.exists() && !dest.isFile())
            return Response.sendResponse(false, "文件名被文件夹占用", 500);

        if (!dest.exists()) {
            boolean success = false;
            try {
                success = dest.createNewFile();
            } catch (IOException e) {
                Console.log(e.getMessage());
            }

            if (!success)
                return Response.sendResponse(false, "目标文件创建失败", 500);
        }

        builder.append("-");

        File[] files = new File[total];
        File item = null;
        int i = 0;
        for (i = 0; i < total; i++) {
            item = new File(builder.toString() + i);

            if (!item.exists() || !item.isFile())
                return Response.sendResponse(false, "组成文件未找到", 404);

            files[i] = item;
        }

        FileChannel channel = null;
        try {
            channel = new FileOutputStream(dest).getChannel();
            FileChannel inChannel;
            for (File file : files) {
                inChannel = new FileInputStream(file).getChannel();
                inChannel.transferTo(0, inChannel.size(), channel);
                inChannel.close();
            }
        } catch (IOException e) {
            return Response.sendResponse(false, "碎片文件合并失败", 500);
        } finally {
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e) {
                    Console.log(e.getMessage());
                }
            }

            for (File file : files)
                if (file.exists() && file.isFile())
                    file.delete();
        }

        String name = request.getPut("name").toString();

        JSONObject result = this.save2db(name, hash, FileChecker.mime(dest), dest.length());

        if (!result.containsKey("success"))
            result.put("success", true);

        if (result.getBooleanValue("success"))
            return Response.sendResponse(result);

        return Response.sendResponse(result, "数据库保存失败", 500);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/blob")
    public ResponseEntity<?> blob() {
        return this.slice(this.getRequest());
    }

    @RequestMapping(method = {
            RequestMethod.POST,
            RequestMethod.GET
    }, value = {
            "/video",
            "/audio"
    })
    public ResponseEntity<?> video() {
        Request request = this.getRequest();

        if (request.isPost())
            return this.upload(request,
                    request.getURI().endsWith("/audio") ? FileChecker.Mime.Audio : FileChecker.Mime.Video);
        else
            return this.slice(request);
    }

    protected ResponseEntity<?> slice(Request request, String uuid) {
        Object result = baseService.detail(uuid);
        if (result == null)
            return Response.sendResponse(false, "未找到对应的文件", 404);

        JSONObject info = JSONObject.parseObject(JSON.toJSONString(result));

        String path = new StringBuilder()
                .append(this.dir)
                .append(File.separator)
                .append(info.getString("hash")).toString();

        String range = request.getHeader("Range").toString();
        int start = 0;
        int end = 0;
        if (range != null && !range.equals("")) {
            range = range.substring(range.lastIndexOf("=") + 1);
            String[] ranges = range.split("-");
            if (ranges.length > 0)
                start = Integer.parseInt(ranges[0]);

            if (ranges.length > 1)
                end = Integer.parseInt(ranges[1]);
        }

        int maxSize = info.getInteger("size");
        int last = maxSize - 1;
        end = Math.min(last, end);
        start = Math.max(start, 0);

        if (start == 0) {
            Map<String, Data> params = new HashMap<>();
            params.put("last_call", new Data(Date.time()));
            params.put("use_times", new Data(info.getLongValue("use_times") + 1));
            this.baseService.update(uuid, params);
        }

        byte[] bytes = null;
        String mime = info.getString("mime");
        long totalSize = info.getLong("size");
        Response response;
        if (!mime.contains("video") && !mime.contains("audio")) {
            DocService service = new DocService();
            bytes = service.read(path);
            response = Response.status(200);
            if (!(mime.contains("pdf") || mime.contains("image") || mime.contains("video") || mime.contains("audio")))
                response = response.setHeader("Content-Disposition", "attachment;filename="
                        + name(info.getString("originalName")));
        } else {
            VideoService videoService = new VideoService();
            bytes = videoService.slice(start, end, maxSize).read(path);
            response = Response.status(206).setHeader("Content-Range", new StringBuilder()
                    .append("bytes ")
                    .append(videoService.start)
                    .append("-")
                    .append(videoService.end).append("/")
                    .append(totalSize).toString()
            );
        }

        if (bytes == null)
            return Response.sendResponse(false, "未找到对应的文件", 404);

        return response
                .setHeader("Accept-Ranges", "bytes")
                .setHeader("Last-Modified", Date.format(
                                "EEE, dd MMM yyyy HH:mm:ss 'GMT'",
                                info.getLong("createTime")
                        )
                )
                .media(bytes,
                        info.getString("mime"),
                        totalSize,
                        Hash.md5(bytes)
                );
    }

    private String name(String name) {
        try {
            return URLEncoder.encode(name, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    private ResponseEntity<?> slice(Request request) {
        String uuid = request.getQuery("id").toString("");
        return this.slice(request, uuid);
    }

    @RequestMapping(method = {
            RequestMethod.POST,
            RequestMethod.GET
    }, value = "/image")
    public ResponseEntity<?> image() {
        Request request = this.getRequest();

        if (request.isPost())
            return this.upload(request, FileChecker.Mime.Image);
        else {
            Object result = baseService.detail(request.getQuery("id").toString(""));
            if (result == null)
                return Response.status(404).content("").message("未找到对应的文件").send();

            JSONObject info = JSONObject.parseObject(JSON.toJSONString(result));

            ImageService im = ImageService.init(this.magick);
            if (request.hasQuery("w"))
                im.setW(request.getQuery("w").toInt(0));
            if (request.hasQuery("h"))
                im.setH(request.getQuery("h").toInt(0));
            if (request.hasQuery("q"))
                im.setQuality(request.getQuery("q").toFloat(100.f));
            if (request.hasQuery("b")) {
                JSONArray blur = JSONArray.parseArray(request.getQuery("b").toString());
                if (blur != null)
                    im.setRadius(1.0 * blur.getInteger(0)).setSigma(1.0 * blur.getInteger(1));
            }
            boolean shortFirst = true;
            if (request.hasQuery("e"))
                shortFirst = request.getQuery("e").toBool();

            String path = new StringBuilder()
                    .append(this.dir)
                    .append(File.separator)
                    .append(info.getString("hash")).toString();

            byte[] bytes = im.read(path, shortFirst);

            if (bytes == null)
                return Response.status(404).content("").message("对应的文件不存在").send();

            return Response.ok().media(bytes,
                    info.getString("mime"),
                    bytes.length,
                    Hash.md5(bytes)
            );
        }
    }

    @RequestMapping(method = {
            RequestMethod.POST,
            RequestMethod.GET
    }, value = "/doc")
    public ResponseEntity<?> doc() {
        Request request = this.getRequest();

        if (request.isPost())
            return this.upload(request, FileChecker.Mime.File);
        else {
            Object result = baseService.detail(request.getQuery("id").toString(""));
            if (result == null)
                return Response.status(404).content("").message("未找到对应的文件").send();

            JSONObject info = JSONObject.parseObject(JSON.toJSONString(result));

            String path = new StringBuilder()
                    .append(this.dir)
                    .append(File.separator)
                    .append(info.getString("hash")).toString();

            DocService docService = new DocService();
            byte[] bytes = docService.read(path);

            if (bytes == null)
                return Response.status(404).content("").message("对应的文件不存在").send();

            String fn = info.getString("originalName");

            return Response.ok()
                    .setHeader("Content-Disposition",
                            new StringBuilder()
                                    .append("attachment;filename=")
                                    .append(
                                            new String(
                                                    fn.getBytes(Constants.DefaultCharset),
                                                    Charset.forName("ISO-8859-1")
                                            )
                                    ).toString()
                    )
                    .setHeader("Last-Modified", Date.format(
                                    "EEE, dd MMM yyyy HH:mm:ss 'GMT'",
                                    info.getLong("createTime")
                            )
                    )
                    .media(bytes,
                            info.getString("mime"),
                            info.getInteger("size"),
                            info.getString("hash")
                    );
        }
    }

    private JSONObject checkIsINDB(String hash) {
        QueryBuilder builder = baseService.getBuilder();
        builder.setCountField("uuid").setParams(new JSONObject() {{
            put("hash", hash);
        }}).setLimit(0, 1);

        Page<Map<String, Object>> pager = baseService.doQuery(builder);

        if (pager.getTotal() > 0)
            return JSONObject.parseObject(JSON.toJSONString(pager.getRecords().get(0)));
        else
            return null;
    }

    private JSONObject handleUpload(MultipartFile file, FileChecker.Mime checker) {
        // 0. 检查文件类型
        String mime = file.getContentType();

        if (!FileChecker.check(mime, checker))
            return new JSONObject() {{
                put("success", false);
                put("message", "文件类型有误");
            }};

        // 1. 生成MD5
        InputStream input = null;
        try {
            input = file.getInputStream();
        } catch (IOException e) {
            Console.log(e.getMessage());
        }
        if (input == null)
            return null;

        String md5 = Hash.stream(input);
        // 2. 数据库检查是否存在
        JSONObject result = this.checkIsINDB(md5);
        if (result != null) {
            JSONObject data = new JSONObject();
            data.put("hash", result.getString("hash"));
            data.put("success", true);
            data.put("id", result.getString("uuid"));

            data.put("size", result.getInteger("size"));
            data.put("name", result.getString("originalName"));

            return data;
        }

        // 4. 移动到指定位置
        String path = new StringBuilder().append(this.dir).append(File.separator).append(md5).toString();

        File dest = new File(path);
        if (!dest.exists() || !dest.isFile()) {
            try {
                file.transferTo(dest);
            } catch (IOException e) {
                path = null;
            }
        }

        if (path == null)
            return new JSONObject() {{
                put("success", false);
                put("message", "文件移动失败");
            }};

        // 5. 保存到数据库
        String name = file.getOriginalFilename();
        return this.save2db(name, md5, mime, file.getSize());
    }

    private JSONObject save2db(String name, String md5, String mime, long size) {
        JSONObject result = this.checkIsINDB(md5);
        if (result != null)
            return result;

        String ext_name = "";
        if (name == null)
            name = "";
        else {
            int index = name.lastIndexOf(".");
            ext_name = index > -1 ? name.substring(index + 1) : "";
        }

        Map<String, Data> params = new HashMap<>();
        params.put("hash", new Data(md5));
        params.put("mime", new Data(mime == null ? "" : mime));
        params.put("size", new Data(size));
        params.put("extName", new Data(ext_name));
        params.put("lastCall", new Data(0));
        params.put("useTimes", new Data(0));
        params.put("originalName", new Data(name));

        BaseEntity entity = (BaseEntity) baseService.add(params);
        if (entity != null) {
            JSONObject data = new JSONObject();

            data.put("hash", params.get("hash").toString());
            data.put("success", true);
            data.put("id", entity.pkVal());

            data.put("size", params.get("size").toInt());
            data.put("name", params.get("originalName").toString());

            return data;
        }

        return new JSONObject() {{
            put("success", false);
            put("message", "数据库保存失败");
        }};
    }

    public ResponseEntity<?> upload(Request request, FileChecker.Mime checker) {
        MultiValueMap<String, MultipartFile> files = request.getFiles();

        JSONObject result = new JSONObject();
        int count = 0;
        String fk = null;
        for (String key : files.keySet()) {
            List<MultipartFile> items = files.get(key);
            int size = items.size();
            if (size < 1)
                continue;

            for (int i = 0; i < size; i++) {
                key = size > 1 ? (key + "." + i) : key;
                if (count++ < 1)
                    fk = key;

                MultipartFile file = items.get(i);
                result.put(key, this.handleUpload(file, checker));
            }
        }

        if (count == 1) {
            JSONObject data = result.getJSONObject(fk);
            if (!data.containsKey("success"))
                data.put("success", true);
            return Response.sendResponse(data);
        }

        return Response.sendResponse(result);
    }
}
