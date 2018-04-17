package com.sc.utils.spring;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.PropertyResourceConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.support.PropertiesLoaderSupport;
import org.springframework.web.context.ContextLoader;

import java.lang.reflect.Method;
import java.util.Properties;


public class SpringPropertyResourceReader {
	private static ApplicationContext applicationContext = ContextLoader.getCurrentWebApplicationContext();
	private static AbstractApplicationContext abstractContext = (AbstractApplicationContext) applicationContext;
	private static Properties properties = new Properties();
	static {
		try {
			
			String[] postProcessorNames = abstractContext.getBeanNamesForType(BeanFactoryPostProcessor.class, true,
					true);

			for (String ppName : postProcessorNames) {
				
				BeanFactoryPostProcessor beanProcessor = abstractContext.getBean(ppName,
						BeanFactoryPostProcessor.class);
				
				
				
				if (beanProcessor instanceof PropertyResourceConfigurer) {
					PropertyResourceConfigurer propertyResourceConfigurer = (PropertyResourceConfigurer) beanProcessor;

					
					
					Method mergeProperties = PropertiesLoaderSupport.class.getDeclaredMethod("mergeProperties");
					
					mergeProperties.setAccessible(true);
					Properties props = (Properties) mergeProperties.invoke(propertyResourceConfigurer);

					
					
					Method convertProperties = PropertyResourceConfigurer.class.getDeclaredMethod("convertProperties",
							Properties.class);
					
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
