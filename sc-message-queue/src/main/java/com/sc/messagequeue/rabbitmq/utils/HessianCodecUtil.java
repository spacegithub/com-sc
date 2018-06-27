package com.sc.messagequeue.rabbitmq.utils;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HessianCodecUtil {  
  
    private static Logger logger = LoggerFactory.getLogger(HessianCodecUtil.class);
  
    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream baos = null;
        HessianOutput output = null;  
        try {  
            baos = new ByteArrayOutputStream(1024);
            output = new HessianOutput(baos);  
            output.startCall();  
            output.writeObject(obj);  
            output.completeCall();  
        } catch (final IOException ex) {
            throw ex;  
        } finally {  
            if (output != null) {  
                try {  
                    baos.close();  
                } catch (final IOException ex) {
                    logger.error("Failed to close stream.", ex);  
                }  
            }  
        }  
        return baos != null ? baos.toByteArray() : null;  
    }  
  
    public static Object deSerialize(byte[] in) throws IOException {
        Object obj = null;
        ByteArrayInputStream bais = null;
        HessianInput input = null;  
        try {  
            bais = new ByteArrayInputStream(in);
            input = new HessianInput(bais);  
            input.startReply();  
            obj = input.readObject();  
            input.completeReply();  
        } catch (final IOException ex) {
            throw ex;  
        } catch (final Throwable e) {
            logger.error("Failed to decode object.", e);  
        } finally {  
            if (input != null) {  
                try {  
                    bais.close();  
                } catch (final IOException ex) {
                    logger.error("Failed to close stream.", ex);  
                }  
            }  
        }  
        return obj;  
    }  
  
}  