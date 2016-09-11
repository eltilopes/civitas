package br.com.civitas.processamento.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import br.com.civitas.arquitetura.controller.AbstractCrudBean;
import br.com.civitas.processamento.entity.Ano;
import br.com.civitas.processamento.entity.Cidade;
import br.com.civitas.processamento.entity.NivelPagamento;
import br.com.civitas.processamento.service.AnoService;
import br.com.civitas.processamento.service.CidadeService;
import br.com.civitas.processamento.service.NivelPagamentoService;

@ManagedBean
@ViewScoped
public class NivelPagamentoBean extends AbstractCrudBean<NivelPagamento, NivelPagamentoService>  implements Serializable{

	private static final long serialVersionUID = 6283001396634682530L;

	@ManagedProperty("#{cidadeService}")
	private CidadeService cidadeService;
	
	@ManagedProperty("#{anoService}")
	private AnoService anoService;
	
	@ManagedProperty("#{nivelPagamentoService}")
	private NivelPagamentoService service;
	
	private List<Cidade> cidades;
	private List<Ano> anos;
	
	@PostConstruct
	public void init(){
		cidades = cidadeService.buscarTodasAtivas();
		anos = anoService.buscarTodos();
	}

	public CidadeService getCidadeService() {
		return cidadeService;
	}

	public void setCidadeService(CidadeService cidadeService) {
		this.cidadeService = cidadeService;
	}

	public List<Cidade> getCidades() {
		return cidades;
	}

	public void setCidades(List<Cidade> cidades) {
		this.cidades = cidades;
	}

	public List<Ano> getAnos() {
		return anos;
	}

	public void setAnos(List<Ano> anos) {
		this.anos = anos;
	}

	public void setAnoService(AnoService anoService) {
		this.anoService = anoService;
	}

	public void setService(NivelPagamentoService service) {
		super.setService(service);
		this.service = service;
	}

}
