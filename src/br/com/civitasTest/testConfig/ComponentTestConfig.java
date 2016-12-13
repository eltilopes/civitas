package br.com.civitasTest.testConfig;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages={"br.com.civitas","br.com.civitasTest"}, excludeFilters={ @Filter(Configuration.class)} )
@PropertySource(value={"classpath:application.test.properties","classpath:hibernate.properties","classpath:persistence.xml"})
public class ComponentTestConfig {
	//TODO: Atentar-se a essas configuraçoes
}