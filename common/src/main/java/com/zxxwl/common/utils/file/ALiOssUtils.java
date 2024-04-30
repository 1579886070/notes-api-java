package com.zxxwl.common.utils.file;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.zxxwl.common.constants.ALYConstants.*;

@Slf4j
public class ALiOssUtils {
    /**
     * 上传oss by  byte[]
     *
     * @param file         byte[]
     * @param savePathName savePathName
     * @return success
     * @author qingyu
     */
    public static boolean upOssByByte(byte[] file, String savePathName) {
        AtomicBoolean success = new AtomicBoolean(false);
        OSS ossClient = null;
        try {
            // 创建OSSClient实例。
            ossClient = new OSSClientBuilder().build(OSS_ENDPOINT, OSS_ACCESS_KEY, OSS_ACCESS_SECRET);
            // 传Byte数组。
            ossClient.putObject(OSS_STATIC_POSITION, savePathName, new ByteArrayInputStream(file));
            success.set(true);
        } catch (OSSException oe) {
            log.error("""
                    Caught an OSSException, which means your request made it to OSS,but was rejected with an error response for some reason.
                    Error Message:{}
                    Error Code:{}
                    Request ID:{}
                    Host ID:{}
                    """, oe.getMessage(), oe.getErrorCode(), oe.getRequestId(), oe.getHostId());
            success.set(false);
        } catch (ClientException ce) {
            log.error("""
                    Caught an ClientException, which means the client encountered a serious internal problem while trying to communicate with OSS, such as not being able to access the network.
                    Error Message:{}
                    """, ce.getMessage());
            success.set(false);
        } catch (Exception e) {
            log.error("【文件上传失败，错误信息：[{}]】", e.getMessage());
            success.set(false);
        } finally {
            // 关闭OSSClient
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return success.get();
    }

    /**
     * 上传oss by InputStream
     *
     * @param is           InputStream
     * @param savePathName savePathName
     * @return success
     * @author qingyu
     */
    public static boolean upOssByIs(InputStream is, String savePathName) {
        AtomicBoolean success = new AtomicBoolean(false);
        OSS ossClient = null;
        try {
            // 创建OSSClient实例。
            ossClient = new OSSClientBuilder().build(OSS_ENDPOINT, OSS_ACCESS_KEY, OSS_ACCESS_SECRET);
            // 传Byte数组。
            ossClient.putObject(OSS_STATIC_POSITION, savePathName, is);
            success.set(true);
        } catch (OSSException oe) {
            log.error("""
                    Caught an OSSException, which means your request made it to OSS,but was rejected with an error response for some reason.
                    Error Message:{}
                    Error Code:{}
                    Request ID:{}
                    Host ID:{}
                    """, oe.getMessage(), oe.getErrorCode(), oe.getRequestId(), oe.getHostId());
            success.set(false);
        } catch (ClientException ce) {
            log.error("""
                    Caught an ClientException, which means the client encountered a serious internal problem while trying to communicate with OSS, such as not being able to access the network.
                    Error Message:{}
                    """, ce.getMessage());
            success.set(false);
        } catch (Exception e) {
            log.error("【文件上传失败，错误信息：{}】", e.getMessage());
            success.set(false);
        } finally {
            // 关闭OSSClient
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return success.get();
    }
}
