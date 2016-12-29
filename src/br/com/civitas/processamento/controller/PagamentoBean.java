package br.com.civitas.processamento.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.primefaces.event.data.PageEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.com.civitas.arquitetura.ApplicationException;
import br.com.civitas.arquitetura.controller.AbstractCrudBean;
import br.com.civitas.arquitetura.util.FacesUtils;
import br.com.civitas.processamento.entity.ArquivoPagamento;
import br.com.civitas.processamento.entity.CargaHorariaPagamento;
import br.com.civitas.processamento.entity.Cargo;
import br.com.civitas.processamento.entity.Cidade;
import br.com.civitas.processamento.entity.Evento;
import br.com.civitas.processamento.entity.EventoPagamento;
import br.com.civitas.processamento.entity.NivelPagamento;
import br.com.civitas.processamento.entity.Pagamento;
import br.com.civitas.processamento.entity.Secretaria;
import br.com.civitas.processamento.entity.Setor;
import br.com.civitas.processamento.entity.UnidadeTrabalho;
import br.com.civitas.processamento.service.ArquivoPagamentoService;
import br.com.civitas.processamento.service.CargaHorariaPagamentoService;
import br.com.civitas.processamento.service.CargoService;
import br.com.civitas.processamento.service.CidadeService;
import br.com.civitas.processamento.service.EventoPagamentoService;
import br.com.civitas.processamento.service.EventoService;
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

	@ManagedProperty("#{arquivoPagamentoService}")
	private ArquivoPagamentoService arquivoPagamentoService;

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
	private List<Cargo> cargosDisponiveis;
	private List<Cargo> cargosSelecionados;
	private List<Setor> setoresDisponiveis;
	private List<Setor> setoresSelecionados;
	private List<Secretaria> secretariasDisponiveis;
	private List<Secretaria> secretariasSelecionadas;
	private List<UnidadeTrabalho> unidadesSelecionadas;
	private List<UnidadeTrabalho> unidadesDisponiveis;
	private List<NivelPagamento> niveisSelecionados;
	private List<NivelPagamento> niveisDisponiveis;
	private List<CargaHorariaPagamento> cargasDisponiveis;
	private List<CargaHorariaPagamento> cargasSelecionados;
	private List<Evento> eventosDisponiveis;
	private List<Evento> eventosSelecionados;
	private List<Map<String, Object>> pagamentosMap;
	private List<String> pagamentosColumnsMap;
	private Map<String, Double> somatorioValores;
	private Map<String, Double> somatorioValoresPaginacao;
	
	private Cidade cidade;
	private List<ArquivoPagamento> arquivoPagamentos;
	
	private StreamedContent arquivoExcel;
	
	@PostConstruct
	public void init() {
		cidades = cidadeService.buscarCidadesComArquivosProcessados();
		pagamentosMap  = new ArrayList<Map<String, Object>>();
		getEntitySearch().setArquivo(new ArquivoPagamento());
	}

	@Override
	public void cancel(ActionEvent event) {
		super.cancel(event);
		getEntitySearch().setArquivo(new ArquivoPagamento());
	}

	public void carregarPorCidade() {
		if (Objects.nonNull(getCidade())) {
			arquivoPagamentos = arquivoPagamentoService.buscarPorCidade(getCidade());
			setSecretariasDisponiveis(secretariaService.buscarCidade(getCidade()));
			setSetoresDisponiveis(setorService.buscarCidade(getCidade()));
			setCargosDisponiveis(cargoService.buscarCidade(getCidade()));
			setUnidadesDisponiveis(unidadeTrabalhoService.buscarCidade(getCidade()));
			setNiveisDisponiveis(nivelPagamentoService.buscarCidade(getCidade()));
			setCargasDisponiveis(cargaHorariaPagamentoService.buscarCidade(getCidade()));
			setEventosDisponiveis(eventoService.buscarCidade(getCidade()));
			setEventosSelecionados(getEventosDisponiveis());
			setCargosSelecionados(new ArrayList<Cargo>());
			setSetoresSelecionados(new ArrayList<Setor>());
			setCargasSelecionados(new ArrayList<CargaHorariaPagamento>());
			setNiveisSelecionados(new ArrayList<NivelPagamento>());
			setUnidadesSelecionadas(new ArrayList<UnidadeTrabalho>());
			setSecretariasSelecionadas(new ArrayList<Secretaria>());
		}
	}

	@Override
	public void find(ActionEvent event) {
		if(Objects.nonNull(getEntitySearch().getArquivo()) && Objects.nonNull(getCidade())){
			try {
				List<Pagamento> list = null;
				list = service.getPagamentoPorArquivo(getEntitySearch().getArquivo(), cargosSelecionados, secretariasSelecionadas, setoresSelecionados,
						unidadesSelecionadas, niveisSelecionados, cargasSelecionados);
	
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
		}else{
			FacesUtils.addWarnMessage("Selecione a Cidade, Ano e Mês para Consulta.");
		}	
	}

	private void popularEventosSelecionados(List<Pagamento> list) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		pagamentosMap  = new ArrayList<Map<String, Object>>();
		pagamentosColumnsMap = getListFields();
		somatorioValores = new HashMap<String, Double>();
		somatorioValoresPaginacao = new HashMap<String, Double>();
		for(Pagamento pagamento : list){
			setTotaisValores(pagamento.getTotalProventos(), PagamentoVO.proventosColuna());
			pagamento.setEventosPagamentoSelecionados(new ArrayList<EventoPagamento>());
			if(Objects.nonNull(eventosSelecionados) && !eventosSelecionados.isEmpty()){
				for(Evento evento : eventosSelecionados){
					Double valorEvento = 0D;
					EventoPagamento eventoPagamentoZerado = getEventoValorZerado(evento);
					pagamento.getEventosPagamentoSelecionados().add(eventoPagamentoZerado);
					for(EventoPagamento ep : pagamento.getEventosPagamento()){
						if(evento.getChave().equals(ep.getEvento().getChave())){
							adicionarValorEventoPagamento(pagamento, ep, eventoPagamentoZerado);
							valorEvento = ep.getValor();
							setTotaisValores(valorEvento, evento.getNome());
						}
					}
					if(valorEvento.equals(0D)){
						setTotaisValores(valorEvento, evento.getNome());
					}
				}	
			}
			pagamentosMap.add( getMapValueFields(pagamento));
		}
	}

	private void adicionarValorEventoPagamento(Pagamento pagamento, EventoPagamento ep, EventoPagamento eventoPagamentoZerado) {
		EventoPagamento eventoPagamento = ep;
		int index = pagamento.getEventosPagamentoSelecionados().indexOf(eventoPagamentoZerado);
		if(index != -1){
			eventoPagamento = pagamento.getEventosPagamentoSelecionados().get(index);
			eventoPagamento.setValor(eventoPagamento.getValor() + ep.getValor());
		}
		pagamento.getEventosPagamentoSelecionados().add(eventoPagamento);
	}

	private void setTotaisValores(Double valor, String chave) {
		if(!somatorioValores.containsKey(chave)){
			somatorioValores.put(chave, 0.00); 
		}
		somatorioValores.put(chave, somatorioValores.get(chave) + valor); 
	}
	
	public Double getSomatorio(String chave) {
		return somatorioValores.containsKey(chave) ? somatorioValores.get(chave) : null;
	}
	
	public Double getSomatorioPaginacao(String chave) {
		return somatorioValoresPaginacao.containsKey(chave) ? somatorioValoresPaginacao.get(chave) : null;
	}

	public String getObject(String chave, HashMap<String, Object> mapa) {
		String value = (String) mapa.get(chave);
		Locale locale = new Locale("pt", "BR");
        NumberFormat nf = NumberFormat.getInstance(locale);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        if(value != null && !chave.equals(PagamentoVO.diasTrabalhadosColuna())
        		&& !chave.equals(PagamentoVO.numeroMatriculaColuna())
        		&& !chave.equals(PagamentoVO.cargaHorariaColuna())){
        	try {
        		return mapa != null ? nf.format(Double.parseDouble(value.replace(".", "").replace(",", ".").toString())) : "";
			} catch (Exception e) {}
        }
		return mapa != null ? (String) mapa.get(chave) : "";
	}
	
	
	
	public void somarValor(String chave, HashMap<String, Object> mapa) {
		
		try {
			if(!chave.equals(PagamentoVO.diasTrabalhadosColuna())
					&& !chave.equals(PagamentoVO.numeroMatriculaColuna())
	        		&& !chave.equals(PagamentoVO.cargaHorariaColuna())){
				String valor = getObject(chave, mapa);
				valor = valor.replace(".", "").replace(",", ".");
				Double value = Double.parseDouble(valor);
				setTotaisValoresPaginacao(value, chave);
			}
		} catch (Exception e) {}
	}
	
	private void setTotaisValoresPaginacao(Double valor, String chave) {
		if(!somatorioValoresPaginacao.containsKey(chave)){
			somatorioValoresPaginacao.put(chave, 0.00); 
		}
		somatorioValoresPaginacao.put(chave, somatorioValoresPaginacao.get(chave) + valor); 
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
					pagamentosMap.put(eventoPagamento.getEvento().getNome(), String.format("%.2f", eventoPagamento.getValor()));
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
				if(!colunas.contains((String) evento.getNome())){
					colunas.add((String) evento.getNome());
				}
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

	public void exportarExcel() {
		Workbook  workbook ;
		String nomeArquivo = "pagamento.xlsx";
		FileInputStream fis;
		try {
			fis = new FileInputStream("file.xls");
            workbook = WorkbookFactory.create(fis);
            org.apache.poi.ss.usermodel.Sheet firstSheet = workbook.getSheetAt(0);
			int numeroLinha = 0;
			Row  row = firstSheet.createRow(numeroLinha++);
			int numeroCelulaColuna = 0;
			boolean primeiraColuna = true;
			for(String nomeColuna : pagamentosColumnsMap){
				row = firstSheet.getRow(0);
				System.out.println(numeroCelulaColuna +" : "+ nomeColuna);
				Cell cell=row.getCell(numeroCelulaColuna);
				if(cell == null)     cell = row.createCell(numeroCelulaColuna, Cell.CELL_TYPE_STRING);
                cell.setCellValue(nomeColuna);
				for (Map<String, Object> mapa : pagamentosMap) {
					row = primeiraColuna ? firstSheet.createRow(numeroLinha++) : firstSheet.getRow(numeroLinha++);
					cell=row.getCell(numeroCelulaColuna);
					if(cell == null)     cell = row.createCell(numeroCelulaColuna, Cell.CELL_TYPE_STRING);
	                cell.setCellValue(getObject(nomeColuna,(HashMap<String, Object>) mapa));
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
			FacesUtils.addErrorMessage("Erro ao exportar arquivo");
		} 
	} 
	   
	public void onPaginate(PageEvent event){
		somatorioValoresPaginacao = new HashMap<String, Double>();
	}

	public Cidade getCidade() {
		return cidade;
	}

	public void setCidade(Cidade cidade) {
		this.cidade = cidade;
	}

	public List<ArquivoPagamento> getArquivoPagamentos() {
		return arquivoPagamentos;
	}

	public void setArquivoPagamentos(List<ArquivoPagamento> arquivoPagamentos) {
		this.arquivoPagamentos = arquivoPagamentos;
	}

	public void setArquivoPagamentoService(ArquivoPagamentoService arquivoPagamentoService) {
		this.arquivoPagamentoService = arquivoPagamentoService;
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

	public void setCargaHorariaPagamentoService(CargaHorariaPagamentoService cargaHorariaPagamentoService) {
		this.cargaHorariaPagamentoService = cargaHorariaPagamentoService;
	}

	public List<Cargo> getCargosDisponiveis() {
		return cargosDisponiveis;
	}

	public void setCargosDisponiveis(List<Cargo> cargosDisponiveis) {
		this.cargosDisponiveis = cargosDisponiveis;
	}

	public List<Cargo> getCargosSelecionados() {
		return cargosSelecionados;
	}

	public void setCargosSelecionados(List<Cargo> cargosSelecionados) {
		this.cargosSelecionados = cargosSelecionados;
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

	public List<UnidadeTrabalho> getUnidadesSelecionadas() {
		return unidadesSelecionadas;
	}

	public void setUnidadesSelecionadas(List<UnidadeTrabalho> unidadesSelecionadas) {
		this.unidadesSelecionadas = unidadesSelecionadas;
	}

	public List<UnidadeTrabalho> getUnidadesDisponiveis() {
		return unidadesDisponiveis;
	}

	public void setUnidadesDisponiveis(List<UnidadeTrabalho> unidadesDisponiveis) {
		this.unidadesDisponiveis = unidadesDisponiveis;
	}

	public List<NivelPagamento> getNiveisSelecionados() {
		return niveisSelecionados;
	}

	public void setNiveisSelecionados(List<NivelPagamento> niveisSelecionados) {
		this.niveisSelecionados = niveisSelecionados;
	}

	public List<NivelPagamento> getNiveisDisponiveis() {
		return niveisDisponiveis;
	}

	public void setNiveisDisponiveis(List<NivelPagamento> niveisDisponiveis) {
		this.niveisDisponiveis = niveisDisponiveis;
	}

	public List<CargaHorariaPagamento> getCargasDisponiveis() {
		return cargasDisponiveis;
	}

	public void setCargasDisponiveis(List<CargaHorariaPagamento> cargasDisponiveis) {
		this.cargasDisponiveis = cargasDisponiveis;
	}

	public List<CargaHorariaPagamento> getCargasSelecionados() {
		return cargasSelecionados;
	}

	public void setCargasSelecionados(List<CargaHorariaPagamento> cargasSelecionados) {
		this.cargasSelecionados = cargasSelecionados;
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

	public static String getDiasTrabalhadosColuna(){
		return PagamentoVO.getDiasTrabalhadosColuna();
	}
	
	public boolean getNomesColunaSemDouble(String coluna){
		return PagamentoVO.getNomesColunaSemDouble().contains(coluna);
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
	
}
