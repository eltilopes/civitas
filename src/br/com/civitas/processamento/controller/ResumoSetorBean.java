package br.com.civitas.processamento.controller;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import br.com.civitas.arquitetura.ApplicationException;
import br.com.civitas.arquitetura.controller.AbstractCrudBean;
import br.com.civitas.arquitetura.util.FacesUtils;
import br.com.civitas.processamento.entity.ArquivoPagamento;
import br.com.civitas.processamento.entity.Cidade;
import br.com.civitas.processamento.entity.ResumoSetor;
import br.com.civitas.processamento.service.ArquivoPagamentoService;
import br.com.civitas.processamento.service.CidadeService;
import br.com.civitas.processamento.service.ResumoSetorService;

@ManagedBean
@ViewScoped
public class ResumoSetorBean extends AbstractCrudBean<ResumoSetor, ResumoSetorService> implements Serializable {

	private static final long serialVersionUID = 4449139529084428737L;

	@ManagedProperty("#{cidadeService}")
	private CidadeService cidadeService;
	
	@ManagedProperty("#{resumoSetorService}")
	private ResumoSetorService service;

	@ManagedProperty("#{arquivoPagamentoService}")
	private ArquivoPagamentoService arquivoPagamentoService;

	private Cidade cidade;
	private ResumoSetor resumoSetorTotal;
	private List<Cidade> cidades;
	private List<ArquivoPagamento> arquivoPagamentos;
	
	@PostConstruct
	public void init() {
		cidades = cidadeService.buscarTodasAtivas();
	}
	
	public void carregarPorCidade() {
		if (Objects.nonNull(getCidade())) {
			arquivoPagamentos = arquivoPagamentoService.buscarPorCidade(getCidade());
		}
	}
	
	@SuppressWarnings("unchecked")
	public void find(ActionEvent event) {
		if(Objects.nonNull(getEntitySearch().getArquivoPagamento()) && Objects.nonNull(getCidade())){
			try {
				limpaListas();
				List<ResumoSetor> resumos = null;
				resumos = service.buscarPorArquivoPagamento(getEntitySearch().getArquivoPagamento());
				if (resumos.isEmpty()) {
					throw new ApplicationException("Consulta sem resultados.");
				}
				getResultSearch().setWrappedData(resumos);
				setOriginalResult((List<ResumoSetor>) getResultSearch().getWrappedData());
				carregarResumoTotal(resumos);
				setCurrentState(STATE_SEARCH);
			} catch (ApplicationException e) {
				e.printStackTrace();
				FacesUtils.addWarnMessage(e.getMessage());
				limpaListas();
			} catch (Exception e) {
				e.printStackTrace();
				FacesUtils.addErrorMessage(getMessage("ERROR_MESSAGE"));
				limpaListas();
			}
		}else{
			FacesUtils.addWarnMessage("Selecione a Cidade, Ano e Mês para Consulta.");
			limpaListas();
		}
	}
	
	private void carregarResumoTotal(List<ResumoSetor> resumos) {
		resumoSetorTotal = new ResumoSetor();
		resumoSetorTotal.setQuantidadePagamentos(resumos.stream().mapToInt(r -> r.getQuantidadePagamentos()).sum());
		resumoSetorTotal.setTotalDescontos(resumos.stream().mapToDouble(r -> r.getTotalDescontos()).sum());
		resumoSetorTotal.setTotalLiquido(resumos.stream().mapToDouble(r -> r.getTotalLiquido()).sum());
		resumoSetorTotal.setTotalProventos(resumos.stream().mapToDouble(r -> r.getTotalProventos()).sum());
		resumoSetorTotal.setTotalRemuneracao(resumos.stream().mapToDouble(r -> r.getTotalRemuneracao()).sum());
		resumoSetorTotal.setSomatorioDescontos(resumos.stream().mapToDouble(r -> r.getSomatorioDescontos()).sum());
		resumoSetorTotal.setSomatorioLiquido(resumos.stream().mapToDouble(r -> r.getSomatorioLiquido()).sum());
		resumoSetorTotal.setSomatorioProventos(resumos.stream().mapToDouble(r -> r.getSomatorioProventos()).sum());
		resumoSetorTotal.setSomatorioRemuneracao(resumos.stream().mapToDouble(r -> r.getSomatorioRemuneracao()).sum());
	}

	public String getEstiloLinha(ResumoSetor resumoSetor){
		return resumoSetor.valoresResumoConferidos()  ? "color:#4f4f4f !important;" : "color:#d9534f !important;";
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

	public void setService(ResumoSetorService service) {
		super.setService(service);
		this.service = service;
	}

	public void setArquivoPagamentoService(ArquivoPagamentoService arquivoPagamentoService) {
		this.arquivoPagamentoService = arquivoPagamentoService;
	}

	public List<ArquivoPagamento> getArquivoPagamentos() {
		return arquivoPagamentos;
	}

	public void setArquivoPagamentos(List<ArquivoPagamento> arquivoPagamentos) {
		this.arquivoPagamentos = arquivoPagamentos;
	}

	public Cidade getCidade() {
		return cidade;
	}

	public void setCidade(Cidade cidade) {
		this.cidade = cidade;
	}

	public ResumoSetor getResumoSetorTotal() {
		return resumoSetorTotal;
	}

	public void setResumoSetorTotal(ResumoSetor resumoSetorTotal) {
		this.resumoSetorTotal = resumoSetorTotal;
	}

}
