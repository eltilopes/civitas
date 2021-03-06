package br.com.civitas.processamento.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import br.com.civitas.arquitetura.controller.AbstractCrudBean;
import br.com.civitas.arquitetura.util.FacesUtils;
import br.com.civitas.processamento.entity.Cargo;
import br.com.civitas.processamento.entity.Cidade;
import br.com.civitas.processamento.enums.TipoArquivo;
import br.com.civitas.processamento.factory.FactoryEnuns;
import br.com.civitas.processamento.service.CargoService;
import br.com.civitas.processamento.service.CidadeService;

@ManagedBean
@ViewScoped
public class CargoBean extends AbstractCrudBean<Cargo, CargoService>  implements Serializable{

	private static final long serialVersionUID = 4449139529084428737L;

	@ManagedProperty("#{cidadeService}")
	private CidadeService cidadeService;
	
	@ManagedProperty("#{cargoService}")
	private CargoService service;
	
	private List<Cidade> cidades;
	private List<TipoArquivo> tiposArquivos;
	private List<Cargo> cargosInativos;
	private boolean existeCargosInativos = false;
	
	@PostConstruct
	public void init(){
		cidades = cidadeService.buscarTodasAtivas();
		tiposArquivos = FactoryEnuns.listaTipoArquivo();
		verificarCargosInativos();
	}

	private void verificarCargosInativos() {
		setExisteCargosInativos(service.existeCargosInativos());
	}

	@Override
	public void find(ActionEvent event) {
		setCargosInativos(new ArrayList<>());
		setExisteCargosInativos(service.existeCargosInativos());
		super.find(event);
	}
	
	@Override
	public void cancel(ActionEvent event) {
		setExisteCargosInativos(service.existeCargosInativos());
		super.cancel(event);
	}
	
	public void visuaizarCargosInativos(){
		limpaListas();
		setCargosInativos(service.buscarTodosInativos(getEntitySearch()));
	}
	
	@Override
	public void executeUpdate() {
		getEntity().setAtivo(true);
		getEntity().setLinhaCargo(null);
		getEntity().setDescricao(getEntity().getDescricao().trim());
		getEntity().setNumero(getEntity().getDescricao().substring(0,2).hashCode());
		if(service.existeCargosInativos(getEntity())){
			super.executeUpdate();
		}else{
			FacesUtils.addInfoMessage("Esse cargo j? estava cadastrado!");
			setCurrentState( STATE_SEARCH );
		}
		apagarCargosSemelhantes();
		visuaizarCargosInativos();
	}
	
	private void apagarCargosSemelhantes() {
		List<Cargo> cargosSemelhantes = service.buscarTipoArquivoCidadeDescricao(getEntity().getCidade(), getEntity().getTipoArquivo(), getEntity().getDescricao());
		List<Cargo> cargosNaoSemelhantes = new ArrayList<Cargo>();
		for(Cargo cargo : cargosSemelhantes){
			String numeroMatricula = cargo.getLinhaCargo().substring(cargo.getLinhaCargo().length() -7, cargo.getLinhaCargo().length()).trim();
			String descricaoCargoSemelhante = cargo.getLinhaCargo().substring(
							cargo.getLinhaCargo().indexOf(getEntity().getDescricao()), 
							cargo.getLinhaCargo().indexOf(numeroMatricula)).trim();
			if(!descricaoCargoSemelhante.equals(getEntity().getDescricao())){
				cargosNaoSemelhantes.add(cargo);
			}
		}
		cargosSemelhantes.removeAll(cargosNaoSemelhantes);
		service.removeAll(cargosSemelhantes);
	}

	public void prepararUpdate(Cargo cargoInativo) {
		setCurrentState(STATE_EDIT);
		setEntity(cargoInativo);
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
	
	public List<TipoArquivo> getTiposArquivos() {
		return tiposArquivos;
	}

	public void setTiposArquivos(List<TipoArquivo> tiposArquivos) {
		this.tiposArquivos = tiposArquivos;
	}

	public boolean isExisteCargosInativos() {
		return existeCargosInativos;
	}

	public void setExisteCargosInativos(boolean existeCargosInativos) {
		this.existeCargosInativos = existeCargosInativos;
	}

	public void setService(CargoService service) {
		super.setService(service);
		this.service = service;
	}

	public List<Cargo> getCargosInativos() {
		return cargosInativos;
	}

	public void setCargosInativos(List<Cargo> cargosInativos) {
		this.cargosInativos = cargosInativos;
	}

}
