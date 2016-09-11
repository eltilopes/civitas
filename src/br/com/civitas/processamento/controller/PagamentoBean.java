package br.com.civitas.processamento.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.com.civitas.arquitetura.ApplicationException;
import br.com.civitas.arquitetura.controller.AbstractCrudBean;
import br.com.civitas.arquitetura.util.FacesUtils;
import br.com.civitas.processamento.entity.Ano;
import br.com.civitas.processamento.entity.ArquivoPagamento;
import br.com.civitas.processamento.entity.CargaHorariaPagamento;
import br.com.civitas.processamento.entity.Cargo;
import br.com.civitas.processamento.entity.Cidade;
import br.com.civitas.processamento.entity.Evento;
import br.com.civitas.processamento.entity.EventoPagamento;
import br.com.civitas.processamento.entity.Mes;
import br.com.civitas.processamento.entity.NivelPagamento;
import br.com.civitas.processamento.entity.Pagamento;
import br.com.civitas.processamento.entity.Secretaria;
import br.com.civitas.processamento.entity.Setor;
import br.com.civitas.processamento.entity.UnidadeTrabalho;
import br.com.civitas.processamento.service.AnoService;
import br.com.civitas.processamento.service.CargaHorariaPagamentoService;
import br.com.civitas.processamento.service.CargoService;
import br.com.civitas.processamento.service.CidadeService;
import br.com.civitas.processamento.service.EventoPagamentoService;
import br.com.civitas.processamento.service.EventoService;
import br.com.civitas.processamento.service.MesService;
import br.com.civitas.processamento.service.NivelPagamentoService;
import br.com.civitas.processamento.service.PagamentoService;
import br.com.civitas.processamento.service.SecretariaService;
import br.com.civitas.processamento.service.SetorService;
import br.com.civitas.processamento.service.UnidadeTrabalhoService;
import br.com.civitas.processamento.vo.PagamentoVO;

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

	@ManagedProperty("#{secretariaService}")
	private SecretariaService secretariaService;

	@ManagedProperty("#{eventoService}")
	private EventoService eventoService;

	@ManagedProperty("#{eventoPagamentoService}")
	private EventoPagamentoService eventoPagamentoService;

	@ManagedProperty("#{setorService}")
	private SetorService setorService;

	@ManagedProperty("#{unidadeTrabalhoService}")
	private UnidadeTrabalhoService unidadeTrabalhoService;

	@ManagedProperty("#{nivelPagamentoService}")
	private NivelPagamentoService nivelPagamentoService;

	@ManagedProperty("#{cargaHorariaPagamentoService}")
	private CargaHorariaPagamentoService cargaHorariaPagamentoService;

	@ManagedProperty("#{cargoService}")
	private CargoService cargoService;

	private List<Cidade> cidades;
	private List<Ano> anos;
	private List<Mes> meses;
	private List<Cargo> cargos;
	private List<Setor> setores;
	private List<Secretaria> secretarias;
	private List<UnidadeTrabalho> unidadesTrabalho;
	private List<NivelPagamento> niveisPagamento;
	private List<CargaHorariaPagamento> cargasHorariaPagamento;
	private List<Evento> eventosDisponiveis;
	private List<Evento> eventosSelecionados;
	private List<Map<String, Object>> pagamentosMap;
	private List<String> pagamentosColumnsMap;
	private Map<String, Double> somatorioValores;
	
	private Setor setor;
	private Secretaria secretaria;
	private Cargo cargo;
	private UnidadeTrabalho unidadeTrabalho;
	private NivelPagamento nivelPagamento;
	private CargaHorariaPagamento cargaHorariaPagamento;
	private StreamedContent arquivoExcel;
	
	@PostConstruct
	public void init() {
		cidades = cidadeService.buscarTodasAtivas();
		anos = anoService.buscarTodos();
		meses = mesService.buscarTodos();
		getEntitySearch().setArquivo(new ArquivoPagamento());
	}

	@Override
	public void cancel(ActionEvent event) {
		super.cancel(event);
		getEntitySearch().setArquivo(new ArquivoPagamento());
	}

	public void carregarPorCidade() {
		if (Objects.nonNull(getEntitySearch().getArquivo().getCidade())) {
			setSecretarias(secretariaService.buscarCidade(getEntitySearch().getArquivo().getCidade()));
			setSetores(setorService.buscarCidade(getEntitySearch().getArquivo().getCidade()));
			setCargos(cargoService.buscarCidade(getEntitySearch().getArquivo().getCidade()));
			setCargos(cargoService.buscarCidade(getEntitySearch().getArquivo().getCidade()));
			setUnidadesTrabalho(unidadeTrabalhoService.buscarCidade(getEntitySearch().getArquivo().getCidade()));
			setNiveisPagamento(nivelPagamentoService.buscarCidade(getEntitySearch().getArquivo().getCidade()));
			setCargasHorariaPagamento(cargaHorariaPagamentoService.buscarCidade(getEntitySearch().getArquivo().getCidade()));
			setEventosDisponiveis(eventoService.buscarCidade(getEntitySearch().getArquivo().getCidade()));
			setEventosSelecionados(new ArrayList<Evento>());
		}
	}

	@Override
	public void find(ActionEvent event) {
		try {
			List<Pagamento> list = null;
			list = service.getPagamentoPorArquivo(getEntitySearch().getArquivo(), cargo, secretaria, setor,
					unidadeTrabalho, nivelPagamento, cargaHorariaPagamento);
			popularEventosSelecionados(list);
			if (list.isEmpty()) {
				throw new ApplicationException("Consulta sem resultados.");
			}
			setCurrentState(STATE_SEARCH);
		} catch (ApplicationException e) {
			e.printStackTrace();
			FacesUtils.addWarnMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			FacesUtils.addErrorMessage(getMessage("ERROR_MESSAGE"));
		}
	}

	private void popularEventosSelecionados(List<Pagamento> list) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		pagamentosMap  = new ArrayList<Map<String, Object>>();
		pagamentosColumnsMap = getListFields();
		somatorioValores = new HashMap<String, Double>();
		for(Pagamento pagamento : list){
			setTotaisValores(pagamento.getTotalProventos(), PagamentoVO.proventosColuna());
			pagamento.setEventosPagamentoSelecionados(new ArrayList<EventoPagamento>());
			if(Objects.nonNull(eventosSelecionados) && !eventosSelecionados.isEmpty()){
				int i = 0;
				for(Evento evento : eventosSelecionados){
					for(EventoPagamento ep : pagamento.getEventosPagamento()){
						if(evento.getChave().equals(ep.getEvento().getChave())){
							pagamento.getEventosPagamentoSelecionados().add(ep);
							setTotaisValores(ep.getValor(), evento.getNome());
						}
					}
					if(pagamento.getEventosPagamentoSelecionados().size() == i++){
						pagamento.getEventosPagamentoSelecionados().add(getEventoValorZerado(evento));
					}
				}	
			}
			pagamentosMap.add( getMapValueFields(pagamento));
		}
	}

	private void setTotaisValores(Double valor, String chave) {
		if(!somatorioValores.containsKey(chave)){
			somatorioValores.put(chave, 0.0); 
		}
		somatorioValores.put(chave, somatorioValores.get(chave) + valor); 
	}
	
	public Double getSomatorio(String chave) {
		return somatorioValores.containsKey(chave) ? somatorioValores.get(chave) : null;
	}

	public String getObject(String chave, HashMap<String, Object> mapa) {
		return (String) mapa.get(chave);
	}
	
	private Map<String, Object> getMapValueFields(Pagamento pagamento) throws IllegalArgumentException, IllegalAccessException {
		Map<String, Object> pagamentosMap = new HashMap<String, Object>();
		PagamentoVO pagamentoVO = new PagamentoVO(pagamento);
		Field[] declaredFields = pagamentoVO.getClass().getDeclaredFields();
		for (Field field : declaredFields) {
			pagamentosMap.put(PagamentoVO.getNomeColuna(field.getName()), field.get(pagamentoVO));
		}
		if(Objects.nonNull(eventosSelecionados) && !eventosSelecionados.isEmpty()){
			for (EventoPagamento eventoPagamento : pagamento.getEventosPagamentoSelecionados()) {
				pagamentosMap.put(eventoPagamento.getEvento().getChave(),String.format("%.2f", eventoPagamento.getValor()) );
			}
		}
		return pagamentosMap;
	}

	private List<String> getListFields() throws IllegalArgumentException, IllegalAccessException {
		List<String> colunas = new ArrayList<String>();
		PagamentoVO pagamentoVO = new PagamentoVO(true);
		Field[] declaredFields = pagamentoVO.getClass().getDeclaredFields();
		for (Field field : declaredFields) {
			colunas.add((String) field.get(pagamentoVO));
		}
		if(Objects.nonNull(eventosSelecionados) && !eventosSelecionados.isEmpty()){
			for (Evento evento : eventosSelecionados) {
				colunas.add((String) evento.getNome());
			}
		}
		return colunas;
	}

	private EventoPagamento getEventoValorZerado(Evento evento) {
		EventoPagamento eventoPagamento = new EventoPagamento();
		eventoPagamento.setEvento(evento);
		eventoPagamento.setValor(0d);
		return eventoPagamento;
	}

	@SuppressWarnings("resource")
	public void exportarExcel() {

		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet firstSheet = workbook.createSheet("Pagamentos");
		String nomeArquivo = "pagamento.xls";
		
		try {
			int numeroLinha = 0;
			HSSFRow row = firstSheet.createRow(numeroLinha++);
			int numeroCelulaColuna = 0;
			boolean primeiraColuna = true;
			for(String nomeColuna : pagamentosColumnsMap){
				row = firstSheet.getRow(0);
				row.createCell(numeroCelulaColuna).setCellValue(nomeColuna);
				for (Map<String, Object> mapa : pagamentosMap) {
					row = primeiraColuna ? firstSheet.createRow(numeroLinha++) : firstSheet.getRow(numeroLinha++);
					row.createCell(numeroCelulaColuna).setCellValue(getObject(nomeColuna,(HashMap<String, Object>) mapa));
				}
				numeroCelulaColuna++;
				numeroLinha = 1;
				primeiraColuna = false;
			}	

		  File out = new File("file.xls");
		  workbook.write( new FileOutputStream(out));
		  setArquivoExcel(new DefaultStreamedContent(new FileInputStream(out), 
		    		"application/vnd.ms-excel", nomeArquivo));

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Erro ao exportar arquivo");
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

	public CargaHorariaPagamento getCargaHorariaPagamento() {
		return cargaHorariaPagamento;
	}

	public void setCargaHorariaPagamento(CargaHorariaPagamento cargaHorariaPagamento) {
		this.cargaHorariaPagamento = cargaHorariaPagamento;
	}

	public void setCargaHorariaPagamentoService(CargaHorariaPagamentoService cargaHorariaPagamentoService) {
		this.cargaHorariaPagamentoService = cargaHorariaPagamentoService;
	}

	public List<Setor> getSetores() {
		return setores;
	}

	public void setSetores(List<Setor> setores) {
		this.setores = setores;
	}

	public List<Secretaria> getSecretarias() {
		return secretarias;
	}

	public void setSecretarias(List<Secretaria> secretarias) {
		this.secretarias = secretarias;
	}

	public List<Cargo> getCargos() {
		return cargos;
	}

	public void setCargos(List<Cargo> cargos) {
		this.cargos = cargos;
	}

	public Setor getSetor() {
		return setor;
	}

	public void setSetor(Setor setor) {
		this.setor = setor;
	}

	public Secretaria getSecretaria() {
		return secretaria;
	}

	public void setSecretaria(Secretaria secretaria) {
		this.secretaria = secretaria;
	}

	public Cargo getCargo() {
		return cargo;
	}

	public void setCargo(Cargo cargo) {
		this.cargo = cargo;
	}

	public UnidadeTrabalho getUnidadeTrabalho() {
		return unidadeTrabalho;
	}

	public void setUnidadeTrabalho(UnidadeTrabalho unidadeTrabalho) {
		this.unidadeTrabalho = unidadeTrabalho;
	}

	public void setUnidadeTrabalhoService(UnidadeTrabalhoService unidadeTrabalhoService) {
		this.unidadeTrabalhoService = unidadeTrabalhoService;
	}

	public void setSecretariaService(SecretariaService secretariaService) {
		this.secretariaService = secretariaService;
	}

	public void setSetorService(SetorService setorService) {
		this.setorService = setorService;
	}

	public void setCargoService(CargoService cargoService) {
		this.cargoService = cargoService;
	}

	public List<UnidadeTrabalho> getUnidadesTrabalho() {
		return unidadesTrabalho;
	}

	public void setUnidadesTrabalho(List<UnidadeTrabalho> unidadesTrabalho) {
		this.unidadesTrabalho = unidadesTrabalho;
	}

	public List<NivelPagamento> getNiveisPagamento() {
		return niveisPagamento;
	}

	public void setNiveisPagamento(List<NivelPagamento> niveisPagamento) {
		this.niveisPagamento = niveisPagamento;
	}

	public void setNivelPagamentoService(NivelPagamentoService nivelPagamentoService) {
		this.nivelPagamentoService = nivelPagamentoService;
	}

	public void setEventoService(EventoService eventoService) {
		this.eventoService = eventoService;
	}

	public void setEventoPagamentoService(EventoPagamentoService eventoPagamentoService) {
		this.eventoPagamentoService = eventoPagamentoService;
	}

	public NivelPagamento getNivelPagamento() {
		return nivelPagamento;
	}

	public void setNivelPagamento(NivelPagamento nivelPagamento) {
		this.nivelPagamento = nivelPagamento;
	}

	public List<CargaHorariaPagamento> getCargasHorariaPagamento() {
		return cargasHorariaPagamento;
	}

	public void setCargasHorariaPagamento(List<CargaHorariaPagamento> cargasHorariaPagamento) {
		this.cargasHorariaPagamento = cargasHorariaPagamento;
	}

	public List<Evento> getEventosDisponiveis() {
		return eventosDisponiveis;
	}

	public void setEventosDisponiveis(List<Evento> eventosDisponiveis) {
		this.eventosDisponiveis = eventosDisponiveis;
	}

	public List<Evento> getEventosSelecionados() {
		return eventosSelecionados;
	}

	public void setEventosSelecionados(List<Evento> eventosSelecionados) {
		this.eventosSelecionados = eventosSelecionados;
	}

	public StreamedContent getArquivoExcel() {
		return arquivoExcel;
	}

	public void setArquivoExcel(StreamedContent arquivoExcel) {
		this.arquivoExcel = arquivoExcel;
	}

	public List<Map<String, Object>> getPagamentosMap() {
		return pagamentosMap;
	}

	public void setPagamentosMap(List<Map<String, Object>> pagamentosMap) {
		this.pagamentosMap = pagamentosMap;
	}

	public List<String> getPagamentosColumnsMap() {
		return pagamentosColumnsMap;
	}

	public void setPagamentosColumnsMap(List<String> pagamentosColumnsMap) {
		this.pagamentosColumnsMap = pagamentosColumnsMap;
	}

	public Map<String, Double> getSomatorioValores() {
		return somatorioValores;
	}

	public void setSomatorioValores(Map<String, Double> somatorioValores) {
		this.somatorioValores = somatorioValores;
	}

}
