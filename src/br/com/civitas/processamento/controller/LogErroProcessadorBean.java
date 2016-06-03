package br.com.civitas.processamento.controller;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import br.com.civitas.arquitetura.controller.AbstractCrudBean;
import br.com.civitas.processamento.entity.LogErroProcessador;
import br.com.civitas.processamento.service.LogErroProcessadorService;

@ManagedBean
@ViewScoped
public class LogErroProcessadorBean extends AbstractCrudBean<LogErroProcessador, LogErroProcessadorService> implements Serializable {

	private static final long serialVersionUID = 7076337548470510564L;

	@ManagedProperty("#{logErroProcessadorService}")
	private LogErroProcessadorService service;

	public void setService(LogErroProcessadorService service) {
		super.setService(service);
		this.service = service;
	}

}
