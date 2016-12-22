package br.com.civitas.processamento.controller;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import br.com.civitas.arquitetura.ApplicationException;
import br.com.civitas.arquitetura.controller.AbstractCrudBean;
import br.com.civitas.arquitetura.util.FacesUtils;
import br.com.civitas.processamento.entity.Ano;
import br.com.civitas.processamento.entity.ArquivoPagamento;
import br.com.civitas.processamento.entity.Cidade;
import br.com.civitas.processamento.entity.LogErroProcessador;
import br.com.civitas.processamento.entity.Mes;
import br.com.civitas.processamento.entity.ResumoSetor;
import br.com.civitas.processamento.enums.TipoArquivo;
import br.com.civitas.processamento.factory.FactoryEnuns;
import br.com.civitas.processamento.service.AnoService;
import br.com.civitas.processamento.service.ArquivoPagamentoService;
import br.com.civitas.processamento.service.CidadeService;
import br.com.civitas.processamento.service.EventoService;
import br.com.civitas.processamento.service.LogErroProcessadorService;
import br.com.civitas.processamento.service.MesService;
import br.com.civitas.processamento.utils.ValidarArquivoService;

@ManagedBean
@ViewScoped
public class ArquivoPagamentoBean extends AbstractCrudBean<ArquivoPagamento, ArquivoPagamentoService> implements Serializable {

	private static final long serialVersionUID = -7387329115854340573L;

	@ManagedProperty("#{eventoService}")
	private EventoService eventoService;

	@ManagedProperty("#{arquivoPagamentoService}")
	private ArquivoPagamentoService service;

	@ManagedProperty("#{cidadeService}")
	private CidadeService cidadeService;

	@ManagedProperty("#{anoService}")
	private AnoService anoService;

	@ManagedProperty("#{mesService}")
	private MesService mesService;

	@ManagedProperty("#{validarArquivoService}")
	private ValidarArquivoService validarArquivoService ;

	@ManagedProperty("#{logErroProcessadorService}")
	private LogErroProcessadorService logErroProcessadorService ;

	private ArquivoPagamento arquivo;
	private List<Cidade> cidades;
	private List<Ano> anos;
	private List<Mes> meses;
	private List<TipoArquivo> tiposArquivos;
	private UploadedFile file;
	private List<ResumoSetor> resumos;
	private boolean valoresResumoConferidos;
	private boolean cargosNaoMapeados;
	
	@PostConstruct
	public void init() {
		cidades = cidadeService.buscarTodasAtivas();
		anos = anoService.buscarTodos();
		meses = mesService.buscarTodos();
		tiposArquivos = FactoryEnuns.listaTipoArquivo();
		resumos = new ArrayList<ResumoSetor>();
	}

	@Override
	public void find(ActionEvent event) {
		super.find(event);
		Collections.sort(getOriginalResult(), (ArquivoPagamento a1, ArquivoPagamento a2) -> a2.getDataProcessamento().compareTo(a1.getDataProcessamento()));
	}
	
	public void prepareProcessarArquivo() {
		arquivo = new ArquivoPagamento();
		setCurrentState(STATE_INSERT);
		limpaListas();
	}
	
	@SuppressWarnings("unchecked")
	public void imprimirRelatorio() {
		service.imprimirRelatorio(getEntitySearch(),(List<ArquivoPagamento>)getResultSearch().getWrappedData(), "relatorio_arquivo_pagamento.jasper");;
	}
	
	public void processarArquivo() {
		cargosNaoMapeados = false;
		resumos = new ArrayList<ResumoSetor>();
		valoresResumoConferidos = true;
		try {
			arquivo.setNomeArquivo(new String(file.getFileName().getBytes(Charset.defaultCharset()), "UTF-8"));
			arquivo.setFile(file);
			validarArquivoService.validarArquivo(file, arquivo);
			resumos = service.processarArquivo(arquivo);
			resumos.parallelStream().forEach(r-> {
				if(!r.valoresResumoConferidos()){
					valoresResumoConferidos = r.valoresResumoConferidos(); 
				}
			});
			FacesUtils.addInfoMessage("Arquivo : '"+arquivo.getNomeArquivo()+"' Processado com Sucesso! "+
			arquivo.getCidade().getDescricao()+" - "+arquivo.getMes().getDescricao()+" - "+arquivo.getAno().getAno() );
		} catch (ApplicationException e) {
			if(e.getMessage().equals(ValidarArquivoService.ARQUIVO_COM_CARGOS_NAO_MAPEADOS)){
				cargosNaoMapeados = true;
			}else{
				logger.error(e);
				FacesUtils.addErrorMessage(e.getMessage());
				logErroProcessadorService.save(new LogErroProcessador(arquivo.getNomeArquivo(), e.getMessage()));
			}	
		}catch (Exception e) {
			logger.error(e);
			FacesUtils.addErrorMessage("Erro no processamento. Contate o administrador");
			logErroProcessadorService.save(new LogErroProcessador(arquivo.getNomeArquivo(), e.getMessage()));
		}finally {
			arquivo = new ArquivoPagamento();
		}
	}

	public void updateFile(FileUploadEvent event) {
	    UploadedFile uploadedFile = event.getFile();
	    String fileName = uploadedFile.getFileName();
	    System.out.println(fileName);
	}
	
	public String mensagemCargoNaoMapeado(){
		return ValidarArquivoService.ARQUIVO_COM_CARGOS_NAO_MAPEADOS;
	}
	
	public String getEstiloResumo(){
		return valoresResumoConferidos  ? "label label-success" : "label label-danger";
	}
	
	public String getEstiloLinha(ResumoSetor resumoSetor){
		return resumoSetor.valoresResumoConferidos()  ? "color:#4f4f4f !important;" : "color:#d9534f !important;";
	}

	public void setEventoService(EventoService eventoService) {
		this.eventoService = eventoService;
	}

	public List<Ano> getAnos() {
		return anos;
	}

	public void setAnos(List<Ano> anos) {
		this.anos = anos;
	}

	public List<Mes> getMeses() {
		return meses;
	}

	public void setMeses(List<Mes> meses) {
		this.meses = meses;
	}

	public void setAnoService(AnoService anoService) {
		this.anoService = anoService;
	}

	public void setMesService(MesService mesService) {
		this.mesService = mesService;
	}

	public List<Cidade> getCidades() {
		return cidades;
	}

	public void setCidades(List<Cidade> cidades) {
		this.cidades = cidades;
	}

	public void setCidadeService(CidadeService cidadeService) {
		this.cidadeService = cidadeService;
	}

	public void setValidarArquivoService(ValidarArquivoService validarArquivoService) {
		this.validarArquivoService = validarArquivoService;
	}

	public void setLogErroProcessadorService(LogErroProcessadorService logErroProcessadorService) {
		this.logErroProcessadorService = logErroProcessadorService;
	}

	public void setService(ArquivoPagamentoService service) {
		super.setService(service);
		this.service = service;
	}

	public ArquivoPagamento getArquivo() {
		return arquivo;
	}

	public void setArquivo(ArquivoPagamento arquivo) {
		this.arquivo = arquivo;
	}

	public List<TipoArquivo> getTiposArquivos() {
		return tiposArquivos;
	}

	public void setTiposArquivos(List<TipoArquivo> tiposArquivos) {
		this.tiposArquivos = tiposArquivos;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public List<ResumoSetor> getResumos() {
		return resumos;
	}

	public void setResumos(List<ResumoSetor> resumos) {
		this.resumos = resumos;
	}

	public boolean isValoresResumoConferidos() {
		return valoresResumoConferidos;
	}

	public void setValoresResumoConferidos(boolean valoresResumoConferidos) {
		this.valoresResumoConferidos = valoresResumoConferidos;
	}

	public boolean isCargosNaoMapeados() {
		return cargosNaoMapeados;
	}

	public void setCargosNaoMapeados(boolean cargosNaoMapeados) {
		this.cargosNaoMapeados = cargosNaoMapeados;
	}
	
	public ValidarArquivoService getValidarArquivoService() {
		return validarArquivoService;
	}

}
