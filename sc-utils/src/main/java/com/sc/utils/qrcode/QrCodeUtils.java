package com.sc.utils.qrcode;

/**
 * 二维码生成器
 *
 * @auth:qiss
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */

import com.google.zxing.common.BitMatrix;

import com.sc.utils.qrcode.compose.MatrixToImageWriterEx;
import com.sc.utils.qrcode.compose.QrcodeVo;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

/**
 * 二维码的生成需要借助MatrixToImageWriter类，该类是由Google提供的，可以将该类直接拷贝到源码中使用
 *
 * @author qiss
 */
public class QrCodeUtils {

    /**
     * 创建二维码
     * @param qrcodeVo
     * @return
     */
    public static BufferedImage createQRCode(QrcodeVo qrcodeVo) {
        if (qrcodeVo.getWidth() == null) {
            qrcodeVo.setWidth(Integer.valueOf(300));
        }
        if (qrcodeVo.getHeight() == null) {
            qrcodeVo.setHeight(Integer.valueOf(300));
        }
        BitMatrix matrix = MatrixToImageWriterEx.createQRCode(qrcodeVo);
        return MatrixToImageWriterEx.writeToStream(matrix, qrcodeVo.getFile());
    }

    /**
     * 解析二维码内容
     * @param filePaht 二维码路径
     * @return
     */
    public static String readQRCode(String filePaht) {
        return MatrixToImageWriterEx.readQRCode(filePaht);
    }

    public static void main(String[] args) throws Exception {
        QrcodeVo qrcodeVo = new QrcodeVo();
        qrcodeVo.setContent("http://www.aaaa.com?token=10020%&SDS&fff=1020");
        qrcodeVo.setHeight(500);
        qrcodeVo.setWidth(500);
        qrcodeVo.setFile(new File("c:\\qrcode_logo.png"));
      ImageIO.write(createQRCode(qrcodeVo), "JPEG", new File("c:\\a.jpeg"));
  System.out.println("-->" + readQRCode("c:\\a.jpeg"));

    }


}