package com.zxxwl.file.api.file.controller;


import com.zxxwl.common.annotation.ApiReqLimit;
import com.zxxwl.common.annotation.HeaderTokenCheck;
import com.zxxwl.common.web.http.Response;
import com.zxxwl.file.api.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件表
 *
 * @author qingyu
 * @apiNote 均为短链(相对较短)
 * @since 2023.09.05
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/file")
public class FileController {
    private final FileService baseService;

    /**
     * 单个文件上传接口
     *
     * @param file MultipartFile
     * @return R
     * @apiNote 短链
     */
    @HeaderTokenCheck
    @PostMapping("upload")
    public ResponseEntity<?> upload(@RequestParam(value = "file") MultipartFile file) {
        Response result = baseService.uploadShortLink(file);
        return result.send();
    }

    /**
     * 多个文件上传接口
     *
     * @param file MultipartFile[]
     * @return R
     */
    @HeaderTokenCheck
    @ApiReqLimit
    @PostMapping("batch/upload")
    public ResponseEntity<?> uploads02(@RequestPart(value = "file") MultipartFile[] file) {
        Response result = baseService.batchUploadShortLink(file);
        return result.send();
    }

    /**
     * 视频截图图片
     *
     * @param url 视频url
     * @return R
     * @throws Exception
     * @deprecated 暂不支持
     */
    @HeaderTokenCheck
    @ApiReqLimit
    @Deprecated
    @GetMapping("videoInImage")
    public Object videoInImage(String url) {
        return baseService.videoToImage(url);
    }

    /**
     * 分片上传
     *
     * @param file 文件
     * @return R
     * @deprecated 暂不需要
     */
    @Deprecated
    @PostMapping("fragmentation/upload")
    public Object fragmentationUpload(@RequestParam(value = "file") MultipartFile file) {
        boolean status = baseService.fragmentationUpload(file, "test/" + System.currentTimeMillis() + ".png");
        return status;
    }


}
