package com.sc.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.ConnectionInfo;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

/**
 * <一句话功能简述> <功能详细描述>
 *
 * @auth:qiss
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class ScpCopyUtils {
    private static final Logger logger = LoggerFactory.getLogger(ScpCopyUtils.class);
    private Connection con;
    private Session session;
    private SCPClient scpClient;

    public ScpCopyUtils(String hostIP, String userName, String password ) {
        con = new Connection(hostIP);
        try {
            ConnectionInfo connectionInfo= con.connect();
            boolean isAuthed = con.authenticateWithPassword(userName, password);
            if (!isAuthed) {
                logger.error("鉴权失败,登录{},用户名{},密码{}失败!", hostIP, userName, password);
                con = null;
            }
            scpClient=con.createSCPClient();
            session = con.openSession();
        } catch (Exception e) {
            logger.error("登录远程服务器异常!", e);
        }
    }

    /**
     * 从远端copy文件到本地目录
     * @param remotePath 远端文件地址:列子:/root/testor/aa.txt
     * @param localPath 本地文件目录:列子:/root/testor   D:\\testdir
     * @return
     */
    public boolean copyRemoteFileToLocal(String remotePath, String localPath) {
        try {
            scpClient.get(remotePath, localPath);
        } catch (Exception e) {
            logger.error("获取远程文件错误!", e);
            return false;
        }
        return true;
    }

    /**
     * 从本地复制文件到远端目录
     * @param remotePath 远端目录: /root/testor/
     * @param localPath  本地文件地址:/root/testor/aa.txt
     * @return
     */
    public boolean copyLocalFileToRemote(String remotePath, String localPath) {
        try {
            scpClient.put(remotePath, localPath);
        } catch (Exception e) {
            logger.error("上传文件到远程目录错误!", e);
            return false;
        }
        return true;
    }

    /**
     * 从本地文件流复制到远端目录
     * @param byts  文件流
     * @param fileName 文件名称
     * @param remoteDirectory 远程地址 /root/testor/
     * @return
     */
    public boolean copyLocalFileByteToRemote(byte[] byts, String fileName, String remoteDirectory) {
        try {
            scpClient.put(byts, fileName, remoteDirectory);
        } catch (Exception e) {
            logger.error("上传文件到远程目录错误!", e);
            return false;
        }
        return true;
    }

    /**
     * 执行远端命令并返回结果
     * @param command  要执行的远端命令  ls   cd
     * @return  返回结果
     */
    public String exec(String command) {
        try {
            session.execCommand(command);
            InputStream stdout = new StreamGobbler(session.getStdout());

            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
            StringBuilder stringBuilder = new StringBuilder("执行"+command+"命令返回结果:\n");
            while (true) {
                String line = br.readLine();
                if (line == null)
                    break;
                stringBuilder.append(line).append("\n");
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            logger.error("执行远程命令错误!", e);
            return "";
        }
    }

    /**
     * 关闭远程连接
     * @return
     */
    public boolean close() {
        try {
            con.close();
            session.close();
        } catch (Exception e) {
            logger.error("关闭连接失败!", e);
            return false;
        } finally {
            con.close();
            session.close();
        }
        return true;
    }


}
