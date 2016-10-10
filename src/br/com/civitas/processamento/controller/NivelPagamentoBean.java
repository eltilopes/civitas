package br.com.civitas.processamento.controller;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.UploadedFile;

import br.com.civitas.arquitetura.ApplicationException;
import br.com.civitas.arquitetura.controller.AbstractCrudBean;
import br.com.civitas.arquitetura.seguranca.filter.Acesso;
import br.com.civitas.arquitetura.util.FacesUtils;
import br.com.civitas.arquitetura.util.PropertiesUtils;
import br.com.civitas.processamento.entity.Ano;
import br.com.civitas.processamento.entity.Cidade;
import br.com.civitas.processamento.entity.LogErroProcessador;
import br.com.civitas.processamento.entity.NivelPagamento;
import br.com.civitas.processamento.entity.Secretaria;
import br.com.civitas.processamento.enums.TipoArquivo;
import br.com.civitas.processamento.factory.FactoryEnuns;
import br.com.civitas.processamento.service.AnoService;
import br.com.civitas.processamento.service.CidadeService;
import br.com.civitas.processamento.service.ImportacaoNivelPagamentoService;
import br.com.civitas.processamento.service.LogErroProcessadorService;
import br.com.civitas.processamento.service.NivelPagamentoService;
import br.com.civitas.processamento.service.SecretariaService;

@ManagedBean
@ViewScoped
public class NivelPagamentoBean extends AbstractCrudBean<NivelPagamento, NivelPagamentoService>  implements Serializable{

	private static final long serialVersionUID = 6283001396634682530L;
	private static final String PATH_DOWNLOAD = "PATH_DOWNLOAD_ARQUIVO";
	private static final String NOME_ARQUIVO_DOWNLOAD = "arquivo.csv";
	private static final String MANUAL_IMPORTACAO = "manual.pdf";
	private static final String TIPO_ARQUIVO_CSV = "CSV";

	@ManagedProperty("#{cidadeService}")
	private CidadeService cidadeService;
	
	@ManagedProperty("#{anoService}")
	private AnoService anoService;
	
	@ManagedProperty("#{nivelPagamentoService}")
	private NivelPagamentoService service;
	
	@ManagedProperty("#{secretariaService}")
	private SecretariaService secretariaService;
	
	@ManagedProperty("#{logErroProcessadorService}")
	private LogErroProcessadorService logErroProcessadorService;
	
	@ManagedProperty("#{importacaoNivelPagamentoService}")
	private ImportacaoNivelPagamentoService importacaoNivelPagamentoService;
	
	private List<Cidade> cidades;
	private List<Secretaria> secretarias;
	private List<Ano> anos;
	private List<TipoArquivo> tiposArquivos;
	private Map<String, Object> niveisImportados;
	
	private Ano ano;
	private Cidade cidade;
	private Secretaria secretaria;
	private TipoArquivo tipoArquivo;
	private UploadedFile file;
	private String nomeArquivo;
	
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
		infos.add("1 - Selecionar a Cidade desejada para importar os níveis de pagamento.");
		infos.add("2 - Selecionar a Secretaria desejada para importar os níveis de pagamento.");
		infos.add("3 - Selecionar o Ano em que os níveis de pagamento terão vigência.");
		infos.add("4 - Selecionar o Tipo de Arquivo desejado para importação.");
		infos.add("5 - Selecionar o Arquivo que contêm as informações válidas.");
		infos.add("Caso um dos Níveis de Pagamento esteja com erro, os demais não serão processados.");
		return infos;
	}

	public String downloadArquivoExemplo() {
		Path path = Paths.get(PropertiesUtils.getValueProperty(Acesso.filePermissao, PATH_DOWNLOAD));
		return path + File.separator + NOME_ARQUIVO_DOWNLOAD ;
	}
	
	@SuppressWarnings("unchecked")
	public void importarNiveis(){
		if(validarEntidadesExtenssaoArquivo()){
			try {
				niveisImportados = importacaoNivelPagamentoService.importarArquivo(nomeArquivo, file,ano, cidade, secretaria, tipoArquivo);
				FacesUtils.addInfoMessage("Arquivo Importado com Sucesso! "
						+ "O Arquivo continha "+niveisImportados.get("quantidadeLinhasComNiveis")+" Nível(s) a serem importados. "
						+ "Foram importados "+ ((List<NivelPagamento>)niveisImportados.get("niveisPagamento")).size() +" Nível(s). "
						+  getInformacaoNiveisExistentes((int) niveisImportados.get("quantidadeLinhasComNiveis")));
			} catch (ApplicationException e) {
				logErroProcessadorService.save(new LogErroProcessador(nomeArquivo, e.getMessage()));
				FacesUtils.addErrorMessage(e.getMessage());
			}catch (Exception e) {
				logErroProcessadorService.save(new LogErroProcessador(nomeArquivo, e.getCause().toString()));
				FacesUtils.addErrorMessage("Erro no processamento. Contate o administrador");
			}finally {
				iniciarValores();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private String getInformacaoNiveisExistentes(int quantidadeLinhasComNiveis) {
		return quantidadeLinhasComNiveis==niveisImportados.size() ? "" : "Já existiam " 
				+ (quantidadeLinhasComNiveis - ((List<NivelPagamento>)niveisImportados.get("niveisPagamento")).size()) + " Nível(s) cadastrados.";
	}

	private boolean validarEntidadesExtenssaoArquivo()  {
		try {
			nomeArquivo = new String(file.getFileName().getBytes(Charset.defaultCharset()), "UTF-8");
			validarCidadeSecretariaAnoTipoArquivo();
			validarExtenssaoArquivo();
		} catch (UnsupportedEncodingException e) {
			FacesUtils.addWarnMessage("O nome do arquivo esta formato inválido.");
			return false;
		} catch (Exception e) {
			FacesUtils.addWarnMessage(e.getMessage());
			return false;
		}
		return true;
	}

	private void validarExtenssaoArquivo() {
		String extensao = nomeArquivo.substring(nomeArquivo.length() - 3, nomeArquivo.length());
		if (!extensao.toUpperCase().equals(TIPO_ARQUIVO_CSV)) {
			throw new ApplicationException("Tipo Arquivo Não Suportado. Somente CSV!");
		}
	}
	public void validarCidadeSecretariaAnoTipoArquivo() {
		if(Objects.isNull(ano)){
			throw new ApplicationException("Selecione um Ano para a importação!");
		}
		if(Objects.isNull(cidade)){
			throw new ApplicationException("Selecione uma Cidade para a importação!");
		}
		if(Objects.isNull(secretaria)){
			throw new ApplicationException("Selecione uma Secretaria para a importação!");
		}
		if(!secretaria.getCidade().equals(cidade)){
			throw new ApplicationException("A Secretaria selecionada não pertence a Cidade informada!");
		}
		if(Objects.isNull(tipoArquivo)){
			throw new ApplicationException("Selecione um Tipo de Arquivo para a importação!");
		}
	}

	private void iniciarValores() {
		file = null;
		ano = null;
		cidade = null;
		secretaria = null;
		tipoArquivo = null;
	}

	public String downloadManualImportacao() {
		Path path = Paths.get(PropertiesUtils.getValueProperty(Acesso.filePermissao, PATH_DOWNLOAD));
		return path + File.separator + MANUAL_IMPORTACAO ;
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

	public void setImportacaoNivelPagamentoService(ImportacaoNivelPagamentoService importacaoNivelPagamentoService) {
		this.importacaoNivelPagamentoService = importacaoNivelPagamentoService;
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

	public void setLogErroProcessadorService(LogErroProcessadorService logErroProcessadorService) {
		this.logErroProcessadorService = logErroProcessadorService;
	}

}
