
package com.sc.utils.utils;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;


/**
 * 生成验证码
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉.
 *
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */ 
public class RandomValidateCode {

    /** The Constant RANDOMCODEKEY. */
    public static final String RANDOMCODEKEY = "RANDOMVALIDATECODEKEY";// 放到session中的key
    
    /** The random. */
    private Random random = new Random();
    
    /** The rand string. */
    private String randString = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";// 随机产生的字符串

    /** The width. */
    private int width = 80;// 图片宽
    
    /** The height. */
    private int height = 26;// 图片高
    
    /** The line size. */
    private int lineSize = 7;// 干扰线数量
    
    /** The string num. */
    private int stringNum = 4;// 随机产生字符数量
    
    /** The code string. */
    private String codeString = "";
    /*
     * 获得字体
     */
    /**
     * Gets the font.
     * 
     * @return the font
     */
    private Font getFont() {
        return new Font("Unicode MS", Font.CENTER_BASELINE, 20);
    }

    /*
     * 获得颜色
     */
    /**
     * Gets the rand color.
     * 
     * @param fc the fc
     * @param bc the bc
     * @return the rand color
     */
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

    /**
     * 生成随机图片.
     * 
     * @return the validate image
     */
    public BufferedImage getValidateImage() {
        System.setProperty("java.awt.headless", "true");
        // BufferedImage类是具有缓冲区的Image类,Image类是用于描述图像信息的类
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_BGR);
        Graphics g = image.getGraphics();// 产生Image对象的Graphics对象,改对象可以在图像上进行各种绘制操作
        g.fillRect(0, 0, width, height);
        g.setFont(getFont());
        g.setColor(getRandColor(110, 133));
        // 绘制干扰线
        for (int i = 0; i <= lineSize; i++) {
            drowLine(g);
        }
        // 绘制随机字符
        codeString = "";
        for (int i = 1; i <= stringNum; i++) {
            codeString = drowString(g, codeString, i);
        }
        g.dispose();
        return image;
    }

    /*
     * 绘制字符串
     */
    /**
     * Drow string.
     * 
     * @param g the g
     * @param randomString the random string
     * @param i the i
     * @return the string
     */
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

    /*
     * 绘制干扰线
     */
    /**
     * Drow line.
     * 
     * @param g the g
     */
    private void drowLine(Graphics g) {
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        int xl = random.nextInt(13);
        int yl = random.nextInt(15);
        g.drawLine(x, y, x + xl, y + yl);
    }

    /*
     * 获取随机的字符
     */
    /**
     * Gets the random string.
     * 
     * @param num the num
     * @return the random string
     */
    public String getRandomString(int num) {
        return String.valueOf(randString.charAt(num));
    }

    /**
     * Gets the width.
     * 
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Sets the width.
     * 
     * @param width the new width
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Gets the height.
     * 
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the height.
     * 
     * @param height the new height
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Gets the line size.
     * 
     * @return the line size
     */
    public int getLineSize() {
        return lineSize;
    }

    /**
     * Sets the line size.
     * 
     * @param lineSize the new line size
     */
    public void setLineSize(int lineSize) {
        this.lineSize = lineSize;
    }

    /**
     * Gets the string num.
     * 
     * @return the string num
     */
    public int getStringNum() {
        return stringNum;
    }

    /**
     * Sets the string num.
     * 
     * @param stringNum the new string num
     */
    public void setStringNum(int stringNum) {
        this.stringNum = stringNum;
    }

    /**
     * Gets the code string.
     * 
     * @return the code string
     */
    public String getCodeString() {
        return codeString;
    }
    
}
