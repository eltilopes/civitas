//package br.com.civitas.processamento.controller;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Objects;
//
//import javax.annotation.PostConstruct;
//import javax.faces.bean.ManagedBean;
//import javax.faces.bean.ManagedProperty;
//import javax.faces.bean.ViewScoped;
//
//import org.primefaces.model.chart.Axis;
//import org.primefaces.model.chart.AxisType;
//import org.primefaces.model.chart.BarChartModel;
//import org.primefaces.model.chart.ChartSeries;
//
//import br.com.civitas.arquitetura.ApplicationException;
//import br.com.civitas.arquitetura.controller.AbstractCrudBean;
//import br.com.civitas.arquitetura.util.FacesUtils;
//import br.com.civitas.processamento.entity.ArquivoPagamento;
//import br.com.civitas.processamento.entity.Cidade;
//import br.com.civitas.processamento.entity.ResumoSetor;
//import br.com.civitas.processamento.service.ArquivoPagamentoService;
//import br.com.civitas.processamento.service.CidadeService;
//import br.com.civitas.processamento.service.ResumoSetorService;
//
//@ManagedBean
//@ViewScoped
//public class GraficoBean extends AbstractCrudBean<ResumoSetor, ResumoSetorService> implements Serializable {
//
//	private static final long serialVersionUID = -3569330030452541124L;
//
//	@ManagedProperty("#{cidadeService}")
//	private CidadeService cidadeService;
//
//	@ManagedProperty("#{resumoSetorService}")
//	private ResumoSetorService service;
//
//	@ManagedProperty("#{arquivoPagamentoService}")
//	private ArquivoPagamentoService arquivoPagamentoService;
//
//	private Cidade cidade;
//	private List<Cidade> cidades;
//	private List<ArquivoPagamento> arquivoPagamentos;
//	private BarChartModel setoresBarra;
//	private List<ResumoSetor> resumosSetores;
//	private ChartSeries totalProventos;
//	private ChartSeries totaLiquido;
//	private ChartSeries totalRemuneracao;
//	private ChartSeries totalDescontos;
//	private BarChartModel model;
//	private Double valorMaximo = 0D;
//
//	@PostConstruct
//	public void init() {
//		cidades = cidadeService.buscarTodasAtivas();
//		resumosSetores = new ArrayList<ResumoSetor>();
//	}
//
//	public void carregarPorCidade() {
//		if (Objects.nonNull(getCidade())) {
//			arquivoPagamentos = arquivoPagamentoService.buscarPorCidade(getCidade());
//		}
//	}
//
//	public void visualizarGrafico() {
//		if (Objects.nonNull(getEntitySearch().getArquivoPagamento()) && Objects.nonNull(getCidade())) {
//			try {
//				resumosSetores = null;
//				resumosSetores = service.buscarPorArquivoPagamento(getEntitySearch().getArquivoPagamento());
//				if (resumosSetores.isEmpty()) {
//					throw new ApplicationException("Consulta sem resultados.");
//				} else {
//					createBarModel();
//				}
//				setCurrentState(STATE_SEARCH);
//			} catch (ApplicationException e) {
//				e.printStackTrace();
//				FacesUtils.addWarnMessage(e.getMessage());
//			} catch (Exception e) {
//				e.printStackTrace();
//				FacesUtils.addErrorMessage(getMessage("ERROR_MESSAGE"));
//			}
//		} else {
//			FacesUtils.addWarnMessage("Selecione a Cidade, Ano e Mês para Consulta.");
//		}
//	}
//
//	private void createBarModel() {
//		setoresBarra = initBarModel();
//
//		setoresBarra.setTitle(getEntitySearch().getArquivoPagamento().getAno().getAno() + " "
//				+ getEntitySearch().getArquivoPagamento().getMes().getDescricao());
//		setoresBarra.setLegendPosition("ne");
//		Axis xAxis = setoresBarra.getAxis(AxisType.X);
//		xAxis.setLabel("Setores");
//
//		Axis yAxis = setoresBarra.getAxis(AxisType.Y);
//		yAxis.setLabel("Valor R$");
//		valorMaximo = resumosSetores.stream().mapToDouble(r -> r.getTotalProventos()).max().getAsDouble();
//		yAxis.setMin(0);
//		yAxis.setMax(valorMaximo + 100);
//	}
//
//	@SuppressWarnings("unchecked")
//	private BarChartModel initBarModel() {
//		Collections.sort(resumosSetores, (r1, r2) -> r2.getTotalProventos().compareTo(r1.getTotalProventos()));
//		model = new BarChartModel();
//		totalProventos = new ChartSeries();
//		totalProventos.setLabel("Total Proventos");
//		totaLiquido = new ChartSeries();
//		totaLiquido.setLabel("Total Líquido");
//		totalRemuneracao = new ChartSeries();
//		totalRemuneracao.setLabel("Total Remuneração");
//		totalDescontos = new ChartSeries();
//		totalDescontos.setLabel("Total Descontos");
//		resumosSetores = resumosSetores.subList(0, 5);
//		resumosSetores.forEach(r -> {
//			totalProventos.set(r.getSetor().getDescricao(), r.getTotalProventos());
//			totaLiquido.set(r.getSetor().getDescricao(), r.getTotalLiquido());
//			totalRemuneracao.set(r.getSetor().getDescricao(), r.getTotalRemuneracao());
//			totalDescontos.set(r.getSetor().getDescricao(), r.getTotalDescontos());
//		});
//		model.addSeries(totalProventos);
//		model.addSeries(totaLiquido);
//		model.addSeries(totalRemuneracao);
//		model.addSeries(totalDescontos);
//		model.setExtender("customExtender");
//		return model;
//	}
//
//	public CidadeService getCidadeService() {
//		return cidadeService;
//	}
//
//	public void setCidadeService(CidadeService cidadeService) {
//		this.cidadeService = cidadeService;
//	}
//
//	public List<Cidade> getCidades() {
//		return cidades;
//	}
//
//	public void setCidades(List<Cidade> cidades) {
//		this.cidades = cidades;
//	}
//
//	public void setService(ResumoSetorService service) {
//		super.setService(service);
//		this.service = service;
//	}
//
//	public void setArquivoPagamentoService(ArquivoPagamentoService arquivoPagamentoService) {
//		this.arquivoPagamentoService = arquivoPagamentoService;
//	}
//
//	public List<ArquivoPagamento> getArquivoPagamentos() {
//		return arquivoPagamentos;
//	}
//
//	public void setArquivoPagamentos(List<ArquivoPagamento> arquivoPagamentos) {
//		this.arquivoPagamentos = arquivoPagamentos;
//	}
//
//	public Cidade getCidade() {
//		return cidade;
//	}
//
//	public void setCidade(Cidade cidade) {
//		this.cidade = cidade;
//	}
//
//	public BarChartModel getSetoresBarra() {
//		return setoresBarra;
//	}
//
//	public void setSetoresBarra(BarChartModel setoresBarra) {
//		this.setoresBarra = setoresBarra;
//	}
//
//	public List<ResumoSetor> getResumosSetores() {
//		return resumosSetores;
//	}
//
//	public void setResumosSetores(List<ResumoSetor> resumosSetores) {
//		this.resumosSetores = resumosSetores;
//	}
//
//}
