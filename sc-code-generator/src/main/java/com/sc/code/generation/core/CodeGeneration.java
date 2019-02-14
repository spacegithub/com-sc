package com.sc.code.generation.core;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CodeGeneration {
    /**
     * 用户环境变量
     */
    private final static String domainName = "SearchRefundByIds";
    private final static String packagePath = "sc.payment.service.refund.service";
    private final static String hessionUrl = "hessian.url.payment.service";
    private final static String domainUrl = "/svr/payment/refund";

    /**
     * 系统环境
     */
    private final static String sourcePath = System.getProperty("user.dir") + "\\sc-code-generator\\src\\main\\resources\\templete";
    private final static String targetPath = System.getProperty("user.dir") + "\\out" + "\\" + packagePath.replace(".", "\\");

    public static void main(String[] args) throws Exception {
        /**
         * 模板路径
         */
        Map<String, Object> map = new HashMap();
        map.put("RequestTemplete.java", "request/" + domainName + "Request.java");
        map.put("ResponseTemplete.java", "response/" + domainName + "Response.java");
        map.put("ServiceImplTemplete.java", "service/impl/" + domainName + "ServiceImpl.java");
        map.put("ServiceTemplete.java", "service/I" + domainName + "Service.java");

        /**
         * 环境变量
         */
        Properties pro = new Properties();
        pro.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
        pro.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
        pro.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, sourcePath);//模板地址


        for (String templateFile : map.keySet()) {
            String targetFile = (String) map.get(templateFile);
            VelocityEngine ve = new VelocityEngine(pro);
            String packageName = (packagePath + "." + targetFile.substring(0, targetFile.lastIndexOf("/"))).replace("/", ".");
            VelocityContext context = new VelocityContext();
            context.put("domainName", domainName);
            context.put("packageName", packageName);
            context.put("hessionUrl", hessionUrl);
            context.put("domainUrl", domainUrl);
            Template t = ve.getTemplate(templateFile, "UTF-8");

            File file = new File(targetPath, targetFile);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream outStream = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(outStream,
                    "UTF-8");
            BufferedWriter sw = new BufferedWriter(writer);
            t.merge(context, sw);
            sw.flush();
            sw.close();
            outStream.close();
            System.out.println("成功生成Java文件:" + (targetPath + targetFile).replaceAll("/", "\\\\"));
        }
    }
}
