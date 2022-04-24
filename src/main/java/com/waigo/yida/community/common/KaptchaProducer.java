package com.waigo.yida.community.common;

import com.google.code.kaptcha.Producer;
import com.waigo.yida.community.config.properties.KaptchaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.util.MimeTypeUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * author waigo
 * create 2021-10-03 20:50
 */

/**
 * 提供生成验证码的方法
 * String createCaptcha(String[] collectText):base64版本的
 * BufferedImage createCaptcha(String[] collectText)
 */
@Component
@EnableConfigurationProperties(KaptchaProperties.class)
public class KaptchaProducer {
    @Autowired
    @Qualifier("captchaProducerMath")
    private Producer captchaProducerMath;

    @Autowired
    @Qualifier("captchaProducer")
    private Producer captchaProducer;

    @Autowired
    private KaptchaProperties kaptchaProperties;
    private static final Logger LOGGER = LoggerFactory.getLogger(KaptchaProducer.class);
    /**
     * 传入一个非空的字符串数组，会将生成好的验证码放在索引0位置
     *
     * @param collectText
     * @return
     */
    public String createCaptchaBase64(String[] collectText) {
        //1.生成图片
        BufferedImage captcha = createCaptcha(collectText);
        //2.写进比特流中,FastByteArrayOutputStream性能更好
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(captcha,kaptchaProperties.getImgType(),os);
        } catch (IOException e) {
            LOGGER.error("ImageIo写图片失败：{}",e.getMessage());
        }
        String mimeType = "png".equals(kaptchaProperties.getImgType())?
                MimeTypeUtils.IMAGE_PNG_VALUE:MimeTypeUtils.IMAGE_JPEG_VALUE;
        //例如:data:image/jpeg;base64,xxxxx
        return "data:"+mimeType+";base64,"+new String(Base64Utils.encode(os.toByteArray()));
    }

    /**
     * 传入一个非空的字符串数组，会将生成好的验证码放在索引0位置
     *
     * @param collectText
     * @return
     */
    public BufferedImage createCaptcha(String[] collectText) {
        if ("math".equals(kaptchaProperties.getType())) {
            String text = captchaProducerMath.createText();
            //text是这样的2x3 @ 6，也就是算式@结果这样
            int separator = text.lastIndexOf("@");
            collectText[0] = text.substring(separator+1);
            return captchaProducerMath.createImage(text.substring(0,separator));
        } else {
            String text = captchaProducer.createText();
            collectText[0] = text;
            return captchaProducer.createImage(text);
        }
    }
}
