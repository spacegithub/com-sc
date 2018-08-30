package com.sc.socket.utils;

import com.sc.socket.utils.hutool.FileUtil;
import com.sc.socket.utils.hutool.StrUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class TioTools {
	private static Logger log = LoggerFactory.getLogger(TioTools.class);
	private static Set<String> acceptedExt = new HashSet<>();
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

	public static void main(String[] args) {
//		newProject();
										tio();
		//						addBlankFile("F:\\gitee");
		//		deleteGits(new String[] { "F:\\gitee", "D:\\svn_nb" });
//		addBlankFile("D:\\work\\tio-im");
//		deleteFiles(new String[] { "D:\\work\\tio-im" }, new String[] { ".settings", ".classpath", ".project", ".externalToolBuilders" });

		//		AtomicInteger count = new AtomicInteger();
		//		findFile("D:\\work", "g-m-db", count);
		//		System.out.println("共找到" + count + "个文件及目录");
	}

	/**
	 * 
	 *
	 * @创建时间:　2016年6月29日 下午2:47:09
	 */
	public TioTools() {
	}

	//	@SuppressWarnings("rawtypes")
	//	private static SynThreadPoolExecutor threadExecutor = new SynThreadPoolExecutor(40, 120, "quickstart-thread-pool");

	private static int modifiedCount = 0;

	//	private static int renameCount = 0;

	/**
	 * 
	 * @param rootDirStr
	 * @param old2newFilename 可以为空或null
	 * @throws IOException
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
	 * tio版本升级
	 * 
	 *
	 */
	public static void tio() {
		String[] rootDirStrs = new String[] { "D:\\svn_nb\\base", "G:\\work", "D:\\work\\dts", "D:\\work\\tio-webpack", "D:\\svn_nb\\nbyb", "D:\\work\\t-io",
				"D:\\svn_nb\\fire", "D:\\work\\tio-start", "D:\\svn_nb\\media", "F:\\gitee" };
		for (String rootDirStr : rootDirStrs) {
			Map<String, String> old2newStr = new HashMap<>();
			Map<String, String> old2newFilename = new HashMap<>();

			old2newStr.put("3.1.8.v20180818-RELEASE", "3.1.9.v20180828-RELEASE");

			old2newFilename.putAll(old2newStr);

			try {
				//如果需要修改文件名字，就在这里调一下rename
				//				rename(rootDirStr, old2newFilename);

				//替换文字
				replaceStr(rootDirStr, old2newStr);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	public static void newProject() {
		String[] rootDirStrs = new String[] { "F:\\gitee\\java\\projects\\im-platform" };

		for (String rootDirStr : rootDirStrs) {
			Map<String, String> old2newStr = new HashMap<>();
			Map<String, String> old2newFilename = new HashMap<>();

			String oldName = "live";
			String newName = "im";
			old2newStr.put("tio-" + oldName, "tio-" + newName); //中划线
			old2newStr.put("tio_" + oldName, "tio_" + newName); //下划线
			old2newStr.put("TIO-" + oldName.toUpperCase(), "TIO-" + newName.toUpperCase()); //中划线
			old2newStr.put("TIO_" + oldName.toUpperCase(), "TIO_" + newName.toUpperCase()); //下划线

			old2newFilename.putAll(old2newStr);

			try {
				//如果需要修改文件名字，就在这里调一下rename
				rename(rootDirStr, old2newFilename);

				//替换文字
				replaceStr(rootDirStr, old2newStr);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * 给空目录添加一个空白文件
	 * @param rootDirStr
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
	 * @param rootDirStrs
	 * @param filenames
	 *
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
	 * @param rootDirStr
	 *
	 */
	@SuppressWarnings("unused")
	public static void deleteFile(String rootDirStr, String[] filenames) {
		File rootDir = new File(rootDirStr);
		File[] files = rootDir.listFiles();

		if (files.length == 0) {

		} else {
			lab1: for (int i = 0; i < files.length; i++) {
				try {
					File file = files[i];
					String absolutePath = file.getAbsolutePath();

					lab2: for (int j = 0; j < filenames.length; j++) {
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
