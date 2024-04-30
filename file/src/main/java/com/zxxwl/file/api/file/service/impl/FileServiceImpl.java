package com.zxxwl.file.api.file.service.impl;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import com.zxxwl.common.api.file.entity.TransFileVO;
import com.zxxwl.common.constants.ALYConstants;
import com.zxxwl.common.random.IdUtils;
import com.zxxwl.common.utils.file.ALiOssUtils;
import com.zxxwl.common.utils.file.FileUtils;
import com.zxxwl.common.web.http.Response;
import com.zxxwl.file.api.file.entity.File;
import com.zxxwl.file.api.file.mapper.FileMapper;
import com.zxxwl.file.api.file.service.FileService;
import com.zxxwl.login.api.service.LoginBaseService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 文件表
 *
 * @author qingyu
 * @since 2023-09-05 11:31:41
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class FileServiceImpl implements FileService {
    private final FileMapper baseMapper;
    private final LoginBaseService loginBaseService;

    /**
     * @author qingyu
     */
    @Override
    public Response uploadShortLink(@NotNull(message = "缺少必要参数！") MultipartFile file) {
        TransFileVO transFileVO = this.uploadFile(file, ALYConstants.OSS_PUBLIC_ROOT_TFW);
        if (transFileVO != null) {
            return Response.ok().content(transFileVO);
        }
        return Response.fail().message("上传失败!");
    }

    @Override
    public Response batchUploadShortLink(MultipartFile[] file) {
        if (file == null || file.length < 1) {
            return Response.fail().message("缺少必要参数！");
        }
        List<TransFileVO> list = new ArrayList<>();
        for (MultipartFile f : file) {
            TransFileVO transFileVO = this.uploadFile(f, ALYConstants.OSS_PUBLIC_PICTURE);
            if (transFileVO != null) {
                list.add(transFileVO);
            }
        }
        return Response.ok().content(list);
    }

    /**
     * FIXME 临时使用  后期逻辑 文件非法校验 完整性
     *
     * @param file file
     * @return R
     * @author qingyu
     */
    private TransFileVO uploadFile(MultipartFile file, String rootPath) {
        // 获取文件名加后缀
        if (StringUtils.isBlank(file.getOriginalFilename())) {
            // 上传异常！
            return null;
        }
        // 获取文件后缀
        String suffix2Dot = FileUtils.getExtensionWithDot(file.getOriginalFilename());

        // 获取 contentType
        String contentType = FileUtils.getContentType(file.getContentType(), suffix2Dot);

        // 处理存储路径  先分类，再日期
        // other
        String path = "o";
        if (contentType.contains(FileUtils.IMAGE)) {
            // image
            path = "i";
            /*if (StringUtils.isBlank(suffix2Dot)) {
                suffix2Dot = ".jpg";
            }*/
        } else if (contentType.contains(FileUtils.VIDEO)) {
            // video
            path = "v";
        }

        // 存储路径处理 年月日  0230606
        path += FileUtils.SEPARATOR + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + FileUtils.SEPARATOR;
        // 组合新的文件名(包含`.后缀`)
        String newFileName = IdUtils.getSnowFlakeId() + suffix2Dot;
        return this.saveFileTx(file, newFileName, rootPath + path + newFileName, suffix2Dot, contentType);
    }


    @Override
    public String videoToImage(String url) {
        /*String targetFilePath = "";
        try (FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(url)) {
            grabber.start();

            //判断是否是竖屏小视频
            String rotate = grabber.getVideoMetadata("rotate");
            int lengthInFrames = grabber.getLengthInFrames();

            Frame frame;

            int i = 0;
            int index = 1;
            // 截取图片第几帧
            while (i < lengthInFrames) {
                frame = grabber.grabImage();
                if (i == index) {
                    if (null != rotate && rotate.length() > 1) {
                        // 获取缩略图
                        targetFilePath = doExecuteFrame(frame, true);
                    } else {
                        // 获取缩略图
                        targetFilePath = doExecuteFrame(frame, false);
                    }
                    break;
                }
                i++;
            }

            grabber.stop();
        }
        // 返回的是视频第N帧
        return targetFilePath;*/
        return "";
    }

    /**
     * 截取缩略图，存入阿里云OSS（按自己的上传类型自定义转换文件格式）
     *
     * @return 图片地址
     * @throws Exception
     */
    public String doExecuteFrame(Frame f, boolean direction) throws Exception {
        /*if (null == f || null == f.image) {
            return "";
        }

        BufferedImage bi;
        try (Java2DFrameConverter converter = new Java2DFrameConverter()) {
            bi = converter.getBufferedImage(f);
        }
        if (direction) {
            Image image = (Image) bi;
            //图片旋转90度
            bi = rotate(image, 90);
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(bi, "png", os);
        InputStream input = new ByteArrayInputStream(os.toByteArray());


        Path temp = Files.createTempFile("temp", "t.png");
        OutputStream outputStream = Files.newOutputStream(temp);

        ImageIO.write(bi, "png", outputStream);
        long size = Files.size(temp);

        return uploadOnIs(input);*/
        // TODO
        return null;
    }


    private long getTempFileSize(InputStream is) {
        AtomicLong size = new AtomicLong(0);
        DiskFileItem fileItem = (DiskFileItem) new DiskFileItemFactory().createItem("file",
                MediaType.IMAGE_PNG_VALUE, true, "tmp.png");
        try (OutputStream os = fileItem.getOutputStream()) {
            is.transferTo(os);
        } catch (IOException e) {
            log.error("获取临时文件大小异常");
        } finally {
            size.set(fileItem.getSize());
        }
        return size.get();
    }

    private long getTempFileSize02(InputStream is) {
        AtomicLong size = new AtomicLong(0);
        Path temp = null;
        try {
            temp = Files.createTempFile("temp", "tmp.png");
            Files.copy(is, temp);

        } catch (IOException e) {
            log.error("获取临时文件大小异常");
        } finally {
            try {
                assert temp != null;
                size.set(Files.size(temp));
            } catch (IOException e) {
                log.error("获取临时文件大小异常");
            }
        }
        return size.get();
    }


    /**
     * 图片旋转角度
     *
     * @param src   源图片
     * @param angel 角度
     * @return R 目标图片
     */
    public static BufferedImage rotate(Image src, int angel) {
        int srcWidth = src.getWidth(null);
        int srcHeight = src.getHeight(null);

        // calculate the new image size
        Rectangle rectangle = calcRotatedSize(new Rectangle(new Dimension(
                srcWidth, srcHeight)), angel);

        BufferedImage res;
        res = new BufferedImage(rectangle.width, rectangle.height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = res.createGraphics();

        // transform(这里先平移、再旋转比较方便处理；绘图时会采用这些变化，绘图默认从画布的左上顶点开始绘画，源图片的左上顶点与画布左上顶点对齐，然后开始绘画，修改坐标原点后，绘画对应的画布起始点改变，起到平移的效果；然后旋转图片即可)
        //平移（原理修改坐标系原点，绘图起点变了，起到了平移的效果，如果作用于旋转，则为旋转中心点）
        // old g2.translate((rectangle.width - srcWidth) / 2, (rectangle.height - srcHeight) / 2);
        g2.translate((rectangle.width - srcWidth) >> 1, (rectangle.height - srcHeight) >> 1);
        // 旋转（原理 translate(dx,dy)->rotate(radians)-> translate(-dx,-dy);修改坐标系原点后，旋转90度，然后再还原坐标系原点为(0,0),但是整个坐标系已经旋转了相应的度数 ）
        // old g2.rotate(Math.toRadians(angel), srcWidth / 2, srcHeight / 2);
        g2.rotate(Math.toRadians(angel), srcWidth >> 1, srcHeight >> 1);
        // 先旋转（以目标区域中心点为旋转中心点，源图片左上顶点对准目标区域中心点，然后旋转）
        // g2.translate(rect_des.width/2,rect_des.height/ 2);
        // g2.rotate(Math.toRadians(angel));
        // 再平移（原点恢复到源图的左上顶点处（现在的右上顶点处），否则只能画出1/4）
        // g2.translate(-src_width/2,-src_height/2);
        g2.drawImage(src, null, null);

        return res;
    }

    /**
     * 计算转换后目标矩形的宽高
     *
     * @param src   源矩形
     * @param angel 角度
     * @return 目标矩形
     */
    private static Rectangle calcRotatedSize(Rectangle src, int angel) {
        double cos = Math.abs(Math.cos(Math.toRadians(angel)));
        double sin = Math.abs(Math.sin(Math.toRadians(angel)));
        int desWidth = (int) (src.width * cos) + (int) (src.height * sin);
        int desHeight = (int) (src.height * cos) + (int) (src.width * sin);

        return new Rectangle(new Dimension(desWidth, desHeight));
    }


    @Override
    public boolean fragmentationUpload(MultipartFile file, String name) {
//        String oss = ALYConstants.OSS_STATIC_POSITION;
//        String endpoint = ALYConstants.OSS_ENDPOINT;
//        String accessKeyId = ALYConstants.OSS_ACCESS_KEY;
//        String accessKeySecret = ALYConstants.OSS_ACCESS_SECRET;
//
//        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//
//        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(oss, name);
//
//        try {
//            // 初始化分片。
//            InitiateMultipartUploadResult result = ossClient.initiateMultipartUpload(request);
//            // 返回uploadId，它是分片上传事件的唯一标识，您可以根据这个uploadId发起相关的操作，如取消分片上传、查询分片上传等。
//            String uploadId = result.getUploadId();
//
//            // partETags是PartETag的集合。PartETag由分片的ETag和分片号组成。
//            ArrayList<PartETag> partETags = new ArrayList<PartETag>();
//            // 计算文件有多少个分片。
//            final long partSize = 2 * 1024 * 1024L;
//            long fileLength = file.getSize();
//            int partCount = (int) (fileLength / partSize);
//            if (fileLength % partSize != 0) {
//                partCount++;
//            }
//
//            for (int i = 0; i < partCount; i++) {
//                long startPos = i * partSize;
//                long curPartSize = (i + 1 == partCount) ? (fileLength - startPos) : partSize;
//                InputStream instream = null;
//                try {
//                    instream = new ByteArrayInputStream(file.getBytes());
//
//                    // 跳过已经上传的分片。
//                    long skip = instream.skip(startPos);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                UploadPartRequest uploadPartRequest = new UploadPartRequest();
//                uploadPartRequest.setBucketName(oss);
//                uploadPartRequest.setKey(name);
//                uploadPartRequest.setUploadId(uploadId);
//                uploadPartRequest.setInputStream(instream);
//                // 设置分片大小。除了最后一个分片没有大小限制，其他的分片最小为100 KB。
//                uploadPartRequest.setPartSize(curPartSize);
//                // 设置分片号。每一个上传的分片都有一个分片号，取值范围是1~10000，如果超出这个范围，OSS将返回InvalidArgument的错误码。
//                uploadPartRequest.setPartNumber(i + 1);
//                // 每个分片不需要按顺序上传，甚至可以在不同客户端上传，OSS会按照分片号排序组成完整的文件。
//                UploadPartResult uploadPartResult = ossClient.uploadPart(uploadPartRequest);
//                // 每次上传分片之后，OSS的返回结果包含PartETag。PartETag将被保存在partETags中。
//                partETags.add(uploadPartResult.getPartETag());
//            }
//
//            // 创建CompleteMultipartUploadRequest对象。
//            // 在执行完成分片上传操作时，需要提供所有有效的partETags。OSS收到提交的partETags后，会逐一验证每个分片的有效性。当所有的数据分片验证通过后，OSS将把这些分片组合成一个完整的文件。
//            CompleteMultipartUploadRequest completeMultipartUploadRequest =
//                    new CompleteMultipartUploadRequest(oss, name, uploadId, partETags);
//
//            // 如果需要在完成文件上传的同时设置文件访问权限，请参考以下示例代码。
//            // completeMultipartUploadRequest.setObjectACL(CannedAccessControlList.PublicRead);
//
//            // 完成上传
//            CompleteMultipartUploadResult completeMultipartUploadResult = ossClient.completeMultipartUpload(completeMultipartUploadRequest);
//
//            // 关闭OSSClient。
//            ossClient.shutdown();
//
//            return true;
//        } catch (Exception e) {
//            log.error("【文件分片上传失败，错误信息：[{}]】", e.getMessage());
//        }

        return false;
    }

    @Override
    public MultipartFile base64ToMultipart(String base64) {

        /*String base64Str = "data:image/png;base64," + base64;

        try {
            String[] baseStr = base64Str.split(",");

            byte[] b = new byte[0];
            b = Base64.getDecoder().decode(baseStr[1]);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }

            return new BASE64DecodedMultipartFile(b, baseStr[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }*/
        // TODO
        return null;
    }


    /**
     * 持久化 保存文件
     *
     * @param file         file
     * @param newFileName  newFileName
     * @param savePathName savePathName
     * @param suffix       suffix
     * @return R
     */
    private TransFileVO saveFile(MultipartFile file, String newFileName, String savePathName, String suffix, String contentType) {
        try {
            // 上传 到 oss
            boolean state = ALiOssUtils.upOssByIs(file.getInputStream(), savePathName);
            if (!state) {
                // 文件上传失败！
                return null;
            }
        } catch (IOException e) {
            log.error("【oss上传资源异常，异常信息：[{}]】", e.getMessage());
            return null;
        }
        String url = ALYConstants.OSS_STATIC_MAIN_LINK + savePathName;

        String fileId = this.insertInfo(file.getOriginalFilename(), file.getSize(), url, newFileName, suffix, contentType);
        // 记录 url,fileId
        TransFileVO transFileVO = new TransFileVO();
        transFileVO.setId(fileId);
        transFileVO.setUrl(url);
        return transFileVO;
    }

    private TransFileVO saveFileTx(MultipartFile file, String newFileName, String savePathName, String suffix, String contentType) {
        String secretId = "AKIDj9WxWCDOj6jmqxDqdSD2sk69Dg4Uzleq";
        String secretKey = "LqOf3HlSXguKDBKXj6fDkIxlEunjnkZG";
        String bucketName = "resource-1252475870";
        String region = "ap-nanjing";

        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        COSClient cosClient = new COSClient(cred, clientConfig);
        try {
            // 上传文件到cos
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            InputStream inputStream = file.getInputStream();
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, savePathName, inputStream, metadata);
            cosClient.putObject(putObjectRequest);
            inputStream.close();
            // 返回文件在cos上的访问url
        } catch (IOException e) {
            throw new RuntimeException("上传失败");
        } finally {
            // 关闭cos客户端
            cosClient.shutdown();
        }
        String url = "https://oss.xiaoxinyes.club/" + savePathName;

        String fileId = this.insertInfo(file.getOriginalFilename(), file.getSize(), url, newFileName, suffix, contentType);
        // 记录 url,fileId
        TransFileVO transFileVO = new TransFileVO();
        transFileVO.setId(fileId);
        transFileVO.setUrl(url);
        return transFileVO;
    }

    /**
     * @param url         访问地址
     * @param newName     新名
     * @param suffix      后缀
     * @param contentType 媒体类型
     * @return fileId(String)
     * @author qingyu
     */
    private String insertInfo(String originalFilename, Long size, String url, String newName, String suffix, String contentType) {
        String memberId = loginBaseService.currentMemberId();
        File file = new File();
        file.setId(IdUtils.getSnowFlakeId());
        file.setMemberId(memberId);
        file.setName(originalFilename);
        file.setNewName(newName);
        file.setUrl(url);
        file.setType(contentType);
        file.setSize(size);
        file.setSuffix(suffix);
        file.setCreateTime(Instant.now().toEpochMilli());

        this.baseMapper.insert(file);
        log.info("【文件上传，上传用户：[{}]，地址：[{}]】", memberId, url);
        return file.getId();

    }


}
