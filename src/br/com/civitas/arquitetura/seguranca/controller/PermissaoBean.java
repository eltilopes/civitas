package br.com.civitas.arquitetura.seguranca.controller;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import br.com.civitas.arquitetura.base.Permissao;
import br.com.civitas.arquitetura.controller.AbstractCrudBean;
import br.com.civitas.arquitetura.seguranca.service.PermissaoService;


@ManagedBean
@ViewScoped
public class PermissaoBean extends AbstractCrudBean<Permissao, PermissaoService> implements Serializable {

	private static final long serialVersionUID = 2910488110927950368L;

	@ManagedProperty("#{permissaoService}")
	protected PermissaoService service;
	
	@Override
	public void setService(PermissaoService service) {
		super.setService(service);
		this.service = service;
	}

}