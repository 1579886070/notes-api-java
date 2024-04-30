package com.zxxwl.common.utils.media;


import com.zxxwl.common.utils.Console;
import org.im4java.core.*;
import org.im4java.process.Pipe;
import org.im4java.process.ProcessStarter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ImageService extends FileService {

    protected String dir;

    private String magick;

    public ImageService() {
    }

    private class Config{
        int w;
        int h;
        double radius;
        double sigma;
        float quality;
    }

    private Config config = new Config();

    public static ImageService init(String magick){
        return new ImageService().setMagick(magick);
    }

    public ImageService setMagick(String magick){
        this.magick = magick;
        return this;
    }

    public ImageService setW(int w){
        this.config.w = w;
        return this;
    }

    public ImageService setH(int h){
        this.config.h = h;
        return this;
    }

    public ImageService setRadius(double radius){
        this.config.radius = radius;
        return this;
    }

    public ImageService setSigma(double sigma){
        this.config.sigma = sigma;
        return this;
    }

    public ImageService setQuality(float quality){
        this.config.quality = quality;
        return this;
    }

    public byte[] read(String path){
        return this.read(path, true);
    }

    /**
     * 图片读取
     * @param path String 图片保存位置
     * @param sf boolean 短边优先缩放
     * @return 二进制图片内容
     */
    public byte[] read(String path, boolean sf){
        File file = this.getFile(path);

        if( file == null )
            return null;

        FileInputStream stream = this.toStream(file);
        if( stream == null )
            return null;

        if( !isUnix() )
            ProcessStarter.setGlobalSearchPath(this.magick);

        Info image = null;
        try {
            image = new Info(path, true);
        } catch (InfoException e) {
            return null;
        }

        int[] setting = this.resize(image, sf);

        IMOperation op = new IMOperation();
        op.addImage("-");

        if( setting[2] > 0 && setting[3] > 0 ){
            // 执行有意义的缩放、裁剪
            op.resize(setting[2], setting[3]);
            op.crop(setting[2], setting[3], setting[0], setting[1]);
        }

        // 去除exif
        op.addRawArgs("+profile", "*");

        // 压缩大小
        if( this.config.quality < 100 && this.config.quality > 1 )
            op.quality((double)this.config.quality);

        // 高斯模糊
        if( this.config.radius > 0 || this.config.sigma > 0 ){
            op.blur(
                    this.config.radius > 0 ? this.config.radius : 5.0,
                    this.config.sigma  > 0 ? this.config.sigma : 20.0
            );
        }

        op.addImage(image.getImageFormat() + ":-");

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        ConvertCmd convert = new ConvertCmd();
        if( File.separator.equals("\\"))
            convert.setSearchPath(this.magick);
        convert.setInputProvider(new Pipe(stream, null));
        convert.setOutputConsumer(new Pipe(null, bytes));

        try {
            convert.run(op);
        } catch (IOException | InterruptedException | IM4JavaException e) {
            Console.bug(e.getMessage());
        }finally {
            try {
                stream.close();
            } catch (IOException e) {
                Console.bug(e.getMessage());
            }
        }

        byte[] result = bytes.toByteArray();
        try {
            bytes.close();
        } catch (IOException e) {
            Console.bug(e.getMessage());
        }

        return result;
    }

    public int[] resize(Info image, boolean sf){
        int width = 0;
        try {
            width = image.getImageWidth();
        } catch (InfoException e) {
            return new int[]{
                    0, 0, this.config.w, this.config.h
            };
        }
        int height = 0;
        try {
            height = image.getImageHeight();
        } catch (InfoException e) {
            return new int[]{
                    0, 0, width, this.config.h
            };
        }

        int tw = this.config.w == 0 ? width : this.config.w;
        int th = this.config.h == 0 ? height : this.config.h;

        float scale = 1.0f;
        float rw = width;
        float rh = height;
        if( tw != width || th != height ){
            // 执行缩放
            float sx = this.config.w * 1.f / width;
            float sy = this.config.h * 1.f / height;

            if( sx > 1 )
                sx = 1.f;

            if( sy > 1 )
                sy = 1.f;

            if( sf )
                // 短边优先缩放，保持图片完整
                scale = sx < sy ? sx : sy;
            else
                // 长边优先缩放，保证填充显示
                scale = sx < sy ? sy : sx;

            // 根据缩放，得到目标宽高
            if( scale < 1.f ){
                rw = width * scale;
                rh = height * scale;
            }
        }

        // 根据缩放结果，判断是否需要裁剪
        float x = 0;
        float y = 0;
        if( rw > tw )
            x = (rw - tw) * .5f;
        else
            rw = tw;

        if( rh > th )
            y = (rh - th) * .5f;
        else
            rh = th;

        return new int[]{
                (int)x,
                (int)y,
                (int)rw,
                (int)rh,
        };
    }
}
