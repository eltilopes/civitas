package br.com.civitas.processamento.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import br.com.civitas.arquitetura.ApplicationException;
import br.com.civitas.arquitetura.util.Util;
import br.com.civitas.helpers.utils.StringUtils;
import br.com.civitas.processamento.entity.ArquivoPagamento;
import br.com.civitas.processamento.entity.CargaHorariaPagamento;
import br.com.civitas.processamento.entity.Cargo;
import br.com.civitas.processamento.entity.Evento;
import br.com.civitas.processamento.entity.EventoPagamento;
import br.com.civitas.processamento.entity.Matricula;
import br.com.civitas.processamento.entity.MatriculaPagamento;
import br.com.civitas.processamento.entity.Pagamento;
import br.com.civitas.processamento.entity.ResumoSetor;
import br.com.civitas.processamento.entity.Secretaria;
import br.com.civitas.processamento.entity.Setor;
import br.com.civitas.processamento.entity.UnidadeTrabalho;
import br.com.civitas.processamento.entity.Vinculo;
import br.com.civitas.processamento.enums.IdentificadorArquivoInformatica;
import br.com.civitas.processamento.interfac.IProcessarArquivoPagamento;

@Service
public class ProcessarArquivoInformaticaService extends ProcessarArquivoPagamento implements IProcessarArquivoPagamento{

	private  Pagamento pagamento;
	private  Matricula matricula;
	private  Secretaria secretaria;
	private  CargaHorariaPagamento cargaHorariaPagamento;
	private  Setor setor;
	private  List<Pagamento> pagamentos;
	private  boolean processamentoPagamentoAtivo = false;
	private  boolean processamentoEventos = false;
	private  boolean resumoSetor = false;
	private  String linhaAnterior = "";
	
	public List<ResumoSetor> processar(ArquivoPagamento arquivoPagamento) throws Exception{
		//TODO: ajustar List<ResumoSetor>
		setArquivoPagamento(arquivoPagamento);
		processar();
		return null;
	}
	
	private void processar() throws Exception {
		iniciarArquivos();
		iniciarValores();
		carregarEventos();
		carregarPagamentos();
		finalizarArquivos();
	}

	private void carregarPagamentos() throws Exception {
		processamentoEventos = false;
		BufferedReader br = new BufferedReader(getFilReaderPagamento());
		while (br.ready()) {
			String linha = br.readLine(); 
			localizarPagamentos(linha);
			linhaAnterior = linha;
		}
		pagamentos.add(pagamento);
		br.close();
		getPagamentoService().inserirPagamentos(pagamentos,getEventos(), getArquivoPagamento());
	}

	private void carregarEventos() throws IOException{
		BufferedReader brEvento = new BufferedReader(getFileReaderEvento());
		while (brEvento.ready()) {
			String linha = brEvento.readLine(); 
			localizarEvento(linha);
		}
		brEvento.close();
	}
	

	private void iniciarValores( ) {
		pagamento = null;
		matricula = null;
		secretaria = null;
		setor = null;
		cargaHorariaPagamento = null;
		pagamentos = new ArrayList<Pagamento>();
		setMatriculas(getMatriculaService().buscarPorCidade(getArquivoPagamento().getCidade()));
		setUnidadesTrabalho(getUnidadeTrabalhoService().buscarTipoArquivoCidade(getArquivoPagamento().getCidade(), getArquivoPagamento().getTipoArquivo()));
		setCargasHorariaPagamento(getCargaHorariaPagamentoService().buscarTipoArquivoCidade(getArquivoPagamento().getCidade(), getArquivoPagamento().getTipoArquivo()));
		setSetores(getSetorService().buscarTipoArquivoCidade(getArquivoPagamento().getCidade(), getArquivoPagamento().getTipoArquivo()));
		setSecretarias(getSecretariaService().buscarTipoArquivoCidade(getArquivoPagamento().getCidade(), getArquivoPagamento().getTipoArquivo()));
		setCargos(getCargoService().buscarTipoArquivoCidade(getArquivoPagamento().getCidade(), getArquivoPagamento().getTipoArquivo()));
		setVinculos(getVinculoService().buscarPorCidade(getArquivoPagamento().getCidade()));
		setEventos(getEventoService().buscarTipoArquivoCidade(getArquivoPagamento().getCidade(), getArquivoPagamento().getTipoArquivo()));
		processamentoPagamentoAtivo = false;
		processamentoEventos = false;
		resumoSetor = false;
		linhaAnterior = "";
	}

	private void localizarEvento(String linha) {
		verificarIdentificadorEvento(linha);
		verificarIdentificadorResumoSetor(linha);
		if(processamentoEventos && !resumoSetor && !linha.contains(IdentificadorArquivoInformatica.SEPARADOR_ARQUIVO.getDescricao())){
			List<Evento> eventosNaLinha = getEventoNaLinha(linha);
			for(Evento e : eventosNaLinha){
				getEvento(e, linha);
			}
		}
	}

	private List<Evento> getEventoNaLinha(String linha) {
		List<Evento> eventosNaLinha = new ArrayList<Evento>();
		try {
			int posicaoSegundoEvento = verificarPosicaoSegundoEvento(linha);
			List<String> eventosLinha = carregarEventosLinha(linha, posicaoSegundoEvento);
			for(String linhaEvento : eventosLinha){
				Evento evento = new Evento();
				evento.setChave(linhaEvento.substring(0,4));
				int posicaoInicialIdentificador = 5 ;
				int posicaoPrimeiraVirgula = linha.indexOf(IdentificadorArquivoInformatica.VIRGULA.getDescricao()) ;
				String linhaInvertida = new StringBuffer(linha.substring(0, posicaoPrimeiraVirgula)).reverse().toString();
				int posicaoEspacoLinhaInvertida = linhaInvertida.indexOf(IdentificadorArquivoInformatica.ESPACO_NA_LINHA.getDescricao()) ;
				evento.setNome(getNomeEvento(linha.substring(posicaoInicialIdentificador, posicaoPrimeiraVirgula - posicaoEspacoLinhaInvertida)));
				evento.setCidade(getArquivoPagamento().getCidade());;
				evento.setTipoArquivo(getArquivoPagamento().getTipoArquivo());
				eventosNaLinha.add(evento);
			}
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar o Chave Evento. Linha: " + linha);
		}
		return eventosNaLinha;
	}

	private String getNomeEvento(String nome) {
		String nomeVerificado = "";
		String[] palavras = nome.split(" ");
		for(String palavra : palavras){
			if(!existeCaractereInvalido(palavra) || !Util.palavraSomenteNumeros(palavra)){
				nomeVerificado = nomeVerificado + palavra + " ";
			}
		}
		return nomeVerificado.toUpperCase().trim();
	}

	private boolean existeCaractereInvalido(String palavra) {
		for (char letra : palavra.toCharArray())  {
			if( letra == IdentificadorArquivoInformatica.BARRA.getDescricao().charAt(0) ||
				letra == IdentificadorArquivoInformatica.PERCENTUAL.getDescricao().charAt(0) ||	
				letra == IdentificadorArquivoInformatica.VIRGULA.getDescricao().charAt(0) 	){
				return true;
			}
		}
		return false;
	}

	private List<String> carregarEventosLinha(String linha, int posicaoSegundoEvento) {
		List<String> eventosLinha = new ArrayList<String>();
		if(posicaoSegundoEvento  == 0){
			eventosLinha.add(linha);
		}else{
			eventosLinha.add(linha.substring(0,posicaoSegundoEvento));
			eventosLinha.add(linha.substring(posicaoSegundoEvento, linha.length()));
		}
		return eventosLinha;
	}
	
	private int verificarPosicaoSegundoEvento(String linha) {
		String[] palavras = linha.split(" ");
		boolean primeiraPalavra = true;
		for(String palavra : palavras){
			if(palavra.length()==4 && Util.palavraSomenteNumeros(palavra) && !primeiraPalavra){
				return linha.substring(4,linha.length()).indexOf(palavra) + 4;
			}
			primeiraPalavra = false;
		}
		return 0;
	}

	private void verificarIdentificadorEvento(String linha) {
		if(linha.contains(IdentificadorArquivoInformatica.INICIO_EVENTO.getDescricao())){
			processamentoEventos = true;
		}
		if(linha.contains(IdentificadorArquivoInformatica.FIM_EVENTO.getDescricao()) 
				|| linha.contains(IdentificadorArquivoInformatica.FIM_RESUMO_GERAL.getDescricao()) 
				|| linha.contains(IdentificadorArquivoInformatica.FIM_RESUMO_SETOR.getDescricao()) ){
			processamentoEventos = false;
		}
	}

	private void verificarIdentificadorResumoSetor(String linha) {
		if(linha.contains(IdentificadorArquivoInformatica.INICIO_RESUMO_SETOR.getDescricao())
			|| linha.contains(IdentificadorArquivoInformatica.INICIO_RESUMO_GERAL.getDescricao())){ 
			resumoSetor = true;
		}
		if((linha.contains(IdentificadorArquivoInformatica.FIM_RESUMO_SETOR.getDescricao()))
			|| linha.contains(IdentificadorArquivoInformatica.FIM_RESUMO_GERAL.getDescricao())){ 
			resumoSetor = false;
		}
	}

	private void localizarPagamentos(String linhaAtual) throws Exception {
		verificarIdentificadorPagamento(linhaAtual);
		localizarSecretariaSetor(linhaAtual);
		localizarUnidadeTrabalho(linhaAtual);
		localizarMatricula(linhaAtual);
		localizarEventosPagamento(linhaAtual);
		finalizarPagamento(linhaAtual);
	}
	
	private void finalizarPagamento(String linhaAtual) {
		if(processamentoPagamentoAtivo  &&	linhaAtual.contains(IdentificadorArquivoInformatica.FIM_PAGAMENTO_INDIVIDUAL.getDescricao())	){
			setTotaisPagamento(linhaAtual);
		}
	}

	private void setTotaisPagamento(String linhaAtual) {
		pagamento.setTotalRemuneracao(0d);
		pagamento.setTotalLiquido(Double.parseDouble(linhaAtual.replace(IdentificadorArquivoInformatica.FIM_PAGAMENTO_INDIVIDUAL.getDescricao(), "").replace(".","").replace(",", ".").trim()));
		pagamento.setTotalProventos(Double.parseDouble(linhaAnterior.substring(
				IdentificadorArquivoInformatica.VANTAGEM.getDescricao().length(), 
				linhaAnterior.indexOf(IdentificadorArquivoInformatica.DESCONTO.getDescricao())).replace(".","").replace(",", ".").trim()));
		pagamento.setTotalDescontos(Double.parseDouble(linhaAnterior.substring(
				linhaAnterior.indexOf(IdentificadorArquivoInformatica.DESCONTO.getDescricao()) + IdentificadorArquivoInformatica.DESCONTO.getDescricao().length(), 
				linhaAnterior.length()).replace(".","").replace(",", ".").trim()));
	}

	private void localizarEventosPagamento(String linhaAtual) {
		verificarIdentificadorEvento(linhaAtual);
		verificarIdentificadorResumoSetor(linhaAtual);
		if(processamentoEventos && processamentoPagamentoAtivo && !resumoSetor && !linhaAtual.contains(IdentificadorArquivoInformatica.SEPARADOR_ARQUIVO.getDescricao())){
			getDiasTrabalhados(linhaAtual);
			List<Evento> eventosNaLinha = getEventoNaLinha(linhaAtual);
			List<String> eventosLinha = carregarEventosLinha(linhaAtual, verificarPosicaoSegundoEvento(linhaAtual));
			for(Evento evento : eventosNaLinha){
				for(String linha : eventosLinha){
					if(linha.toUpperCase().contains(evento.getNome().toUpperCase())){
						getEventos().forEach((e) -> {
							if(e.getChave().equals(evento.getChave()) && e.getNome().equals(evento.getNome())){
								pagamento.getEventosPagamento().add(getEventoPagamento(linha, e));
							}
						});
					}
				}
			}
		}
	}

	private void getDiasTrabalhados(String linhaAtual) {
		if(linhaAtual.contains(IdentificadorArquivoInformatica.VENCIMENTO_BASE.getDescricao())){
			String dias = linhaAtual.substring(
					linhaAtual.indexOf(IdentificadorArquivoInformatica.VENCIMENTO_BASE.getDescricao()) 
					+ IdentificadorArquivoInformatica.VENCIMENTO_BASE.getDescricao().length()).trim();
			dias = dias.substring(0, dias.indexOf(IdentificadorArquivoInformatica.ESPACO_NA_LINHA.getDescricao())).trim();
			pagamento.setDiasTrabalhados(dias);
		}
	}

	private void localizarUnidadeTrabalho(String linhaAtual) {
		if( processamentoPagamentoAtivo && linhaAtual.substring(0, 5).equals(IdentificadorArquivoInformatica.IDENTIFICADOR_UNIDADE_TRABALHO.getDescricao())){
			matricula.getMatriculaPagamento().setUnidadeTrabalho(getUnidadeTrabalho(getUnidadeTrabalho(linhaAtual), linhaAtual));
		}else if(Util.palavraSomenteNumeros(linhaAtual.substring(0, 6))){
			int posicaoInicialUnidadeTrabalho = verificarPosicaoSegundoEvento(linhaAtual);
			matricula.getMatriculaPagamento().setUnidadeTrabalho(getUnidadeTrabalho(getUnidadeTrabalho(" " + linhaAtual.substring(posicaoInicialUnidadeTrabalho)), linhaAtual));
		}
	}

	private void localizarSecretariaSetor(String linhaAtual) {
		if(linhaAtual.contains(IdentificadorArquivoInformatica.SECRETARIA.getDescricao()) &&
		   linhaAtual.contains(IdentificadorArquivoInformatica.SETOR.getDescricao())	){
			secretaria = getSecretaria(getSecretaria(linhaAtual), linhaAtual);
			setor = getSetor(getSetor(linhaAtual), linhaAtual);
		}
	}

	private UnidadeTrabalho getUnidadeTrabalho(String linhaAtual) throws ApplicationException {
		UnidadeTrabalho unidadeTrabalho = new UnidadeTrabalho();
		try {
			String codigo = linhaAtual.substring(linhaAtual.indexOf(IdentificadorArquivoInformatica.ESPACO_NA_LINHA.getDescricao()));
			codigo = codigo.trim();
			codigo = codigo.substring(
					0,	codigo.indexOf(IdentificadorArquivoInformatica.ESPACO_NA_LINHA.getDescricao())).trim();
			String descricaoUnidadeTrabalho = linhaAtual.substring(linhaAtual.indexOf(codigo) + codigo.length(), 
					linhaAtual.lastIndexOf(IdentificadorArquivoInformatica.ESPACO_NA_LINHA.getDescricao())).trim();
			unidadeTrabalho.setCidade(getArquivoPagamento().getCidade());
			unidadeTrabalho.setTipoArquivo(getArquivoPagamento().getTipoArquivo());
			unidadeTrabalho.setDescricao(descricaoUnidadeTrabalho); 
			unidadeTrabalho.setCodigo(Integer.parseInt(codigo)); 
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar a Unidade Trabalho. Linha: " + linhaAtual);
		}
		return unidadeTrabalho;
	}
	
	private Secretaria getSecretaria(String linhaAtual) throws ApplicationException {
		Secretaria secretaria = new Secretaria();
		try {
			String descricao = linhaAtual.substring(
					linhaAtual.indexOf(IdentificadorArquivoInformatica.ESPACO_NA_LINHA.getDescricao()),
					linhaAtual.indexOf(IdentificadorArquivoInformatica.SETOR.getDescricao())).trim();
			descricao = descricao.substring(
					descricao.indexOf(IdentificadorArquivoInformatica.ESPACO_NA_LINHA.getDescricao())).trim();
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
			String descricao = linhaAtual.substring(
					linhaAtual.indexOf(IdentificadorArquivoInformatica.SETOR.getDescricao()) + IdentificadorArquivoInformatica.SETOR.getDescricao().length()).trim();
			descricao = descricao.substring(
					descricao.indexOf(IdentificadorArquivoInformatica.ESPACO_NA_LINHA.getDescricao()),
					descricao.length()).trim();
			setor.setCidade(getArquivoPagamento().getCidade());
			setor.setTipoArquivo(getArquivoPagamento().getTipoArquivo());
			setor.setDescricao(descricao);
			
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar o Setor. Linha: " + linhaAtual);
		}
		return setor;
	}

	private  void verificarIdentificadorPagamento(String linha) {
		if(linhaAnterior.contains(IdentificadorArquivoInformatica.INICIO_PAGAMENTOS.getDescricao())
		   &&  linha.contains(IdentificadorArquivoInformatica.SEPARADOR_ARQUIVO.getDescricao())	){
			processamentoPagamentoAtivo = true;
		}
		if(linhaAnterior.contains(IdentificadorArquivoInformatica.SEPARADOR_ARQUIVO.getDescricao())
		   &&  linha.contains(IdentificadorArquivoInformatica.SEPARADOR_ARQUIVO.getDescricao())	){
			processamentoPagamentoAtivo = false;
		}
	}
	
	private EventoPagamento getEventoPagamento(String linha, Evento evento) {
		EventoPagamento eventoPagamento = new EventoPagamento();
		try {
			eventoPagamento.setPagamento(pagamento);
			eventoPagamento.setEvento(evento);
			eventoPagamento.setValor(Double.parseDouble(getValorEvento(linha, linha.lastIndexOf(IdentificadorArquivoInformatica.VIRGULA.getDescricao()))));
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar o Evento Pagamento. Linha: " + linha);
		}
		return eventoPagamento;
	}

	private String getValorEvento(String linha, int posicaoVirgula) {
		try {
			String linhaInvertida = new StringBuffer(linha.substring(0, posicaoVirgula)).reverse().toString();
			int posicaoEspacoLinhaInvertida = linhaInvertida.indexOf(IdentificadorArquivoInformatica.ESPACO_NA_LINHA.getDescricao()) ;
			String valor = linha.substring(posicaoVirgula - posicaoEspacoLinhaInvertida, posicaoVirgula + 3);
			return valor.replace(".","").replace(",", ".");
		} catch (Exception e) {	
			throw new ApplicationException("Erro ao pegar o Valor Evento. Linha: " + linha);
		}
	}

	private  void localizarMatricula(String linhaAtual) throws Exception {
		if(processamentoPagamentoAtivo 
				   &&	linhaAnterior.contains(IdentificadorArquivoInformatica.SEPARADOR_ARQUIVO.getDescricao())
				   &&	!linhaAtual.contains(IdentificadorArquivoInformatica.SEPARADOR_ARQUIVO.getDescricao())	){
			String numeroMatricula = getNumeroMatricula(linhaAtual);
			verificarPagamento(numeroMatricula);
			getMatricula(numeroMatricula, linhaAtual);
		}
	}

	private void verificarPagamento(String numeroMatricula) {
		if(Objects.nonNull(pagamento) && Objects.nonNull(matricula) && !matricula.getNumeroMatricula().equals(numeroMatricula)){
			pagamentos.add(pagamento);
		}
		pagamento = new Pagamento();
	}
	
	private Date getDataAdmissao(String linhaAtual) {
		try {
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			return (Date)formatter.parse(linhaAtual.substring(
					linhaAtual.length() -10,linhaAtual.length()));
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar a Data de Admissão. Linha: " + linhaAtual);
		}
	}

	private String getNumeroMatricula(String linhaAtual) throws Exception {
		String numeroMatricula = linhaAtual.substring(0,5).trim();
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
	
	private void setMatriculaPagamento(String linhaAtual) {
		matricula.setMatriculaPagamento(new MatriculaPagamento());
		matricula.getMatriculaPagamento().setSecretaria(secretaria);
		matricula.getMatriculaPagamento().setCargaHorariaPagamento(cargaHorariaPagamento);
		matricula.getMatriculaPagamento().setSetor(setor);
		matricula.getMatriculaPagamento().setVinculo(getVinculo(getVinculo(linhaAtual), linhaAtual));
		matricula.getMatriculaPagamento().setCargo(getCargo(getCargo(linhaAtual), linhaAtual));
		matricula.getMatriculaPagamento().setAno(getArquivoPagamento().getAno());
		matricula.getMatriculaPagamento().setMes(getArquivoPagamento().getMes());
		matricula.getMatriculaPagamento().setMatricula(matricula);
//		matricula.getMatriculaPagamento().setCargaHoraria(99);
		getMatriculaPagamentoService().salvar(matricula.getMatriculaPagamento());
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
	
	private  void novaMatricula(String numeroMatricula, String linhaAtual) throws ApplicationException {
		matricula = new Matricula();
		matricula.setNumeroMatricula(numeroMatricula);
		matricula.setDataAdmissao(getDataAdmissao(linhaAtual));
		matricula.setNomeFuncionario(getNomeFuncionario(linhaAtual));
	}

	private Cargo getCargo(String linhaAtual) throws ApplicationException {
		Cargo cargo = new Cargo();
		try {
			int posicaoInicialCargo = verificarPosicaoSegundoEvento(linhaAtual);
			String palavraComCargo = linhaAtual.substring(posicaoInicialCargo, linhaAtual.lastIndexOf(matricula.getMatriculaPagamento().getVinculo().getDescricao()));
			Integer numero = Integer.parseInt(palavraComCargo.substring(0,4));
			String descricao = palavraComCargo.substring(5, palavraComCargo.length()).toUpperCase().trim();
			if(StringUtils.notNullOrEmpty(descricao)){
				cargo.setCidade(getArquivoPagamento().getCidade());
				cargo.setTipoArquivo(getArquivoPagamento().getTipoArquivo());
				cargo.setNumero(numero); 
				cargo.setDescricao(descricao);
			}else {
				cargo = null;
			}
			
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar o Cargo. Linha: " + linhaAtual);
		}
		return cargo;
	}

	private String getNomeFuncionario(String linha) throws ApplicationException {
		String nome= "";
		try {
			nome = linha.substring(5,verificarPosicaoSegundoEvento(linha));
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar o Nome do Funcionário. Linha: " + linha);
		}
		return nome ;
		
	}

	private Vinculo getVinculo(String linha) throws ApplicationException {
		Vinculo vinculo = new Vinculo();
		try {
			String descricao = linha.substring(0,linha.length() - 10).trim();
			descricao = descricao.substring(descricao.lastIndexOf(IdentificadorArquivoInformatica.ESPACO_NA_LINHA.getDescricao()), descricao.length()).trim();
			Integer numero = descricao.substring(0,2).hashCode();
			vinculo.setNumero(numero); 
			vinculo.setDescricao(descricao);
			vinculo.setCidade(getArquivoPagamento().getCidade());
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar o Vínculo. Linha: " + linha);
		}
		return vinculo;
	}
	
}


