package com.sc.utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ProFileUtil {
    private static final String fileName = "prop.properties";

    public ProFileUtil() {
    }

    public static byte[] getFileByte(String pro) throws IOException {
        Object b = null;
        Object in = null;

        byte[] b1;
        try {
            Properties prop = new Properties();
            in = ProFileUtil.class.getClassLoader().getResourceAsStream("prop.properties");
            if(in == null) {
                throw new RuntimeException("没有找到配置文件prop.properties");
            }

            prop.load((InputStream)in);
            ((InputStream)in).close();
            String filepath = prop.getProperty(pro);
            if(filepath == null) {
                throw new RuntimeException("没有找到配置信息" + pro);
            }

            in = new FileInputStream(new File(filepath));
            if(in == null) {
                throw new RuntimeException("文件不存在" + filepath);
            }

            b1 = new byte[20480];
            ((InputStream)in).read(b1);
        } finally {
            if(in != null) {
                ((InputStream)in).close();
            }

        }

        return b1;
    }

    public static String getPro(String pro) {
        InputStream in = null;

        String var5;
        try {
            Properties ex = new Properties();
            in = ProFileUtil.class.getClassLoader().getResourceAsStream("prop.properties");
            if(in == null) {
                throw new RuntimeException("没有找到配置文件prop.properties");
            }

            ex.load(in);
            in.close();
            var5 = ex.getProperty(pro).trim();
        } catch (Exception var13) {
            RuntimeException rex = new RuntimeException(var13.getMessage());
            rex.setStackTrace(var13.getStackTrace());
            throw rex;
        } finally {
            if(in != null) {
                try {
                    in.close();
                } catch (Exception var12) {
                    RuntimeException rex1 = new RuntimeException(var12.getMessage());
                    rex1.setStackTrace(var12.getStackTrace());
                    throw rex1;
                }
            }

        }

        return var5;
    }

}