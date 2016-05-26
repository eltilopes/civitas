package br.com.civitas.arquitetura.seguranca.controller;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import br.com.civitas.arquitetura.base.GrupoPermissao;
import br.com.civitas.arquitetura.controller.AbstractCrudBean;
import br.com.civitas.arquitetura.seguranca.service.GrupoPermissaoService;


@ManagedBean
@ViewScoped
public class GrupoPermissaoBean extends AbstractCrudBean<GrupoPermissao, GrupoPermissaoService> implements Serializable {

	private static final long serialVersionUID = 5267775445253860893L;

	@ManagedProperty("#{grupoPermissaoService}")
	protected GrupoPermissaoService service;

	public void setService(GrupoPermissaoService service) {
		super.setService(service);
		this.service = service;
	}


}
