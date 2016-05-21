package br.com.civitas.arquitetura.base.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class MailConfig {
	
	@Autowired
	private Environment environment;
	
	public String getUsername(){
		return environment.getProperty("mail.username");
	}
	
	public String getPassword(){
		return environment.getProperty("mail.password");
	}
	
	public String getHost(){
		return environment.getProperty("mail.host");
	}
	
	public Integer getPort(){
		return Integer.parseInt(environment.getProperty("mail.port"));
	}
	
	public String getProtocol(){
		return environment.getProperty("mail.transport.protocol");
	}
	
	public String getHostname(){
		return environment.getProperty("hostname");
	}
	
	public Boolean getStmpAuth(){
		return environment.getProperty("mail.smtp.auth").equals("true") ? true : false;
	}

	public Boolean getEnviaEmail() {
		return environment.getProperty("envia.email").equals("true") ? true : false;
	}
}