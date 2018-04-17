package com.sc.utils.qrcode;



import com.google.zxing.common.BitMatrix;

import com.sc.utils.qrcode.compose.MatrixToImageWriterEx;
import com.sc.utils.qrcode.compose.QrcodeVo;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;


public class QrCodeUtils {

    
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
