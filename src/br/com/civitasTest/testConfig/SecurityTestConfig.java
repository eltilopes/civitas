package br.com.civitasTest.testConfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;


@Configuration
@ImportResource(value={"classpath:br/com/civitasTest/testConfig/spring-test-security.xml"})
public class SecurityTestConfig {

	
}
