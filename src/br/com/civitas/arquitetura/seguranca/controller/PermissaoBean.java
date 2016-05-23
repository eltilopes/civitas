package br.com.civitas.arquitetura.seguranca.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import br.com.civitas.arquitetura.base.GrupoPermissao;
import br.com.civitas.arquitetura.base.Permissao;
import br.com.civitas.arquitetura.controller.AbstractCrudBean;
import br.com.civitas.arquitetura.seguranca.service.GrupoPermissaoService;
import br.com.civitas.arquitetura.seguranca.service.PermissaoService;


@ManagedBean
@ViewScoped
public class PermissaoBean extends AbstractCrudBean<Permissao, PermissaoService> implements Serializable {

	private static final long serialVersionUID = 2910488110927950368L;

	@ManagedProperty("#{permissaoService}")
	protected PermissaoService service;
	
	@ManagedProperty("#{grupoPermissaoService}")
	protected GrupoPermissaoService grupoPermissaoService;

	private List<GrupoPermissao> gruposPermissao;

	@PostConstruct
	public void init() {
		setGruposPermissao(grupoPermissaoService.buscarTodos());
	}
	
	@Override
	public void setService(PermissaoService service) {
		super.setService(service);
		this.service = service;
	}

	public void setGrupoPermissaoService(GrupoPermissaoService grupoPermissaoService) {
		this.grupoPermissaoService = grupoPermissaoService;
	}

	public List<GrupoPermissao> getGruposPermissao() {
		return gruposPermissao;
	}

	public void setGruposPermissao(List<GrupoPermissao> gruposPermissao) {
		this.gruposPermissao = gruposPermissao;
	}
}
