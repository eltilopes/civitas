package br.com.civitas.acesso;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@ManagedBean(eager = true, name = "environmentBean")
@ApplicationScoped
public class EnvironmentBean {

	@ManagedProperty("#{environmentService}")
	private EnvironmentService service;
	public void setService(EnvironmentService service) { this.service = service; }
	
	public boolean isDesenvolvimento(){
		return service.isDesenvolvimento();
	}
	
	public boolean isHomologacao(){
		return service.isHomologacao();
	}
	
	public boolean isProducao(){
		return service.isProducao();
	}
}

@Service
class EnvironmentService {
	
	private @Value("${dataSource.url}") String dataSourceUrl;
	private @Value("${dataSource.key_dsv}") String dataSourceKeyDsv;
	private @Value("${dataSource.key_hmg}") String dataSourceKeyHmg;
	private @Value("${dataSource.key_prod}") String dataSourceKeyProd;
	
	public boolean isDesenvolvimento() {
		return StringUtils.contains(dataSourceUrl, dataSourceKeyDsv);
	}
	
	public boolean isHomologacao() {
		return StringUtils.contains(dataSourceUrl, dataSourceKeyHmg);
	}
	
	public boolean isProducao() {
		return StringUtils.contains(dataSourceUrl, dataSourceKeyProd);
	}
}