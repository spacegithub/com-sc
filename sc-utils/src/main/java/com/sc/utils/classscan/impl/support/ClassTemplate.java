package com.sc.utils.classscan.impl.support;

import com.sc.utils.reflect.ClassUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileFilter;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 用于获取类的模板类

 */
public abstract class ClassTemplate {

    private static final Logger logger = LoggerFactory.getLogger(ClassTemplate.class);

    protected final String packageName;
    
    protected String oldBasePackage;
    
    protected String oldBasePackageJudge;
    
    protected boolean existsRegular;
    
    protected AntPathMatcher pathMatcher = new AntPathMatcher();

    protected ClassTemplate(String packageName) {
        this.packageName = packageName;
    }

    public final List<Class<?>> getClassList() {
        List<Class<?>> classList = new ArrayList<Class<?>>();
        try {
            String[] basePackages = StringUtils.tokenizeToStringArray(StringUtils.replace(packageName, ".", "/"), ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
            if(basePackages == null) 
            	return classList;
            for(int i=0;i<basePackages.length;i++){
            	oldBasePackage = StringUtils.trimAllWhitespace(basePackages[i]);
            	oldBasePackageJudge = StringUtils.replace(oldBasePackage, "/", ".") + ".**";
            	String basePackage = oldBasePackage;
            	existsRegular = false;
            	int index = org.apache.commons.lang3.StringUtils.indexOf(oldBasePackage, "/*");
            	if(index != -1){
            		existsRegular = true;
            		//截取到第一个.*左边的路径,比如sc.*.filecore -> sc
            		basePackage = org.apache.commons.lang3.StringUtils.substring(oldBasePackage, 0, index);
            	}
        		// 从包名获取 URL 类型的资源
        		Enumeration<URL> urls = ClassUtil.getClassLoader().getResources(basePackage);
        		// 遍历 URL 资源
        		while (urls.hasMoreElements()) {
        			URL url = urls.nextElement();
        			if (url != null) {
        				// 获取协议名（分为 file 与 jar）
        				String protocol = url.getProtocol();
        				if (protocol.equals("file")) {
        					// 若在 class 目录中，则执行添加类操作
        					String packagePath = url.getPath().replaceAll("%20", " ");
//        					System.out.println("1-------------------"+packagePath);
        					addClass(classList, packagePath, basePackages[i]);
        				} else if (protocol.equals("jar")) {
        					// 若在 jar 包中，则解析 jar 包中的 entry
        					JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
        					JarFile jarFile = jarURLConnection.getJarFile();
        					Enumeration<JarEntry> jarEntries = jarFile.entries();
        					while (jarEntries.hasMoreElements()) {
        						JarEntry jarEntry = jarEntries.nextElement();
        						String jarEntryName = jarEntry.getName();
        						// 判断该 entry 是否为 class
        						if (jarEntryName.endsWith(".class")) {
        							// 获取类名
        							String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");
        							// 执行添加类操作
//        							System.out.println("-------------------"+className);
        							doAddClass(classList, className);
        						}
        					}
        				}
        			}
        		}
        	}
        } catch (Exception e) {
            logger.error("获取类出错！", e);
        }
        return classList;
    }

	private void addClass(List<Class<?>> classList, String packagePath, String packageName) {
        try {
            // 获取包名路径下的 class 文件或目录
            File[] files = new File(packagePath).listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory();
                }
            });
            // 遍历文件或目录
            for (File file : files) {
                String fileName = file.getName();
                // 判断是否为文件或目录
                if (file.isFile()) {
                    // 获取类名
                    String className = fileName.substring(0, fileName.lastIndexOf("."));
                    if (!StringUtils.isEmpty(packageName)) {
                        className = packageName + "." + className;
                    }
                    if(className != null)
                    	className = className.replaceAll("/", ".");
                    // 执行添加类操作
                    doAddClass(classList, className);
                } else {
                    // 获取子包
                    String subPackagePath = fileName;
                    if (!StringUtils.isEmpty(packagePath)) {
                        subPackagePath = packagePath + "/" + subPackagePath;
                    }
                    // 子包名
                    String subPackageName = fileName;
                    if (!StringUtils.isEmpty(packageName)) {
//                    	subPackageName = packageName + "." + subPackageName;
                        subPackageName = packageName + "/" + subPackageName;
                    }
                    // 递归调用
                    addClass(classList, subPackagePath, subPackageName);
                }
            }
        } catch (Exception e) {
            logger.error("添加类出错！", e);
        }
    }

    private void doAddClass(List<Class<?>> classList, String className) {
    	if(existsRegular){
    		if(!pathMatcher.match(oldBasePackageJudge, className))
    			return;
    	}
        // 加载类
    	if(org.apache.commons.lang3.StringUtils.contains(className, "*"))
    		return;
        Class<?> cls = ClassUtil.loadClass(className, false);
        // 判断是否可以添加类
        //if (cls.isAnnotationPresent(Hessian.class) || cls.isAnnotationPresent(WebServlet.class)) {
            // 添加类
            classList.add(cls);
        //}
    }

    /**
     * 验证是否允许添加类
     */
    public abstract boolean checkAddClass(Class<?> cls);
}