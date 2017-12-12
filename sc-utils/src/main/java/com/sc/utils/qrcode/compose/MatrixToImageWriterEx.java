package com.sc.utils.qrcode.compose;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Hashtable;

import javax.imageio.ImageIO;

/**
 * @author qiss
 */
public class MatrixToImageWriterEx {


    /**
     * 解析二维码（QRCode）
     *
     * @param filePath 图片路径
     */
    public static String readQRCode(String filePath) {
        String str = null;
        try {
            str = new MultiFormatReader().decode(new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(ImageIO.read(new FileInputStream(filePath))))), new Hashtable()).getText();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 创建二维码
     */
    public static BitMatrix createQRCode(QrcodeVo qrcodeVo) {
        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, qrcodeVo.getErrorCorrection() != null ? qrcodeVo.getErrorCorrection() : ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, Integer.valueOf(qrcodeVo.getMargin() != null ? qrcodeVo.getMargin().intValue() : 1));
        BitMatrix matrix = null;
        try {
            matrix = new MultiFormatWriter().encode(qrcodeVo.getContent(), BarcodeFormat.QR_CODE, qrcodeVo.getWidth().intValue(), qrcodeVo.getHeight().intValue(), hints);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return matrix;
    }

    /**
     * 将二维码写入到流
     */
    public static BufferedImage writeToStream(BitMatrix matrix, File logoFile) {
        BufferedImage bi = MatrixToImageWriter.toBufferedImage(matrix);

        if ((logoFile != null)) {
            InputStream in = null;
            try {
                in = new FileInputStream(logoFile);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return overlapImage(bi, in);
        }
        return bi;
    }

    public static BufferedImage overlapImage(BufferedImage image, InputStream in) {
        try {
            BufferedImage logo = ImageIO.read(in);
            Graphics2D g = image.createGraphics();

            int width = image.getWidth() / 5;
            int height = image.getHeight() / 5;

            int x = (image.getWidth() - width) / 2;
            int y = (image.getHeight() - height) / 2;

            g.drawImage(logo, x, y, width, height, null);
            g.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }
}