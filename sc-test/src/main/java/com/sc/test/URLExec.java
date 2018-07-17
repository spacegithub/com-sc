package com.sc.test;

/**
 * <一句话功能简述>
 * <功能详细描述>
 */
public class URLExec {
    private static URLExec _instance;
    private String ip;
    private String url;
    private String port;

    public URLExec(String ip, String port, String url) {
        this.ip = ip;
        this.port = port;
        this.url = url;
    }

    public static URLExec instance(String ip, String port, String url) {
        if (_instance == null) {
            _instance = new URLExec(ip, port, url);
        }

        return _instance;
    }

    public static URLExec instance(String port, String url) {
        return instance("127.0.0.1", port, url);
    }

    public URLExec changeURL(String url) {
        return new URLExec(this.ip,this.port,url);
    }

    @Override
    public String toString() {
        return "http://" + ip + ":" + port + url;
    }


}
