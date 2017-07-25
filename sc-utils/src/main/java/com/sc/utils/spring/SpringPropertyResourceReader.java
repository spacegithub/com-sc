package com.sc.utils.spring;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.PropertyResourceConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.support.PropertiesLoaderSupport;
import org.springframework.web.context.ContextLoader;

import java.lang.reflect.Method;
import java.util.Properties;

/**
 * 可直接使用此工具类获取spring中的配置文件
 * 注意:如果spring配置为懒加载实例则@postconstruct不起作用,在@postconstruct使用此类会获取到空值
 */
public class SpringPropertyResourceReader {
	private static ApplicationContext applicationContext = ContextLoader.getCurrentWebApplicationContext();
	private static AbstractApplicationContext abstractContext = (AbstractApplicationContext) applicationContext;
	private static Properties properties = new Properties();
	static {
		try {
			// get the names of BeanFactoryPostProcessor
			String[] postProcessorNames = abstractContext.getBeanNamesForType(BeanFactoryPostProcessor.class, true,
					true);

			for (String ppName : postProcessorNames) {
				// get the specified BeanFactoryPostProcessor
				BeanFactoryPostProcessor beanProcessor = abstractContext.getBean(ppName,
						BeanFactoryPostProcessor.class);
				// check whether the beanFactoryPostProcessor is
				// instance of the PropertyResourceConfigurer
				// if it is yes then do the process otherwise continue
				if (beanProcessor instanceof PropertyResourceConfigurer) {
					PropertyResourceConfigurer propertyResourceConfigurer = (PropertyResourceConfigurer) beanProcessor;

					// get the method mergeProperties
					// in class PropertiesLoaderSupport
					Method mergeProperties = PropertiesLoaderSupport.class.getDeclaredMethod("mergeProperties");
					// get the props
					mergeProperties.setAccessible(true);
					Properties props = (Properties) mergeProperties.invoke(propertyResourceConfigurer);

					// get the method convertProperties
					// in class PropertyResourceConfigurer
					Method convertProperties = PropertyResourceConfigurer.class.getDeclaredMethod("convertProperties",
							Properties.class);
					// convert properties
					convertProperties.setAccessible(true);
					convertProperties.invoke(propertyResourceConfigurer, props);

					properties.putAll(props);
				}
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String getProperty(String propertyName) {
		return properties.getProperty(propertyName);
	}
}
