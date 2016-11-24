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
import br.com.civitas.processamento.entity.Cidade;
import br.com.civitas.processamento.entity.ResumoSetor;
import br.com.civitas.processamento.entity.Secretaria;
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

	private List<Cidade> cidades;
	private Secretaria secretaria;
	
	@PostConstruct
	public void init() {
		cidades = cidadeService.buscarTodasAtivas();
		setSecretaria(new Secretaria());
	}
	
	@SuppressWarnings("unchecked")
	public void find(ActionEvent event) {
		if(Objects.nonNull(getSecretaria().getCidade())){
			try {
				limpaListas();
				List<ResumoSetor> list = null;
				list = service.buscarPorCidade(getSecretaria().getCidade());
				if (list.isEmpty()) {
					throw new ApplicationException("Consulta sem resultados.");
				}
				getResultSearch().setWrappedData(list);
				setOriginalResult((List<ResumoSetor>) getResultSearch().getWrappedData());
				setCurrentState(STATE_SEARCH);
			} catch (ApplicationException e) {
				e.printStackTrace();
				FacesUtils.addWarnMessage(e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
				FacesUtils.addErrorMessage(getMessage("ERROR_MESSAGE"));
			}
		}else{
			FacesUtils.addWarnMessage("Selecione uma cidade para Consulta.");
		}
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

	public Secretaria getSecretaria() {
		return secretaria;
	}

	public void setSecretaria(Secretaria secretaria) {
		this.secretaria = secretaria;
	}

}
