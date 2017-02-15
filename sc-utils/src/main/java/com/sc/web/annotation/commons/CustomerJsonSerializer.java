package com.sc.web.annotation.commons;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

/**
 * depend on jackson
 * @author Diamond
 */
public class CustomerJsonSerializer {

    static final String DYNC_INCLUDE = "DYNC_INCLUDE";
    static final String DYNC_FILTER = "DYNC_FILTER";
    ObjectMapper mapper = new ObjectMapper();

    @JsonFilter(DYNC_FILTER)
    interface DynamicFilter {
    }

    @JsonFilter(DYNC_INCLUDE)
    interface DynamicInclude {
    }

    /**
     * @param clazz 需要设置规则的Class
     * @param include 转换时包含哪些字段
     * @param filter 转换时过滤哪些字段
     */
    public void filter(Class<?> clazz, String include, String filter) {
        if (clazz == null) return;
        if (include != null && include.length() > 0) {
            mapper.setFilterProvider(new SimpleFilterProvider().addFilter(DYNC_INCLUDE,
                    SimpleBeanPropertyFilter.filterOutAllExcept(include.split(","))));
            mapper.addMixIn(clazz, DynamicInclude.class);
        } else if (filter !=null && filter.length() > 0) {
            mapper.setFilterProvider(new SimpleFilterProvider().addFilter(DYNC_FILTER,
                    SimpleBeanPropertyFilter.serializeAllExcept(filter.split(","))));
            mapper.addMixIn(clazz, DynamicFilter.class);
        }
    }

    public String toJson(Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }
    public void main(String args[]) {
/*        CustomerJsonSerializer cjs= new CustomerJsonSerializer();
        // 设置转换 Article 类时，只包含 id, name
        cjs.filter(Article.class, "id,name", null);

        String include = cjs.toJson(new Article());

        cjs = new CustomerJsonSerializer();
        // 设置转换 Article 类时，过滤掉 id, name
        cjs.filter(Article.class, null, "id,name");

        String filter = cjs.toJson(new Article());

        System.out.println("include: " + include);
        System.out.println("filter: " + filter);*/
    }
}