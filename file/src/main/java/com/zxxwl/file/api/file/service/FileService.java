package com.zxxwl.file.api.file.service;

import com.zxxwl.common.web.http.Response;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件表
 *
 * @author qingyu
 * @since 2023-09-05 11:31:41
 */
public interface FileService {
    /**
     * {@code  https://zxxwl-xiaota.oss-cn-hangzhou.aliyuncs.com/tfw/i/yyyMMdd/123456789123456789.jpeg}
     *
     * @param file MultipartFile
     * @return success
     * @author qingyu
     */
    Response uploadShortLink(MultipartFile file);

    /**
     * {@code  https://zxxwl-xiaota.oss-cn-hangzhou.aliyuncs.com/tfw/i/yyyMMdd/123456789123456789.jpeg}
     *
     * @param file MultipartFile[]
     * @return success
     * @author qingyu
     */
    Response batchUploadShortLink(MultipartFile[] file);


    Object videoToImage(String url);

    boolean fragmentationUpload(MultipartFile file, String s);

    MultipartFile base64ToMultipart(String base64);

}
