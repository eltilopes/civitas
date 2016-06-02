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
import br.com.civitas.processamento.entity.Ano;
import br.com.civitas.processamento.entity.ArquivoPagamento;
import br.com.civitas.processamento.entity.Cidade;
import br.com.civitas.processamento.entity.Mes;
import br.com.civitas.processamento.entity.Pagamento;
import br.com.civitas.processamento.service.AnoService;
import br.com.civitas.processamento.service.CidadeService;
import br.com.civitas.processamento.service.MesService;
import br.com.civitas.processamento.service.PagamentoService;

@ManagedBean
@ViewScoped
public class PagamentoBean extends AbstractCrudBean<Pagamento, PagamentoService> implements Serializable {

	private static final long serialVersionUID = 7039521079739010349L;

	@ManagedProperty("#{pagamentoService}")
	private PagamentoService service;

	@ManagedProperty("#{cidadeService}")
	private CidadeService cidadeService;

	@ManagedProperty("#{anoService}")
	private AnoService anoService;

	@ManagedProperty("#{mesService}")
	private MesService mesService;

	private List<Cidade> cidades;
	private List<Ano> anos;
	private List<Mes> meses;

	@PostConstruct
	public void init() {
		cidades = cidadeService.buscarTodos();
		anos = anoService.buscarTodos();
		meses = mesService.buscarTodos();
		getEntitySearch().setArquivo(new ArquivoPagamento());
	}

	@Override
	public void cancel(ActionEvent event) {
		super.cancel(event);
		getEntitySearch().setArquivo(new ArquivoPagamento());
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void find(ActionEvent event) {
		try{
			limpaListas();
			List<Pagamento> list = null;
			list = service.getPagamentoPorArquivo(getEntitySearch().getArquivo());
			if( list.isEmpty() ){
				throw new ApplicationException( "Consulta sem resultados." );
			}
			getResultSearch().setWrappedData(list);
			setOriginalResult( (List<Pagamento>)getResultSearch().getWrappedData() );
			setCurrentState(STATE_SEARCH);
		}catch (ApplicationException e) {
			e.printStackTrace();
			FacesUtils.addWarnMessage( e.getMessage() );
		}catch (Exception e) {
			e.printStackTrace();
			FacesUtils.addErrorMessage( getMessage( "ERROR_MESSAGE" ) );
		}
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

	public void setService(PagamentoService service) {
		super.setService(service);
		this.service = service;
	}

}
