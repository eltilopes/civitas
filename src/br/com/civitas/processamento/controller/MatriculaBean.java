package br.com.civitas.processamento.controller;

import static br.com.civitas.arquitetura.util.FacesUtils.addErrorMessage;
import static br.com.civitas.arquitetura.util.FacesUtils.addInfoMessage;

import java.io.Serializable;
import java.util.ArrayList;
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
import br.com.civitas.processamento.entity.Matricula;
import br.com.civitas.processamento.entity.NivelPagamento;
import br.com.civitas.processamento.entity.Secretaria;
import br.com.civitas.processamento.entity.Setor;
import br.com.civitas.processamento.service.CidadeService;
import br.com.civitas.processamento.service.MatriculaService;
import br.com.civitas.processamento.service.NivelPagamentoService;
import br.com.civitas.processamento.service.SecretariaService;
import br.com.civitas.processamento.service.SetorService;

@ManagedBean
@ViewScoped
public class MatriculaBean extends AbstractCrudBean<Matricula, MatriculaService> implements Serializable {

	private static final long serialVersionUID = 4449139529084428737L;

	@ManagedProperty("#{cidadeService}")
	private CidadeService cidadeService;
	
	@ManagedProperty("#{secretariaService}")
	private SecretariaService secretariaService;
	
	@ManagedProperty("#{setorService}")
	private SetorService setorService;
	
	@ManagedProperty("#{nivelPagamentoService}")
	private NivelPagamentoService nivelPagamentoService;

	@ManagedProperty("#{matriculaService}")
	private MatriculaService service;

	private List<Cidade> cidades;
	private List<Setor> setoresDisponiveis;
	private List<Setor> setoresSelecionados;
	private List<Secretaria> secretariasDisponiveis;
	private List<Secretaria> secretariasSelecionadas;
	private List<Matricula> listaMatriculaSelecionadas;
	private List<NivelPagamento> niveisDisponiveis;
	private NivelPagamento nivelPagamento;
	
	private boolean todosSelecionados = false;
	
	@PostConstruct
	public void init() {
		cidades = cidadeService.buscarTodasAtivas();
		getEntitySearch().setSecretaria(new Secretaria());
	}

	@SuppressWarnings("unchecked")
	public void prepareAtualizarMatriculas(){
		niveisDisponiveis = nivelPagamentoService.buscarCidade(getEntitySearch().getSecretaria().getCidade());
		if(niveisDisponiveis.isEmpty()){
			FacesUtils.addWarnMessage("Não existe Níveis de Pagamento para a Cidade selecionada!");
		}else{
			listaMatriculaSelecionadas = new ArrayList<Matricula>();
			nivelPagamento = new NivelPagamento();
			List<Matricula> listaMatricula = (List<Matricula>)getResultSearch().getWrappedData();
			for (Matricula m : listaMatricula){
				if(m.isSelecionado()){
					listaMatriculaSelecionadas.add(m);
				}
			}
			setCurrentState(STATE_INSERT);
		}
	}
	
	public void atualizarMatriculasSelecionadas() {
		if(Objects.nonNull(nivelPagamento)){
			for(Matricula m : listaMatriculaSelecionadas){
				m.setNivelPagamento(nivelPagamento);
			}
			try{
				service.updateAll(listaMatriculaSelecionadas );
				addInfoMessage( getMessage( "SUCCESS_ITENS_UPDATE" ) );
				setCurrentState( STATE_SEARCH);
				getEntities().setWrappedData( null );
				setListaMatriculaSelecionadas(null);
			}catch (Exception e) {
				e.printStackTrace();
				addErrorMessage( getMessage( "ERROR_MESSAGE" ) );
			}
		}else{
			FacesUtils.addWarnMessage("Selecione o Nível de Pagamento!");
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public void find(ActionEvent event) {
		if(Objects.nonNull(getEntitySearch().getSecretaria().getCidade())){
			try {
				limpaListas();
				List<Matricula> list = null;
				list = service.getMatriculaPorNomeSetorSecretaria(getEntitySearch(), getSecretariasSelecionadas(), getSetoresSelecionados());
				if (list.isEmpty()) {
					throw new ApplicationException("Consulta sem resultados.");
				}
				getResultSearch().setWrappedData(list);
				setOriginalResult((List<Matricula>) getResultSearch().getWrappedData());
				setCurrentState(STATE_SEARCH);
				setTodosSelecionados(false);
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
	
	@SuppressWarnings("unchecked")
	public boolean habilitarBotaoAtualizarMatricula(){
		List<Matricula> listaMatricula = (List<Matricula>)getResultSearch().getWrappedData();
		if(listaMatricula != null){
			for (Matricula m : listaMatricula){
				if(m.isSelecionado()){
					return true;
				}
			}
		}
		return false;
	}
	
	public void carregarPorCidade() {
		if (Objects.nonNull(getEntitySearch().getSecretaria().getCidade())) {
			setSecretariasDisponiveis(secretariaService.buscarCidade(getEntitySearch().getSecretaria().getCidade()));
			setSetoresDisponiveis(setorService.buscarCidade(getEntitySearch().getSecretaria().getCidade()));
			setSetoresSelecionados(new ArrayList<Setor>());
			setSecretariasSelecionadas(new ArrayList<Secretaria>());
		}
	}
	
	public void selecionarTodos(){
		setMatriculasSelecionadas(todosSelecionados);
	}
	
	@SuppressWarnings("unchecked")
	private void setMatriculasSelecionadas(Boolean selecionado) {
		if(getResultSearch().getWrappedData() != null){
			for(Matricula m : (List<Matricula>)getResultSearch().getWrappedData() ){
					m.setSelecionado(selecionado);
			}
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

	public List<Setor> getSetoresDisponiveis() {
		return setoresDisponiveis;
	}

	public void setSetoresDisponiveis(List<Setor> setoresDisponiveis) {
		this.setoresDisponiveis = setoresDisponiveis;
	}

	public List<Setor> getSetoresSelecionados() {
		return setoresSelecionados;
	}

	public void setSetoresSelecionados(List<Setor> setoresSelecionados) {
		this.setoresSelecionados = setoresSelecionados;
	}

	public List<Secretaria> getSecretariasDisponiveis() {
		return secretariasDisponiveis;
	}

	public void setSecretariasDisponiveis(List<Secretaria> secretariasDisponiveis) {
		this.secretariasDisponiveis = secretariasDisponiveis;
	}

	public List<Secretaria> getSecretariasSelecionadas() {
		return secretariasSelecionadas;
	}

	public void setSecretariasSelecionadas(List<Secretaria> secretariasSelecionadas) {
		this.secretariasSelecionadas = secretariasSelecionadas;
	}

	public void setSecretariaService(SecretariaService secretariaService) {
		this.secretariaService = secretariaService;
	}

	public void setSetorService(SetorService setorService) {
		this.setorService = setorService;
	}

	public boolean isTodosSelecionados() {
		return todosSelecionados;
	}

	public void setTodosSelecionados(boolean todosSelecionados) {
		this.todosSelecionados = todosSelecionados;
	}

	public List<Matricula> getListaMatriculaSelecionadas() {
		return listaMatriculaSelecionadas;
	}

	public void setListaMatriculaSelecionadas(List<Matricula> listaMatriculaSelecionadas) {
		this.listaMatriculaSelecionadas = listaMatriculaSelecionadas;
	}

	public List<NivelPagamento> getNiveisDisponiveis() {
		return niveisDisponiveis;
	}

	public void setNiveisDisponiveis(List<NivelPagamento> niveisDisponiveis) {
		this.niveisDisponiveis = niveisDisponiveis;
	}

	public void setNivelPagamentoService(NivelPagamentoService nivelPagamentoService) {
		this.nivelPagamentoService = nivelPagamentoService;
	}

	public NivelPagamento getNivelPagamento() {
		return nivelPagamento;
	}

	public void setNivelPagamento(NivelPagamento nivelPagamento) {
		this.nivelPagamento = nivelPagamento;
	}

}
