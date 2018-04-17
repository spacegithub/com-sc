package com.sc.utils.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
	
	public static void ListZipFile(String path) throws Exception {
		ZipFile zipFile = new ZipFile(path);
		Enumeration<? extends ZipEntry> zipEntrys = zipFile.entries();
		while (zipEntrys.hasMoreElements()) {
			ZipEntry zipEntry = zipEntrys.nextElement();
			System.out.println(zipEntry.getName());
		}
		zipFile.close();
	}

	
	public static void ZipFileText(String path) throws Exception {
		ZipFile zipFile = new ZipFile(path);
		ZipEntry zipEntry = zipFile.getEntry("META-INF/MANIFEST.MF");
		InputStream in = zipFile.getInputStream(zipEntry);
		int c = -1;
		while ((c = in.read()) != -1) {
			System.out.print((char) (c & 0XFF));
		}
		if (in != null) {
			in.close();
			in = null;
		}
		zipFile.close();
		System.out.println();
	}

	
	public static String Zip(String path, File file) throws IOException {
		if (file == null) {
			return null;
		}
		String zipFileName = "";
		if (file.getName().indexOf(".") > -1) {
			zipFileName = file.getName().substring(0,
					file.getName().indexOf("."))
					+ ".zip";
		} else {
			zipFileName = file.getName() + ".zip";
		}
		String zipFullName = path + File.separator + zipFileName;
		byte[] data = new byte[1024 * 2];
		FileInputStream fis = new FileInputStream(file);
		FileOutputStream fos = new FileOutputStream(zipFullName);
		ZipOutputStream zipOS = new ZipOutputStream(fos);
		zipOS.setMethod(ZipOutputStream.DEFLATED);
		zipOS.putNextEntry(new ZipEntry(file.getName()));
		int length = 0;
		while ((length = fis.read(data)) != -1) {
			zipOS.write(data, 0, length);
		}
		zipOS.finish();
		zipOS.close();
		fos.close();
		fis.close();
		return zipFileName;
	}

	
	public static String Zip(String path, String zipName, File[] files)
			throws IOException {
		if (files == null) {
			return null;
		}
		String zipFullName = path + File.separator + zipName;
		byte[] data = new byte[1024];
		FileOutputStream fos = new FileOutputStream(zipFullName);
		for (File file : files) {
			FileInputStream fis = new FileInputStream(file);
			ZipOutputStream zipOS = new ZipOutputStream(fos);
			zipOS.setMethod(ZipOutputStream.DEFLATED);
			zipOS.putNextEntry(new ZipEntry(file.getName()));
			int length = 0;
			while ((length = fis.read(data)) != -1) {
				zipOS.write(data, 0, length);
			}
			zipOS.finish();
			zipOS.close();
			fis.close();
		}
		fos.close();
		return zipFullName;
	}

	
	public static void unzip(File zipFilename, String outputDirectory)
			throws Exception {
		try {
			File outFile = new File(outputDirectory);
			if (!outFile.exists()) {
				outFile.mkdirs();
			}
			ZipFile zipFile = new ZipFile(zipFilename);
			Enumeration<? extends ZipEntry> en = zipFile.entries();
			ZipEntry zipEntry = null;
			while (en.hasMoreElements()) {
				zipEntry = en.nextElement();
				String zipEntryString = zipEntry.toString();
				if (zipEntry.isDirectory()) {
					continue;
				}
				if (zipEntryString.indexOf("/") > 0) {
					String dirName = zipEntry.getName();
					dirName = dirName.substring(0, dirName.lastIndexOf("/"));
					File f1 = new File(outFile.getPath() + File.separator
							+ dirName);
					f1.mkdirs();
					dirName = zipEntry.getName();
					File f2 = new File(outFile.getPath() + File.separator
							+ dirName);
					f2.createNewFile();
					copy(f2, zipFile, zipEntry);
				} else {
					File f = new File(outFile.getPath() + File.separator
							+ zipEntry.getName());
					f.createNewFile();
					copy(f, zipFile, zipEntry);
				}
			}
		} catch (Exception e) {
			e = new Exception("解压zip文件出错!");
			throw e;
		}
	}

	
	private static void copy(File f, ZipFile zipFile, ZipEntry zipEntry)
			throws IOException {
		InputStream in = zipFile.getInputStream(zipEntry);
		FileOutputStream out = new FileOutputStream(f);
		try {
			int c;
			byte[] by = new byte[1024];
			while ((c = in.read(by)) != -1) {
				out.write(by, 0, c);
			}
		} catch (IOException e) {
			throw e;
		} finally {
			if (out != null) {
				out.close();
				out = null;
			}
			if (in != null) {
				in.close();
				in = null;
			}
		}
	}
}
