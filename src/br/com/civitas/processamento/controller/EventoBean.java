package br.com.civitas.processamento.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import br.com.civitas.arquitetura.controller.AbstractCrudBean;
import br.com.civitas.processamento.entity.Cidade;
import br.com.civitas.processamento.entity.Evento;
import br.com.civitas.processamento.enums.TipoArquivo;
import br.com.civitas.processamento.factory.FactoryEnuns;
import br.com.civitas.processamento.service.CidadeService;
import br.com.civitas.processamento.service.EventoService;

@ManagedBean
@ViewScoped
public class EventoBean extends AbstractCrudBean<Evento, EventoService>  implements Serializable{

	private static final long serialVersionUID = -6180806637584035401L;

	@ManagedProperty("#{eventoService}")
	private EventoService service;
	
	@ManagedProperty("#{cidadeService}")
	private CidadeService cidadeService;
	
	private List<Cidade> cidades;
	private List<TipoArquivo> tiposArquivos;
	
	@PostConstruct
	public void init(){
		cidades = cidadeService.buscarTodos();
		tiposArquivos = FactoryEnuns.listaTipoArquivo();
	}

	public List<Cidade> getCidades() {
		return cidades;
	}

	public void setCidades(List<Cidade> cidades) {
		this.cidades = cidades;
	}

	public List<TipoArquivo> getTiposArquivos() {
		return tiposArquivos;
	}

	public void setTiposArquivos(List<TipoArquivo> tiposArquivos) {
		this.tiposArquivos = tiposArquivos;
	}

	public void setService(EventoService service) {
		super.setService(service);
		this.service = service;
	}

	public void setCidadeService(CidadeService cidadeService) {
		this.cidadeService = cidadeService;
	}
	
}
