package com.sc.hessian.client.proxy;

import com.alibaba.fastjson.JSONObject;
import com.caucho.hessian.client.HessianConnection;
import com.caucho.hessian.client.HessianProxy;
import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.client.HessianRuntimeException;
import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.HessianDebugOutputStream;
import com.sc.base.api.header.HessianHeaderLocal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

/**
 * 实现简单的多URL轮循
 *
 *
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class HessianProxyDynamic extends HessianProxy {

    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(HessianProxy.class.getName());
    private static Logger logger= LoggerFactory.getLogger(HessianProxyDynamic.class);
    //轮循URL
    private AtomicInteger atomicInteger=new AtomicInteger(0);
    private List<URL> hessianUrls =new ArrayList();

    /**
     * 重构方法
     * @param urls
     * @param factory
     * @param type
     */
    public HessianProxyDynamic(String urls, HessianProxyFactory factory,Class<?> type){
        super(null, factory,null);
        initRoundUrl(urls);
    }

    /**
     * 初始化url
     * @param url
     */
    public void initRoundUrl(String url) {
        String[] urls = StringUtils.tokenizeToStringArray(url, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
        try {
            for (String urlStr:urls){
                hessianUrls.add(new URL(urlStr));
            }
        } catch (MalformedURLException e) {
            throw new HessianRuntimeException("Not Valid URL: +" +url);
        }
    }

    /**
     * 计算当前应当读取哪个URL
     * @return
     */
    private URL roundUrl(){
        //如果只有一条记录则直接获取第一条
        if (1==hessianUrls.size()){
            return hessianUrls.get(0);
        }
        int urls= atomicInteger.getAndIncrement();
        int index=urls%hessianUrls.size();
        return hessianUrls.get(index);
    }

    @Override
    protected HessianConnection sendRequest(String methodName, Object[] args) throws IOException {
        URL url=roundUrl();
        long startTime=System.currentTimeMillis();
        logger.info("Hessian url:{} ,methodName:{}", url, methodName);
        HessianConnection conn =  _factory.getConnectionFactory().open(url);
        boolean isValid = false;

        try {
            addRequestHeaders(conn);

            OutputStream os = null;

            try {
                os = conn.getOutputStream();
            } catch (Exception e) {
                throw new HessianRuntimeException(e);
            }

            if (log.isLoggable(Level.FINEST)) {
                PrintWriter dbg = new PrintWriter(new LogWriter(log));
                HessianDebugOutputStream dOs = new HessianDebugOutputStream(os, dbg);
                dOs.startTop2();
                os = dOs;
            }

            AbstractHessianOutput out = _factory.getHessianOutput(os);

            out.call(methodName, args);
            out.flush();

            conn.sendRequest();

            isValid = true;
            monitor(url.toString(),methodName,args,startTime);
            return conn;
        } finally {
            if (! isValid && conn != null){
                conn.destroy();
            }
        }

    }

    /**
     * 在请求头部增加部分信息
     * @param conn
     */
    @Override
    protected void addRequestHeaders(HessianConnection conn) {
        HessianHeaderLocal.addHeader(conn);
        super.addRequestHeaders(conn);
    }

    /**
     * 监控Hessian接口性能信息
     * @param url
     * @param methodName
     * @param args
     * @param startTime
     */
    private void monitor(String url,String methodName, Object[] args,long startTime){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("url",url);
        jsonObject.put("methodName",methodName);
        jsonObject.put("params",JSONObject.toJSONString(args));
        jsonObject.put("costTime",(System.currentTimeMillis()-startTime));
        jsonObject.put("header",JSONObject.toJSONString(HessianHeaderLocal.getHeaderInfo()));
        logger.info("monitor:{}",jsonObject.toJSONString());
    }

    static class LogWriter extends Writer {
        private java.util.logging.Logger _log;
        private Level _level = Level.FINEST;
        private StringBuilder _sb = new StringBuilder();

        LogWriter(java.util.logging.Logger log)
        {
            _log = log;
        }

        public void write(char ch)
        {
            if (ch == '\n' && _sb.length() > 0) {
                _log.fine(_sb.toString());
                _sb.setLength(0);
            }
            else
                _sb.append((char) ch);
        }

        public void write(char []buffer, int offset, int length)
        {
            for (int i = 0; i < length; i++) {
                char ch = buffer[offset + i];

                if (ch == '\n' && _sb.length() > 0) {
                    _log.log(_level, _sb.toString());
                    _sb.setLength(0);
                }
                else
                    _sb.append((char) ch);
            }
        }

        public void flush()
        {
        }

        public void close()
        {
            if (_sb.length() > 0)
                _log.log(_level, _sb.toString());
        }
    }

}
