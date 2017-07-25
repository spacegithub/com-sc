package com.sc.utils.web.annotation.handler;


import com.sc.utils.web.annotation.JSON;
import com.sc.utils.web.annotation.commons.CustomerJsonSerializer;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

/**
 * 使用规则
 * 1.controller配置
 * <p> @JSON(type = Article.class, filter="createTime")
 * public Article get(@PathVariable String id) {
 * return articleService.get(id);
 * }
 *
 * @RequestMapping(value="list", method = RequestMethod.GET)
 * @JSON(type = Article.class  , include="id,title") public List<Article> findAll() { return
 * articleService.findAll(); } </p>
 *
 * 2.springMVC配置 <p>
 *     <mvc:return-value-handlers>
 *           <bean class="ResponseJsonHandlerReturnReslover"/>
 *     </mvc:return-value-handlers>
 * </p>
 */
public class ResponseJsonHandlerReturnReslover implements HandlerMethodReturnValueHandler {

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        // 如果有我们自定义的 JSON 注解 就用我们这个Handler 来处理
        boolean hasJsonAnno = returnType.getMethodAnnotation(JSON.class) != null;
        return hasJsonAnno;
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest) throws Exception {
        // 设置这个就是最终的处理类了，处理完不再去找下一个类进行处理
        mavContainer.setRequestHandled(true);

        // 获得注解并执行filter方法 最后返回
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        Annotation[] annos = returnType.getMethodAnnotations();
        CustomerJsonSerializer jsonSerializer = new CustomerJsonSerializer();
        List<Annotation> annotationList = Arrays.asList(annos);
        for (Annotation a : annotationList) {
            if (a instanceof JSON) {
                JSON json = (JSON) a;
                jsonSerializer.filter(json.type(), json.include(), json.filter());
            }
        }
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        String json = jsonSerializer.toJson(returnValue);
        response.getWriter().write(json);
    }
}