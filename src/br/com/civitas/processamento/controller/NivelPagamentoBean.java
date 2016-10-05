package br.com.civitas.processamento.controller;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.UploadedFile;

import br.com.civitas.arquitetura.controller.AbstractCrudBean;
import br.com.civitas.arquitetura.seguranca.filter.Acesso;
import br.com.civitas.arquitetura.util.PropertiesUtils;
import br.com.civitas.processamento.entity.Ano;
import br.com.civitas.processamento.entity.Cidade;
import br.com.civitas.processamento.entity.NivelPagamento;
import br.com.civitas.processamento.entity.Secretaria;
import br.com.civitas.processamento.enums.TipoArquivo;
import br.com.civitas.processamento.factory.FactoryEnuns;
import br.com.civitas.processamento.service.AnoService;
import br.com.civitas.processamento.service.CidadeService;
import br.com.civitas.processamento.service.NivelPagamentoService;
import br.com.civitas.processamento.service.SecretariaService;

@ManagedBean
@ViewScoped
public class NivelPagamentoBean extends AbstractCrudBean<NivelPagamento, NivelPagamentoService>  implements Serializable{

	private static final long serialVersionUID = 6283001396634682530L;
	private static final String PATH_DOWNLOAD = "PATH_DOWNLOAD_ARQUIVO";
	private static final String NOME_ARQUIVO_DOWNLOAD = "arquivo.csv";
	
	@ManagedProperty("#{cidadeService}")
	private CidadeService cidadeService;
	
	@ManagedProperty("#{anoService}")
	private AnoService anoService;
	
	@ManagedProperty("#{nivelPagamentoService}")
	private NivelPagamentoService service;
	
	@ManagedProperty("#{secretariaService}")
	private SecretariaService secretariaService;
	
	private List<Cidade> cidades;
	private List<Secretaria> secretarias;
	private List<Ano> anos;
	private List<TipoArquivo> tiposArquivos;
	
	private Ano ano;
	private Cidade cidade;
	private Secretaria secretaria;
	private TipoArquivo tipoArquivo;
	private UploadedFile file;
	
	private boolean importarArquivo = false;

	@PostConstruct
	public void init(){
		setCidades(cidadeService.buscarTodasAtivas());
		setAnos(anoService.buscarTodos());
		setTiposArquivos(FactoryEnuns.listaTipoArquivo());
	}

	public void prepararImportacaoArquivo(){
		setImportarArquivo(true);
		setCurrentState(STATE_INSERT);
	}
	
	public void carregarPorCidade() {
		if (Objects.nonNull(cidade)) {
			setSecretarias(secretariaService.buscarCidade(cidade));
		}
	}
	
	public ArrayList<String>  getInformacoesImportacao() {
		ArrayList<String> infos = new ArrayList<String>();
		infos.add("A importação deverá seguir as regras informadas.");
		infos.add("O arquivo deverá conter somente informações válidas.");
		infos.add("1 - A primeira linha não deverá ser alterada.");
		infos.add("2 - Campo 'CÓDIGO' deverá contemr somente números.");
		infos.add("3 - Campo 'DESCRIÇÃO' deverá contemr somente letras.");
		infos.add("4 - Campo 'SALÁRIO BASE' deverá contemr somente números.");
		infos.add("Seguir como exemplo o arquivo disponibilizado para download.");
		return infos;
	}

	public String downloadArquivoExemplo() {
		Path path = Paths.get(PropertiesUtils.getValueProperty(Acesso.filePermissao, PATH_DOWNLOAD));
		return path + File.separator + NOME_ARQUIVO_DOWNLOAD ;
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

	public List<Ano> getAnos() {
		return anos;
	}

	public void setAnos(List<Ano> anos) {
		this.anos = anos;
	}

	public void setAnoService(AnoService anoService) {
		this.anoService = anoService;
	}

	public void setService(NivelPagamentoService service) {
		super.setService(service);
		this.service = service;
	}

	public boolean isImportarArquivo() {
		return importarArquivo;
	}

	public void setImportarArquivo(boolean importarArquivo) {
		this.importarArquivo = importarArquivo;
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

	public Cidade getCidade() {
		return cidade;
	}

	public void setCidade(Cidade cidade) {
		this.cidade = cidade;
	}

	public Ano getAno() {
		return ano;
	}

	public void setAno(Ano ano) {
		this.ano = ano;
	}

	public TipoArquivo getTipoArquivo() {
		return tipoArquivo;
	}

	public void setTipoArquivo(TipoArquivo tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}

	public List<Secretaria> getSecretarias() {
		return secretarias;
	}

	public void setSecretarias(List<Secretaria> secretarias) {
		this.secretarias = secretarias;
	}

	public void setSecretariaService(SecretariaService secretariaService) {
		this.secretariaService = secretariaService;
	}

	public Secretaria getSecretaria() {
		return secretaria;
	}

	public void setSecretaria(Secretaria secretaria) {
		this.secretaria = secretaria;
	}

}
