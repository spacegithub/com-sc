package com.sc.utils.utils.file;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;



public class FileUtil {
    
    private FileUtil() {

    }

    
    public static String readFileToString(String path) throws Exception {
        return FileUtils.readFileToString(new File(path), "utf-8");
    }

    
    public static void touch(File file) {
        long currentTime = System.currentTimeMillis();
        if (!file.exists()) {
            System.err.println("file not found:" + file.getName());
            System.err.println("Create a new file:" + file.getName());
            try {
                if (file.createNewFile()) {
                    System.out.println("Succeeded!");
                }
                else {
                    System.err.println("Create file failed!");
                }
            }
            catch (IOException e) {
                System.err.println("Create file failed!");
                e.printStackTrace();
            }
        }
        boolean result = file.setLastModified(currentTime);
        if (!result) {
            System.err.println("touch failed: " + file.getName());
        }
    }

    
    public static void touch(String fileName) {
        File file = new File(fileName);
        touch(file);
    }

    
    public static void touch(File[] files) {
        for (int i = 0; i < files.length; i++) {
            touch(files[i]);
        }
    }

    
    public static void touch(String[] fileNames) {
        File[] files = new File[fileNames.length];
        for (int i = 0; i < fileNames.length; i++) {
            files[i] = new File(fileNames[i]);
        }
        touch(files);
    }

    
    public static boolean isFileExist(String fileName) {
        return new File(fileName).isFile();
    }

    
    public static boolean makeDirectory(File file) {
        File parent = file.getParentFile();
        if (parent != null) {
            return parent.mkdirs();
        }
        return false;
    }

    
    public static boolean makeDirectory(String fileName) {
        File file = new File(fileName);
        return makeDirectory(file);
    }

    
    public static boolean emptyDirectory(File directory) {
        boolean result = true;
        File[] entries = directory.listFiles();
        for (int i = 0; i < entries.length; i++) {
            if (!entries[i].delete()) {
                result = false;
            }
        }
        return result;
    }

    
    public static boolean emptyDirectory(String directoryName) {
        File dir = new File(directoryName);
        return emptyDirectory(dir);
    }

    
    public static boolean deleteDirectory(String dirName) {
        return deleteDirectory(new File(dirName));
    }

    
    public static boolean deleteDirectory(File dir) {
        if ( (dir == null) || !dir.isDirectory()) {
            throw new IllegalArgumentException("Argument " + dir +
                    " is not a directory. ");
        }

        File[] entries = dir.listFiles();
        int sz = entries.length;

        for (int i = 0; i < sz; i++) {
            if (entries[i].isDirectory()) {
                if (!deleteDirectory(entries[i])) {
                    return false;
                }
            }
            else {
                if (!entries[i].delete()) {
                    return false;
                }
            }
        }

        if (!dir.delete()) {
            return false;
        }
        return true;
    }


    
    public static URL getURL(File file) throws MalformedURLException {
        String fileURL = "file:/" + file.getAbsolutePath();
        URL url = new URL(fileURL);
        return url;
    }

    
    public static String getFileName(String filePath) {
        File file = new File(filePath);
        return file.getName();
    }

    
    public static String getFilePath(String fileName) {
        File file = new File(fileName);
        return file.getAbsolutePath();
    }

    
    public static String toUNIXpath(String filePath) {
        return filePath.replace('\\', '/');
    }

    
    public static String getUNIXfilePath(String fileName) {
        File file = new File(fileName);
        return toUNIXpath(file.getAbsolutePath());
    }

    
    public static String getTypePart(String fileName) {
        int point = fileName.lastIndexOf('.');
        int length = fileName.length();
        if (point == -1 || point == length - 1) {
            return "";
        }
        else {
            return fileName.substring(point + 1, length);
        }
    }

    
    public static String getFileType(File file) {
        return getTypePart(file.getName());
    }

    
    public static String getNamePart(String fileName) {
        int point = getPathLsatIndex(fileName);
        int length = fileName.length();
        if (point == -1) {
            return fileName;
        }
        else if (point == length - 1) {
            int secondPoint = getPathLsatIndex(fileName, point - 1);
            if (secondPoint == -1) {
                if (length == 1) {
                    return fileName;
                }
                else {
                    return fileName.substring(0, point);
                }
            }
            else {
                return fileName.substring(secondPoint + 1, point);
            }
        }
        else {
            return fileName.substring(point + 1);
        }
    }

    
    public static String getPathPart(String fileName) {
        int point = getPathLsatIndex(fileName);
        int length = fileName.length();
        if (point == -1) {
            return "";
        }
        else if (point == length - 1) {
            int secondPoint = getPathLsatIndex(fileName, point - 1);
            if (secondPoint == -1) {
                return "";
            }
            else {
                return fileName.substring(0, secondPoint);
            }
        }
        else {
            return fileName.substring(0, point);
        }
    }

    
    public static int getPathIndex(String fileName) {
        int point = fileName.indexOf('/');
        if (point == -1) {
            point = fileName.indexOf('\\');
        }
        return point;
    }

    
    public static int getPathIndex(String fileName, int fromIndex) {
        int point = fileName.indexOf('/', fromIndex);
        if (point == -1) {
            point = fileName.indexOf('\\', fromIndex);
        }
        return point;
    }

    
    public static int getPathLsatIndex(String fileName) {
        int point = fileName.lastIndexOf('/');
        if (point == -1) {
            point = fileName.lastIndexOf('\\');
        }
        return point;
    }

    
    public static int getPathLsatIndex(String fileName, int fromIndex) {
        int point = fileName.lastIndexOf('/', fromIndex);
        if (point == -1) {
            point = fileName.lastIndexOf('\\', fromIndex);
        }
        return point;
    }

    
    public static String trimType(String filename) {
        int index = filename.lastIndexOf(".");
        if (index != -1) {
            return filename.substring(0, index);
        }
        else {
            return filename;
        }
    }
    
    public static String getSubpath(String pathName,String fileName) {
        int index = fileName.indexOf(pathName);
        if (index != -1) {
            return fileName.substring(index + pathName.length() + 1);
        }
        else {
            return fileName;
        }
    }

    
    public static final boolean pathValidate(String path)
    {
        String[] arraypath = path.split("/");
        String tmppath = "";
        for (int i = 0; i < arraypath.length; i++)
        {
            tmppath += "/" + arraypath[i];
            File d = new File(tmppath.substring(1));
            if (!d.exists()) { 
                System.out.println(tmppath.substring(1));
                if (!d.mkdir())
                {
                    return false;
                }
            }
        }
        return true;
    }

    
    public static final String getFileContent(String path) throws IOException
    {
        String filecontent = "";
        try {
            File f = new File(path);
            if (f.exists()) {
                FileReader fr = new FileReader(path);
                BufferedReader br = new BufferedReader(fr); 
                String line = br.readLine(); 
                
                while (line != null) {
                    filecontent += line + "\n";
                    line = br.readLine(); 
                }
                br.close(); 
                fr.close(); 
            }

        }
        catch (IOException e) {
            throw e;
        }
        return filecontent;
    }

    
    public static final boolean genModuleTpl(String path, String modulecontent)  throws IOException
    {

        path = getUNIXfilePath(path);
        String[] patharray = path.split("\\/");
        String modulepath = "";
        for (int i = 0; i < patharray.length - 1; i++) {
            modulepath += "/" + patharray[i];
        }
        File d = new File(modulepath.substring(1));
        if (!d.exists()) {
            if (!pathValidate(modulepath.substring(1))) {
                return false;
            }
        }
        try {
            FileWriter fw = new FileWriter(path); 
            
            fw.write(modulecontent);
            fw.close();
        }
        catch (IOException e) {
            throw e;
        }
        return true;
    }

    
    public static final String getPicExtendName(String pic_path)
    {
        pic_path = getUNIXfilePath(pic_path);
        String pic_extend = "";
        if (isFileExist(pic_path + ".gif"))
        {
            pic_extend = ".gif";
        }
        if (isFileExist(pic_path + ".jpeg"))
        {
            pic_extend = ".jpeg";
        }
        if (isFileExist(pic_path + ".jpg"))
        {
            pic_extend = ".jpg";
        }
        if (isFileExist(pic_path + ".png"))
        {
            pic_extend = ".png";
        }
        return pic_extend; 
    }
    
    public static final boolean CopyFile(File in, File out) throws Exception {
        try {
            FileInputStream fis = new FileInputStream(in);
            FileOutputStream fos = new FileOutputStream(out);
            byte[] buf = new byte[1024];
            int i = 0;
            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
            fis.close();
            fos.close();
            return true;
        } catch (IOException ie) {
            ie.printStackTrace();
            return false;
        }
    }
    
    public static final boolean CopyFile(String infile, String outfile) throws Exception {
        try {
            File in = new File(infile);
            File out = new File(outfile);
            return CopyFile(in, out);
        } catch (IOException ie) {
            ie.printStackTrace();
            return false;
        }

    }


}
