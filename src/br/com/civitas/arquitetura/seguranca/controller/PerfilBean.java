package br.com.civitas.arquitetura.seguranca.controller;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import br.com.civitas.arquitetura.base.Perfil;
import br.com.civitas.arquitetura.base.Permissao;
import br.com.civitas.arquitetura.controller.AbstractCrudBean;
import br.com.civitas.arquitetura.seguranca.service.PerfilService;
import br.com.civitas.arquitetura.seguranca.service.PermissaoService;
import br.com.civitas.arquitetura.util.FacesUtils;


@ManagedBean
@ViewScoped
public class PerfilBean extends AbstractCrudBean<Perfil, PerfilService> implements Serializable {

	private static final long serialVersionUID = 5267775445253860893L;

	@ManagedProperty("#{perfilService}")
	protected PerfilService service;

	@ManagedProperty("#{permissaoService}")
	protected PermissaoService permissaoService ;

	private List<Permissao> listaPermissaoDisponiveis;
	private Permissao permissaoAuxiliarInsert;

	public void setService(PerfilService service) {
		super.setService(service);
		this.service = service;
	}

	@Override
	public void prepareUpdate(ActionEvent event) {
		super.prepareUpdate(event);
		listaPermissaoDisponiveis = permissaoService.getListaPermissaoDisponiveis(getEntity());
	}
	
	@Override
	public void prepareInsert(ActionEvent event) {
		super.prepareInsert(event);
		listaPermissaoDisponiveis = permissaoService.buscarTodos();
	}
	
	public void adicionarPermissao(){
		if(Objects.nonNull(permissaoAuxiliarInsert)){
			getEntity().getPermissoes().add(permissaoAuxiliarInsert);
			listaPermissaoDisponiveis.remove(permissaoAuxiliarInsert);
			setPermissaoAuxiliarInsert(null);
		}else{
			FacesUtils.addWarnMessage("Selecione uma Permissão!");
		}
	}
	
	public void removerPermissao(Permissao permissao){
		if(Objects.nonNull(permissao)){
			listaPermissaoDisponiveis.add(permissao);
			getEntity().getPermissoes().remove(permissao);
			setPermissaoAuxiliarInsert(null);
		}
	}
	
	public void setPermissaoService(PermissaoService permissaoService) {
		this.permissaoService = permissaoService;
	}

	public List<Permissao> getListaPermissaoDisponiveis() {
		return listaPermissaoDisponiveis;
	}

	public void setListaPermissaoDisponiveis(List<Permissao> listaPermissaoDisponiveis) {
		this.listaPermissaoDisponiveis = listaPermissaoDisponiveis;
	}

	public Permissao getPermissaoAuxiliarInsert() {
		return permissaoAuxiliarInsert;
	}

	public void setPermissaoAuxiliarInsert(Permissao permissaoAuxiliarInsert) {
		this.permissaoAuxiliarInsert = permissaoAuxiliarInsert;
	}


}
