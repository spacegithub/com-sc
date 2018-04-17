package com.sc.utils.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class BarcodeUtil {
    
    public static BufferedImage encode(String contents, int width, int height, int offset) throws WriterException, IOException {
        contents = new String(contents.getBytes("UTF-8"), "ISO-8859-1");
        BitMatrix matrix = new MultiFormatWriter().encode(contents, BarcodeFormat.CODE_128, width - offset, height);
        return MatrixToImageWriter.toBufferedImage(matrix);
    }

    
    public static String decode(File file) throws IOException, NotFoundException {
        BufferedImage image = ImageIO.read(file);
        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap imageBinaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result = new MultiFormatReader().decode(imageBinaryBitmap, null);
        return result.getText();
    }

    public static void main(String[] args)throws Exception {
        ImageIO.write(encode("dddd",500,100,12),"png",new File("c:\\a.png"));
        System.out.println("-->" +  decode(new File("c:\\a.png")));
    }
}
