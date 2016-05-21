package br.com.civitas.arquitetura.service;

import org.springframework.context.ApplicationContext;

public class SpringUtil {

	private static ApplicationContext applicationContext;
	
	public static Object getBean(String beanName) {
		return applicationContext.getBean(beanName);
	}
	
    public static void setApplicationContext(final ApplicationContext context)  {
        applicationContext = context;
    }

}
