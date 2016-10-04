package br.com.civitas.processamento.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import br.com.civitas.arquitetura.ApplicationException;
import br.com.civitas.arquitetura.controller.AbstractCrudBean;
import br.com.civitas.arquitetura.util.FacesUtils;
import br.com.civitas.processamento.entity.Cidade;
import br.com.civitas.processamento.entity.Matricula;
import br.com.civitas.processamento.entity.Secretaria;
import br.com.civitas.processamento.service.CidadeService;
import br.com.civitas.processamento.service.MatriculaService;

@ManagedBean
@ViewScoped
public class MatriculaBean extends AbstractCrudBean<Matricula, MatriculaService> implements Serializable {

	private static final long serialVersionUID = 4449139529084428737L;

	@ManagedProperty("#{cidadeService}")
	private CidadeService cidadeService;

	@ManagedProperty("#{matriculaService}")
	private MatriculaService service;

	private List<Cidade> cidades;

	@PostConstruct
	public void init() {
		cidades = cidadeService.buscarTodasAtivas();
		getEntitySearch().setSecretaria(new Secretaria());
	}

	@SuppressWarnings("unchecked")
	public void find(ActionEvent event) {
		try {
			limpaListas();
			List<Matricula> list = null;
			list = service.getMatriculaPorNomeCidade(getEntitySearch());
			if (list.isEmpty()) {
				throw new ApplicationException("Consulta sem resultados.");
			}
			getResultSearch().setWrappedData(list);
			setOriginalResult((List<Matricula>) getResultSearch().getWrappedData());
			setCurrentState(STATE_SEARCH);
		} catch (ApplicationException e) {
			e.printStackTrace();
			FacesUtils.addWarnMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			FacesUtils.addErrorMessage(getMessage("ERROR_MESSAGE"));
		}
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

	public void setService(MatriculaService service) {
		super.setService(service);
		this.service = service;
	}

}
