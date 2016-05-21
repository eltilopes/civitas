package br.com.civitas.arquitetura.service;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource(value={"classpath:br/com/civitas/arquitetura/service/spring-security.xml"})
public class SecurityConfig {}
