
package com.sc.utils.utils.webutil;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;


 
public class RandomValidateCode {

    
    public static final String RANDOMCODEKEY = "RANDOMVALIDATECODEKEY";
    
    
    private Random random = new Random();
    
    
    private String randString = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    
    private int width = 80;
    
    
    private int height = 26;
    
    
    private int lineSize = 7;
    
    
    private int stringNum = 4;
    
    
    private String codeString = "";
    
    
    private Font getFont() {
        return new Font("Unicode MS", Font.CENTER_BASELINE, 20);
    }

    
    
    private Color getRandColor(int fc, int bc) {
        if (fc > 255)
            fc = 255;
        if (bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc - 16);
        int g = fc + random.nextInt(bc - fc - 14);
        int b = fc + random.nextInt(bc - fc - 18);
        return new Color(r, g, b);
    }

    
    public BufferedImage getValidateImage() {
        System.setProperty("java.awt.headless", "true");
        
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_BGR);
        Graphics g = image.getGraphics();
        g.fillRect(0, 0, width, height);
        g.setFont(getFont());
        g.setColor(getRandColor(110, 133));
        
        for (int i = 0; i <= lineSize; i++) {
            drowLine(g);
        }
        
        codeString = "";
        for (int i = 1; i <= stringNum; i++) {
            codeString = drowString(g, codeString, i);
        }
        g.dispose();
        return image;
    }

    
    
    private String drowString(Graphics g, String randomString, int i) {
        g.setFont(getFont());
        g.setColor(new Color(random.nextInt(101), random.nextInt(111), random
                .nextInt(121)));
        String rand = String.valueOf(getRandomString(random.nextInt(randString
                .length())));
        randomString += rand;
        g.translate(random.nextInt(3), random.nextInt(3));
        g.drawString(rand, 13 * i, 16);
        return randomString;
    }

    
    
    private void drowLine(Graphics g) {
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        int xl = random.nextInt(13);
        int yl = random.nextInt(15);
        g.drawLine(x, y, x + xl, y + yl);
    }

    
    
    public String getRandomString(int num) {
        return String.valueOf(randString.charAt(num));
    }

    
    public int getWidth() {
        return width;
    }

    
    public void setWidth(int width) {
        this.width = width;
    }

    
    public int getHeight() {
        return height;
    }

    
    public void setHeight(int height) {
        this.height = height;
    }

    
    public int getLineSize() {
        return lineSize;
    }

    
    public void setLineSize(int lineSize) {
        this.lineSize = lineSize;
    }

    
    public int getStringNum() {
        return stringNum;
    }

    
    public void setStringNum(int stringNum) {
        this.stringNum = stringNum;
    }

    
    public String getCodeString() {
        return codeString;
    }
    
}
