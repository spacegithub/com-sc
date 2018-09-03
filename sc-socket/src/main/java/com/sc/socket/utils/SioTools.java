package com.sc.socket.utils;

import com.sc.socket.utils.hutool.FileUtil;
import com.sc.socket.utils.hutool.StrUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class SioTools {
    private static Logger log = LoggerFactory.getLogger(SioTools.class);
    private static Set<String> acceptedExt = new HashSet<>();
    private static int modifiedCount = 0;

    static {
        acceptedExt.add("js");
        acceptedExt.add("jsp");
        acceptedExt.add("xml");
        acceptedExt.add("bat");
        acceptedExt.add("sh");
        acceptedExt.add("java");
        acceptedExt.add("properties");
        acceptedExt.add("sql");
        acceptedExt.add("txt");
        acceptedExt.add("log");
        acceptedExt.add("css");
        acceptedExt.add("md");
        acceptedExt.add("form");
    }

    /**
     * @创建时间:　2016年6月29日 下午2:47:09
     */
    public SioTools() {
    }

    //	@SuppressWarnings("rawtypes")
    //	private static SynThreadPoolExecutor threadExecutor = new SynThreadPoolExecutor(40, 120, "quickstart-thread-pool");

    public static void main(String[] args) {

    }

    //	private static int renameCount = 0;

    /**
     * @param old2newFilename 可以为空或null
     */
    public static void rename(String rootDirStr, Map<String, String> old2newFilename) throws IOException {
        //		System.out.println("renameCount:" + renameCount++ + ", " + rootDirStr);
        if (old2newFilename == null || old2newFilename.size() == 0) {
            return;
        }

        File rootDir = new File(rootDirStr);
        File[] files = rootDir.listFiles(new MyFileFilter());

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            //			String absolutePath = file.getAbsolutePath();
            String filename = file.getName();
            Set<Entry<String, String>> old2newFilenameSet = old2newFilename.entrySet();
            for (Entry<String, String> entry : old2newFilenameSet) {
                String oldfilenamefragment = entry.getKey();
                String newfilenamefragment = entry.getValue();
                if (filename.contains(oldfilenamefragment)) {
                    String newfilename = filename.replaceAll(oldfilenamefragment, newfilenamefragment);
                    File newFile = new File(file.getParentFile(), newfilename);
                    boolean f = file.renameTo(newFile);
                    file = newFile;
                    if (f) {
                        System.out.println("改名成功，原名:" + filename + ", 新名：" + newfilename + ", " + file.getAbsolutePath());
                    } else {
                        System.out.println("改名失败，原名:" + filename + ", 新名：" + newfilename + ", " + file.getAbsolutePath());
                    }
                }
            }
        }

        rootDir = new File(rootDirStr);
        files = rootDir.listFiles(new MyFileFilter());

        for (int i = 0; i < files.length; i++) {
            File file = files[i];

            if (file.isDirectory()) {
                rename(file.getAbsolutePath(), old2newFilename);
            }
        }

    }

    /**
     *
     * @param rootDirStr
     * @param old2newStr
     * @throws IOException
     */
    public static void replaceStr(String rootDirStr, Map<String, String> old2newStr) throws Exception {
        File rootDir = new File(rootDirStr);
        File[] files = rootDir.listFiles(new MyFileFilter());

        if (files == null) {
            return;
        }

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            String absolutePath = file.getAbsolutePath();
            //			String filename = file.getName();
            //			Set<Entry<String, String>> old2newFilenameSet = old2newFilename.entrySet();

            //			String extension = FilenameUtils.getExtension(file.getName());
            //svn-base
            if (file.isFile() && !"TioTools.java".equals(file.getName())) {
                String filecontent = FileUtil.readUTF8String(file);
                Set<Entry<String, String>> old2newStrSet = old2newStr.entrySet();
                boolean needRewrite = false;
                for (Entry<String, String> entry1 : old2newStrSet) {
                    String oldstrfragment = entry1.getKey();
                    String newstrfragment = entry1.getValue();
                    if (filecontent.contains(oldstrfragment)) {
                        filecontent = filecontent.replaceAll(oldstrfragment, newstrfragment);
                        needRewrite = true;
                    }
                }
                if (needRewrite) {
                    FileUtil.writeString(filecontent, file.getCanonicalPath(), "utf-8");//.writeStringToFile(file, filecontent, "utf-8");
                    System.out.println(++modifiedCount + "、" + file.getAbsolutePath());
                }

            } else if (file.isDirectory()) {
                replaceStr(absolutePath, old2newStr);
            }
        }
    }


    /**
     * 给空目录添加一个空白文件
     */
    public static void addBlankFile(String rootDirStr) {
        File rootDir = new File(rootDirStr);
        File[] files = rootDir.listFiles();

        if (files.length == 0) {
            File blankFile = new File(rootDir, "svn.txt");
            log.warn(blankFile.getAbsolutePath());
            try {
                blankFile.createNewFile();
            } catch (IOException e) {
                log.error(e.toString(), e);
            }
        } else {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                String absolutePath = file.getAbsolutePath();

                if (file.isDirectory()) {
                    addBlankFile(absolutePath);
                }
            }
        }
    }


    /**
     *
     * @param rootDirStr
     * @param searchStr
     * @param count
     *
     */
    public static void findFile(String rootDirStr, String searchStr, java.util.concurrent.atomic.AtomicInteger count) {
        File rootDir = new File(rootDirStr);
        File[] files = rootDir.listFiles();

        if (files.length == 0) {

        } else {
            for (int i = 0; i < files.length; i++) {
                try {
                    File file = files[i];
                    String absolutePath = file.getAbsolutePath();

                    if (StrUtil.containsAny(file.getName(), searchStr)) {
                        if (file.isDirectory()) {
                            log.warn("\r\n[dir ]" + file.getAbsolutePath());
                        } else {
                            log.warn("\r\n[file]" + file.getAbsolutePath());
                        }
                        count.incrementAndGet();
                    }
                    if (file.isDirectory()) {
                        findFile(absolutePath, searchStr, count);
                    }
                } catch (Exception e) {
                    log.error(e.toString(), e);
                }
            }
        }
    }

    /**
     * 删除文件名与filenames中相同的
     */
    public static void deleteFiles(String[] rootDirStrs, String[] filenames) {
        for (String rootDirStr : rootDirStrs) {
            try {
                deleteFile(rootDirStr, filenames);
            } catch (Exception e) {
                log.error(e.toString(), e);
            }
        }
    }

    /**
     * 删除文件名与filenames中相同的
     */
    @SuppressWarnings("unused")
    public static void deleteFile(String rootDirStr, String[] filenames) {
        File rootDir = new File(rootDirStr);
        File[] files = rootDir.listFiles();

        if (files.length == 0) {

        } else {
            lab1:
            for (int i = 0; i < files.length; i++) {
                try {
                    File file = files[i];
                    String absolutePath = file.getAbsolutePath();

                    lab2:
                    for (int j = 0; j < filenames.length; j++) {
                        if (file.getName().equals(filenames[j])) {
                            FileUtil.del(file);
                            log.warn(file.getAbsolutePath());
                            continue lab1;
                        }
                    }

                    if (file.isDirectory()) {
                        deleteFile(absolutePath, filenames);
                    }
                } catch (Exception e) {
                    log.error(e.toString(), e);
                }
            }
        }
    }

    static class MyFileFilter implements FileFilter {

        @Override
        public boolean accept(File file) {
            //			return true;

            String absolutePath = file.getAbsolutePath();
            String filename = file.getName();
            String extension = FileUtil.extName(filename);//.getExtension(filename);
            if (file.isDirectory()) {
                if (absolutePath.contains("\\webapp\\js") || absolutePath.contains("-app\\nginx\\cache") || absolutePath.contains("nginx\\html\\js") || filename.equals("target")) {
                    return false;
                }

                if ("svn-base".equalsIgnoreCase(extension)) {
                    return false;
                }

                return true;
            }

            String ext = FileUtil.extName(file);
            if (acceptedExt.contains(ext)) {
                return true;
            }

            return false;
        }

    }
}
