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

/**
 * <一句话功能简述> <功能详细描述>
 *
 * @auth:qiss
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class CodeGeneration {
    /*************************************
     * 需要修改的项start
     *************************************/
    static String domainName = "UpdatePayStatus"; //类名
    static String packagePath = "sc.code.platform";//类包
    static String hessionUrl="hessian.url.payment";
    /*************************************
     * 需要修改的项end
     *************************************/
    static String templateDir = "\\src\\main\\resources\\templete\\";
    static String sourcePath = System.getProperty("user.dir")+templateDir;
    static String resultDir = "\\out";
    static String targetPath = System.getProperty("user.dir")
            + resultDir + "\\"
            + packagePath.replace(".", "\\");




    public static void main(String[]args) throws Exception {

        Map<String,Object> map = new HashMap();
        map.put("RequestTemplete.java","request/" + domainName + "Request.java");
        map.put("ResponseTemplete.java","response/" + domainName + "Response.java");
        map.put("ServiceImplTemplete.java","service/impl/" + domainName + "ServiceImpl.java");
        map.put("ServiceTemplete.java","service/I" + domainName + "Service.java");

        for(String templateFile:map.keySet()){
            String targetFile = (String) map.get(templateFile);
            Properties pro = new Properties();
            pro.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
            pro.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
            pro.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, sourcePath);
            VelocityEngine ve = new VelocityEngine(pro);
            //获取包名称
            String packageName=(packagePath+"."+targetFile.substring(0,targetFile.lastIndexOf("/"))).replace("/", ".");
            VelocityContext context = new VelocityContext();
            context.put("domainName",domainName);
            context.put("packageName", packageName);
            context.put("hessionUrl",hessionUrl);

            Template t = ve.getTemplate(templateFile, "UTF-8");

            File file = new File(targetPath, targetFile);
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();
            if (!file.exists())
                file.createNewFile();

            FileOutputStream outStream = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(outStream,
                    "UTF-8");
            BufferedWriter sw = new BufferedWriter(writer);
            t.merge(context, sw);
            sw.flush();
            sw.close();
            outStream.close();
            System.out.println("成功生成Java文件:"
                    + (targetPath + targetFile).replaceAll("/", "\\\\"));
        }
    }
}
