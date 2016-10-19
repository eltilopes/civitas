package br.com.civitas.processamento.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.com.civitas.arquitetura.ApplicationException;
import br.com.civitas.arquitetura.util.Util;
import br.com.civitas.helpers.utils.StringUtils;
import br.com.civitas.processamento.entity.ArquivoPagamento;
import br.com.civitas.processamento.entity.Cargo;
import br.com.civitas.processamento.entity.Evento;
import br.com.civitas.processamento.entity.EventoPagamento;
import br.com.civitas.processamento.entity.Matricula;
import br.com.civitas.processamento.entity.MatriculaPagamento;
import br.com.civitas.processamento.entity.Pagamento;
import br.com.civitas.processamento.entity.Secretaria;
import br.com.civitas.processamento.entity.Setor;
import br.com.civitas.processamento.entity.Vinculo;
import br.com.civitas.processamento.enums.IdentificadorArquivoDigimax;
import br.com.civitas.processamento.interfac.IProcessarArquivoPagamento;

@Service
public class ProcessarArquivoDigimaxService extends ProcessarArquivoPagamento implements IProcessarArquivoPagamento{

	private  Pagamento pagamento;
	private  Matricula matricula;
	private  Secretaria secretaria;
	private  Setor setor;
	private  List<Pagamento> pagamentos;
	private  boolean processamentoPagamentoAtivo = false;
	private  boolean processamentoEventos = false;
	private  boolean processamentoTotais = false;
	private  boolean quebraPagina = false;
	private  String linhaAnterior = "";
	private  String linhaAnteriorQuebraPagina = "";
	
	public void processar(ArquivoPagamento arquivoPagamento) throws Exception{
		setArquivoPagamento(arquivoPagamento);
		processar();
	}
	
	private void processar() throws Exception {
		iniciarArquivos();
		iniciarValores();
		carregarEventos();
		carregarPagamentos();
		finalizarArquivos();
	}

	private void carregarPagamentos() throws Exception {
		BufferedReader br = new BufferedReader(getFilReaderPagamento());
		while (br.ready()) {
			String linha = br.readLine(); 
			if(linhaTemImformacao(linha)){
				verificarQuebraPagina(linha);
				if(!quebraPagina && !linha.contains(IdentificadorArquivoDigimax.FIM_QUEBRA_PAGINA.getDescricao())){
					localizarPagamentos(linha);
					linhaAnterior = linha;
				}
				linhaAnteriorQuebraPagina = linha;
			}
		}
		pagamento.getMatricula().getMatriculaPagamento().setSecretaria(secretaria);
		pagamento.getMatricula().getMatriculaPagamento().setSetor(setor);
		pagamentos.add(pagamento);
		br.close();
		getMatriculaPagamentoService().saveOrUpdateAll(pagamentos.stream().map(p -> p.getMatricula().getMatriculaPagamento()).distinct().collect(Collectors.toCollection(ArrayList<MatriculaPagamento> :: new)));
		getPagamentoService().inserirPagamentos(pagamentos,getEventos(), getArquivoPagamento());
	}

	private boolean linhaTemImformacao(String linha) {
		return StringUtils.notNullOrEmpty(linha) && linha.length()>5;
	}

	private void carregarEventos() throws IOException{
		BufferedReader brEvento = new BufferedReader(getFileReaderEvento());
		while (brEvento.ready()) {
			String linha = brEvento.readLine(); 
			if(linhaTemImformacao(linha)){
				localizarEvento(linha);
				linhaAnteriorQuebraPagina = linha;
			}
		}
		brEvento.close();
	}
	

	private void iniciarValores( ) {
		pagamento = null;
		matricula = null;
		pagamentos = new ArrayList<Pagamento>();
		secretaria = null;
		setor = null;
		setMatriculas(getMatriculaService().buscarPorCidade(getArquivoPagamento().getCidade()));
		setCargasHorariaPagamento(getCargaHorariaPagamentoService().buscarTipoArquivoCidade(getArquivoPagamento().getCidade(), getArquivoPagamento().getTipoArquivo()));
		setSetores(getSetorService().buscarTipoArquivoCidade(getArquivoPagamento().getCidade(), getArquivoPagamento().getTipoArquivo()));
		setSecretarias(getSecretariaService().buscarTipoArquivoCidade(getArquivoPagamento().getCidade(), getArquivoPagamento().getTipoArquivo()));
		setCargos(getCargoService().buscarTipoArquivoCidade(getArquivoPagamento().getCidade(), getArquivoPagamento().getTipoArquivo()));
		setVinculos(getVinculoService().buscarPorCidade(getArquivoPagamento().getCidade()));
		setEventos(getEventoService().buscarTipoArquivoCidade(getArquivoPagamento().getCidade(), getArquivoPagamento().getTipoArquivo()));
		processamentoPagamentoAtivo = false;
		processamentoEventos = false;
		processamentoTotais = false;
		quebraPagina = false;
		linhaAnterior = "";
		linhaAnteriorQuebraPagina = "";
	}

	private void localizarEvento(String linha) {
		verificarIdentificadorEvento(linha);
		verificarQuebraPagina(linha);
		if(processamentoEventos && !quebraPagina && !(linha.contains(IdentificadorArquivoDigimax.INICIO_EVENTO.getDescricao()))
				&& !(linha.contains(IdentificadorArquivoDigimax.FIM_QUEBRA_PAGINA.getDescricao()))){
			getEvento(linha, getChaveEvento(linha));
		}
	}

	private void verificarQuebraPagina(String linha) {
		if(linha.contains(IdentificadorArquivoDigimax.INICIO_QUEBRA_PAGINA.getDescricao())){
			quebraPagina = true;
		}
		if(linha.contains(IdentificadorArquivoDigimax.FIM_QUEBRA_PAGINA.getDescricao())
				&& linhaAnteriorQuebraPagina.contains(IdentificadorArquivoDigimax.NOME_FUNCIONARIO.getDescricao())){
			quebraPagina = false;
		}
	}

	private String getChaveEvento(String linha) {
		try {
			int posicaoInicialIdentificador = linha.lastIndexOf(IdentificadorArquivoDigimax.PIPE.getDescricao()) + IdentificadorArquivoDigimax.PIPE.getDescricao().length();
			int posicaoSegundoIdentificador = getPosicaoSegundoIdentificador(linha);
			String linhaInvertida = new StringBuffer(linha.substring(0, posicaoSegundoIdentificador)).reverse().toString();
			int posicaoEspacoLinhaInvertida = linhaInvertida.indexOf(IdentificadorArquivoDigimax.ESPACO_NA_LINHA.getDescricao()) ;
			return linha.substring(posicaoInicialIdentificador, posicaoSegundoIdentificador - posicaoEspacoLinhaInvertida).trim();
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar o Chave Evento. Linha: " + linha);
		}
	}
	
	private int getPosicaoSegundoIdentificador(String linha) {
		if(linha.contains(IdentificadorArquivoDigimax.BARRA.getDescricao())
				&& !linha.contains(IdentificadorArquivoDigimax.ADICIONAL.getDescricao())
				&& !linha.contains(IdentificadorArquivoDigimax.ADICIONAL_ABREVIADO.getDescricao())
				&& !linha.contains(IdentificadorArquivoDigimax.FERIAS.getDescricao())){
			return linha.indexOf(IdentificadorArquivoDigimax.BARRA.getDescricao());
		}
		return linha.indexOf(IdentificadorArquivoDigimax.VIRGULA.getDescricao());
	}

	private void verificarIdentificadorEvento(String linha) {
		if(linha.contains(IdentificadorArquivoDigimax.INICIO_EVENTO.getDescricao())){
			processamentoEventos = true;
			processamentoPagamentoAtivo = true;
		}
		if((linha.contains(IdentificadorArquivoDigimax.FIM_EVENTO.getDescricao()))){
			processamentoEventos = false;
			processamentoPagamentoAtivo = false;
		}
	}

	private void localizarPagamentos(String linhaAtual) throws Exception {
		localizarSecretariaSetor(linhaAtual);
		localizarMatricula(linhaAtual);
		verificarIdentificador(linhaAtual, getArquivoPagamento().getNomeArquivo());
	}
	
	private void localizarSecretariaSetor(String linhaAtual) {
		if(linhaAtual.contains(IdentificadorArquivoDigimax.SECRETARIA.getDescricao())){
			secretaria = getSecretaria(getSecretaria(linhaAtual), linhaAtual);
			setSecretariaPagamentos();
		}else if(linhaAtual.contains(IdentificadorArquivoDigimax.DEPARTAMENTO.getDescricao())
					&& !linhaAtual.contains(IdentificadorArquivoDigimax.TOTAL.getDescricao())){
			setor = getSetor(getSetor(linhaAtual), linhaAtual);
			setSetorPagamentos();
		}
	}

	private void setSecretariaPagamentos() {
		for(Pagamento p : pagamentos){
			p.getMatricula().getMatriculaPagamento().setSecretaria(Objects.isNull(p.getMatricula().getMatriculaPagamento().getSecretaria()) ? secretaria : p.getMatricula().getMatriculaPagamento().getSecretaria() );
		}
	}

	private void setSetorPagamentos() {
		for(Pagamento p : pagamentos){
			p.getMatricula().getMatriculaPagamento().setSetor(Objects.isNull(p.getMatricula().getMatriculaPagamento().getSetor()) ? setor : p.getMatricula().getMatriculaPagamento().getSetor() );
		}
	}

	private Secretaria getSecretaria(String linhaAtual) throws ApplicationException {
		Secretaria secretaria = new Secretaria();
		try {
			String descricao = linhaAtual.substring(linhaAtual.indexOf(IdentificadorArquivoDigimax.HIFEN.getDescricao()) + 1,
					linhaAtual.length()).trim();
			secretaria.setCidade(getArquivoPagamento().getCidade());
			secretaria.setTipoArquivo(getArquivoPagamento().getTipoArquivo());
			secretaria.setDescricao(descricao);
			
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar a Secretaria. Linha: " + linhaAtual);
		}
		return secretaria;
	}
	
	private Setor getSetor(String linhaAtual) throws ApplicationException {
		Setor setor = new Setor();
		try {
			String descricao = linhaAtual.substring(linhaAtual.indexOf(IdentificadorArquivoDigimax.HIFEN.getDescricao()) + 1,
					linhaAtual.length()).trim();
			setor.setCidade(getArquivoPagamento().getCidade());
			setor.setTipoArquivo(getArquivoPagamento().getTipoArquivo());
			setor.setDescricao(descricao);
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar o Setor. Linha: " + linhaAtual);
		}
		return setor;
	}

	private  void verificarIdentificador(String linha, String nomeArquivo) {
		verificarIdentificadorEvento(linha);
		verificarTotais(linha);
		for(Evento e : getEventos()){
			if(processamentoPagamentoAtivo && !(linha.contains(IdentificadorArquivoDigimax.INICIO_EVENTO.getDescricao()))
					 && getChaveEvento(linha).equals(e.getChave())){
				pagamento.getEventosPagamento().add(getEventoPagamento(linha, e));
			}
		}
		setTotaisPagamento(linha);
	}
	
	private void verificarTotais(String linha) {
		if(linha.contains(IdentificadorArquivoDigimax.TOTAIS_PAGAMENTO.getDescricao())){
			processamentoTotais = true;
		}
		if(linha.contains(IdentificadorArquivoDigimax.FIM_TOTAIS_PAGAMENTO.getDescricao())){
			processamentoTotais = false;
		}
	}

	private void setTotaisPagamento(String linha) {
		
		if(processamentoTotais && !quebraPagina && !linha.contains(IdentificadorArquivoDigimax.TOTAIS_PAGAMENTO.getDescricao())){
			String[] valores = retirarEspacosBranco(linha).split(" ");
			try {
				pagamento.setTotalLiquido(Double.parseDouble(valores[5].replace(".","").replace(",", ".")));
				pagamento.setTotalProventos(Double.parseDouble(valores[3].replace(".","").replace(",", ".")));
				pagamento.setTotalDescontos(Double.parseDouble(valores[4].replace(".","").replace(",", ".")));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	private String retirarEspacosBranco(String linha) {
		while(linha.contains("  ")){
			linha = linha.replace("  ", " ");
		}
		return linha;
	}

	private EventoPagamento getEventoPagamento(String linha, Evento evento) {
		EventoPagamento eventoPagamento = new EventoPagamento();
		try {
			eventoPagamento.setPagamento(pagamento);
			eventoPagamento.setEvento(evento);
			eventoPagamento.setValor(Double.parseDouble(getValorEvento(linha, linha.lastIndexOf(IdentificadorArquivoDigimax.VIRGULA.getDescricao()))));
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar o Evento Pagamento. Linha: " + linha);
		}
		return eventoPagamento;
	}

	private String getValorEvento(String linha, int posicaoVirgula) {
		try {
			String linhaInvertida = new StringBuffer(linha.substring(0, posicaoVirgula)).reverse().toString();
			int posicaoEspacoLinhaInvertida = linhaInvertida.indexOf(IdentificadorArquivoDigimax.ESPACO_NA_LINHA.getDescricao()) ;
			String valor = linha.substring(posicaoVirgula - posicaoEspacoLinhaInvertida, posicaoVirgula + 3);
			return valor.replace(".","").replace(",", ".");
		} catch (Exception e) {	
			throw new ApplicationException("Erro ao pegar o Valor Evento. Linha: " + linha);
		}
	}
		
	private  void localizarMatricula(String linhaAtual) throws Exception {
		if(linhaAtual.contains(IdentificadorArquivoDigimax.CPF.getDescricao())){
			String numeroMatricula = getNumeroMatricula();
			if(Objects.nonNull(pagamento) && Objects.nonNull(matricula) && !matricula.getNumeroMatricula().equals(numeroMatricula)){
				pagamentos.add(pagamento);
			}
			pagamento = new Pagamento();
			getMatricula(numeroMatricula, linhaAtual);
		}
	}
	
	private Date getDataAdmissao(String linha) {
		try {
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			String data = linha.substring(
					linha.indexOf(IdentificadorArquivoDigimax.DATA_ADMISSAO.getDescricao()) 
					+ IdentificadorArquivoDigimax.DATA_ADMISSAO.getDescricao().length()
					,linha.indexOf(IdentificadorArquivoDigimax.VINCULO.getDescricao())).trim();
			return (Date)formatter.parse(data);
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar a Data de Admissão. Linha: " + linha);
		}
	}

	private String getNumeroMatricula() throws Exception {
		String numeroMatricula = linhaAnterior.substring(0,4).trim();
		Integer numero = 0;
		try {
			numero = Integer.parseInt(numeroMatricula.replace("-", ""));
		} catch (Exception e) {
			try {
				numero =  numeroMatricula.hashCode();
				numeroMatricula = numero.toString().replace("-", "").substring(0, 8);
			} catch (Exception e2) {
				numeroMatricula = numero.toString().replace("-", "");
			}
		}
		return numeroMatricula;
	}

	private void getMatricula(String numeroMatricula, String linhaAtual) {
		matricula = getMatricula(new Matricula(numeroMatricula));
		if(Objects.isNull(matricula)){
			novaMatricula(numeroMatricula, linhaAtual);
			matricula = getMatricula(matricula, linhaAtual);
		}
		setMatriculaPagamento(linhaAtual);
		pagamento.setMatricula(matricula);
	}
	
	private  void novaMatricula(String numeroMatricula, String linha) throws ApplicationException {
		matricula = new Matricula();
		matricula.setNumeroMatricula(numeroMatricula);
		matricula.setNomeFuncionario(getNomeFuncionario(linhaAnterior));;
		matricula.setDataAdmissao(getDataAdmissao(linha));
	}

	private void setMatriculaPagamento(String linhaAtual) {
		matricula.setMatriculaPagamento(new MatriculaPagamento());
		matricula.getMatriculaPagamento().setCargo(getCargo(getCargo(linhaAnterior), linhaAnterior));
		matricula.getMatriculaPagamento().setVinculo(getVinculo(getVinculo(linhaAtual), linhaAtual));
		matricula.getMatriculaPagamento().setAno(getArquivoPagamento().getAno());
		matricula.getMatriculaPagamento().setMes(getArquivoPagamento().getMes());
		matricula.getMatriculaPagamento().setMatricula(matricula);
	}
	
	private Cargo getCargo(String linha) throws ApplicationException {
		Cargo cargo = new Cargo();
		try {
			String palavraComNumeroCargo = linha.replace(matricula.getNomeFuncionario(), "")
					.replace(matricula.getNumeroMatricula(), "").trim();
			String valorNumeroCargo = palavraComNumeroCargo.substring(0,Util.posicaoPrimeiraLetra(palavraComNumeroCargo)).trim();
			cargo.setCidade(getArquivoPagamento().getCidade());
			cargo.setTipoArquivo(getArquivoPagamento().getTipoArquivo());
			cargo.setNumero(Integer.parseInt(valorNumeroCargo.trim())); 
			cargo.setDescricao(palavraComNumeroCargo.replace(valorNumeroCargo, "").trim());
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar o Cargo. Linha: " + linha);
		}
		return cargo;
	}

	private String getNomeFuncionario(String linha) throws ApplicationException {
		String nome= "";
		try {
			Integer posicaoInicial = getPosicaoInicialNomeFuncionario(linha);
			nome = linha.substring(posicaoInicial, linha.length()).trim();
			nome = nome.substring(0, Util.posicaoPrimeiraNumero(nome)).trim();
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar o Nome do Funcionário. Linha: " + linha);
		}
		return nome ;
		
	}

	private Integer getPosicaoInicialNomeFuncionario(String linha) {
		return linha.indexOf(matricula.getNumeroMatricula()) == -1 ? 0 : linha.indexOf(matricula.getNumeroMatricula()) + matricula.getNumeroMatricula().length();
	}

	private Vinculo getVinculo(String linha) throws ApplicationException {
		Vinculo vinculo = new Vinculo();
		try {
			String descricao = linha.substring(linha.indexOf(IdentificadorArquivoDigimax.VINCULO.getDescricao())
					+ IdentificadorArquivoDigimax.VINCULO.getDescricao().length(), getFinalDescricaoVinculo(linha)).trim();
			Integer numero = descricao.hashCode();
			vinculo.setNumero(Integer.parseInt(numero.toString().substring(0, 2))); 
			vinculo.setCidade(getArquivoPagamento().getCidade());
			vinculo.setDescricao(descricao.toUpperCase());
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar o Vínculo. Linha: " + linha);
		}
		return vinculo;
	}
	
	private int getFinalDescricaoVinculo(String linha) {
		return linha.contains(IdentificadorArquivoDigimax.DATA_AFASTAMENTO.getDescricao()) ? 
			linha.indexOf(IdentificadorArquivoDigimax.DATA_AFASTAMENTO.getDescricao())
			: linha.length();
	}

}


