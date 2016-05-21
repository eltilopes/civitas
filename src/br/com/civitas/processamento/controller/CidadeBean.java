package br.com.civitas.processamento.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import br.com.civitas.arquitetura.controller.AbstractCrudBean;
import br.com.civitas.processamento.entity.Cidade;
import br.com.civitas.processamento.entity.Estado;
import br.com.civitas.processamento.service.CidadeService;
import br.com.civitas.processamento.service.EstadoService;

@ManagedBean
@ViewScoped
public class CidadeBean extends AbstractCrudBean<Cidade, CidadeService>  implements Serializable{

	private static final long serialVersionUID = -2444140578365266044L;

	@ManagedProperty("#{estadoService}")
	private EstadoService estadoService;
	
	@ManagedProperty("#{cidadeService}")
	private CidadeService service;
	
	private List<Estado> estados;
	
	@PostConstruct
	public void init(){
		estados = estadoService.buscarTodos();
	}
	
	public List<Estado> getEstados() {
		return estados;
	}

	public void setEstados(List<Estado> estados) {
		this.estados = estados;
	}

	public void setEstadoService(EstadoService estadoService) {
		this.estadoService = estadoService;
	}

	public EstadoService getEstadoService() {
		return estadoService;
	}

	public CidadeService getService() {
		return service;
	}

	public void setService(CidadeService service) {
		super.setService(service);
		this.service = service;
	}

}
