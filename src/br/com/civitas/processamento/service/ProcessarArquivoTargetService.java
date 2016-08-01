package br.com.civitas.processamento.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.civitas.arquitetura.ApplicationException;
import br.com.civitas.processamento.entity.ArquivoPagamento;
import br.com.civitas.processamento.entity.CargaHorariaPagamento;
import br.com.civitas.processamento.entity.Cargo;
import br.com.civitas.processamento.entity.Evento;
import br.com.civitas.processamento.entity.EventoPagamento;
import br.com.civitas.processamento.entity.Matricula;
import br.com.civitas.processamento.entity.NivelPagamento;
import br.com.civitas.processamento.entity.Pagamento;
import br.com.civitas.processamento.entity.Secretaria;
import br.com.civitas.processamento.entity.Setor;
import br.com.civitas.processamento.entity.UnidadeTrabalho;
import br.com.civitas.processamento.entity.Vinculo;
import br.com.civitas.processamento.enums.IdentificadorArquivoTarget;
import br.com.civitas.processamento.interfac.IProcessarArquivoPagamento;

@Service
public class ProcessarArquivoTargetService extends ProcessarArquivoPagamento implements IProcessarArquivoPagamento{

	@Autowired
	private  PagamentoService pagamentoService;
	
	@Autowired
	private  CargoService cargoService;
	
	@Autowired
	private  SecretariaService secretariaService;
	
	@Autowired
	private  UnidadeTrabalhoService unidadeTrabalhoService;
	
	@Autowired
	private  NivelPagamentoService nivelPagamentoService;
	
	@Autowired
	private  CargaHorariaPagamentoService cargaHorariaPagamentoService;
	
	@Autowired
	private  SetorService setorService;
	
	@Autowired
	private  VinculoService vinculoService;
	
	@Autowired
	private  EventoService eventoService ;
	
	private  Pagamento pagamento;
	private  Matricula matricula;
	private  Secretaria secretaria;
	private  NivelPagamento nivelPagamento;
	private  UnidadeTrabalho unidadeTrabalho;
	private  CargaHorariaPagamento cargaHorariaPagamento;
	private  Setor setor;
	private  List<Pagamento> pagamentos;
	private  List<Matricula> matriculas;
	private  boolean processamentoPagamentoAtivo = false;
	private  boolean processamentoEventos = false;
	private  boolean resumoSetor = false;
	private  String linhaAnterior = "";

	
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
		processamentoEventos = false;
		BufferedReader br = new BufferedReader(getFilReaderPagamento());
		while (br.ready()) {
			String linha = br.readLine(); 
			localizarPagamentos(linha);
			linhaAnterior = linha;
		}
		pagamentos.add(pagamento);
		br.close();
		pagamentoService.inserirPagamentos(pagamentos,getEventos(), getArquivoPagamento());
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
		nivelPagamento = null;
		cargaHorariaPagamento = null;
		pagamentos = new ArrayList<Pagamento>();
		matriculas = new ArrayList<Matricula>();
		setNiveisPagamento(nivelPagamentoService.buscarTipoArquivoCidade(getArquivoPagamento().getCidade(), getArquivoPagamento().getTipoArquivo()));
		setCargasHorariaPagamento(cargaHorariaPagamentoService.buscarTipoArquivoCidade(getArquivoPagamento().getCidade(), getArquivoPagamento().getTipoArquivo()));
		setUnidadesTrabalho(unidadeTrabalhoService.buscarTipoArquivoCidade(getArquivoPagamento().getCidade(), getArquivoPagamento().getTipoArquivo()));
		setSetores(setorService.buscarTipoArquivoCidade(getArquivoPagamento().getCidade(), getArquivoPagamento().getTipoArquivo()));
		setSecretarias(secretariaService.buscarTipoArquivoCidade(getArquivoPagamento().getCidade(), getArquivoPagamento().getTipoArquivo()));
		setCargos(cargoService.buscarTipoArquivoCidade(getArquivoPagamento().getCidade(), getArquivoPagamento().getTipoArquivo()));
		setVinculos(vinculoService.buscarPorCidade(getArquivoPagamento().getCidade()));
		setEventos(eventoService.buscarTipoArquivoCidade(getArquivoPagamento().getCidade(), getArquivoPagamento().getTipoArquivo()));
		processamentoPagamentoAtivo = false;
		processamentoEventos = false;
		resumoSetor = false;
		linhaAnterior = "";
	}

	private void localizarEvento(String linha) {
		verificarIdentificadorEvento(linha);
		verificarIdentificadorResumoSetor(linha);
		if(processamentoEventos && !resumoSetor && !linha.contains(IdentificadorArquivoTarget.SEPARADOR_ARQUIVO.getDescricao())){
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
				evento.setChave(linhaEvento.substring(0,3));
				int posicaoInicialIdentificador = linha.indexOf(IdentificadorArquivoTarget.HIFEN.getDescricao()) + 1 ;
				int posicaoPrimeiraVirgula = linha.indexOf(IdentificadorArquivoTarget.VIRGULA.getDescricao()) ;
				String linhaInvertida = new StringBuffer(linha.substring(0, posicaoPrimeiraVirgula)).reverse().toString();
				int posicaoEspacoLinhaInvertida = linhaInvertida.indexOf(IdentificadorArquivoTarget.ESPACO_NA_LINHA.getDescricao()) ;
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
			if(!existeCaractereInvalido(palavra) || !palavraSomenteNumeros(palavra)){
				nomeVerificado = nomeVerificado + palavra + " ";
			}
		}
		return nomeVerificado.toUpperCase().trim();
	}

	private boolean existeCaractereInvalido(String palavra) {
		for (char letra : palavra.toCharArray())  {
			if( letra == IdentificadorArquivoTarget.BARRA.getDescricao().charAt(0) ||
				letra == IdentificadorArquivoTarget.PERCENTUAL.getDescricao().charAt(0) ||	
				letra == IdentificadorArquivoTarget.VIRGULA.getDescricao().charAt(0) 	){
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
			if(palavra.length()==3 && palavraSomenteNumeros(palavra) && !primeiraPalavra){
				return linha.substring(3,linha.length()).indexOf(palavra) + 3;
			}
			primeiraPalavra = false;
		}
		return 0;
	}

	private boolean palavraSomenteNumeros(String palavra) {
		try {
			Integer.parseInt(palavra);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private void verificarIdentificadorEvento(String linha) {
		if(linha.contains(IdentificadorArquivoTarget.INICIO_EVENTO.getDescricao())){
			processamentoEventos = true;
		}
		if(linha.contains(IdentificadorArquivoTarget.FIM_EVENTO.getDescricao()) && processamentoEventos ){
			processamentoEventos = false;
		}
	}

	private void verificarIdentificadorResumoSetor(String linha) {
		if(linha.contains(IdentificadorArquivoTarget.INICIO_RESUMO_SETOR.getDescricao())){ 
			resumoSetor = true;
		}
		if(linha.contains(IdentificadorArquivoTarget.FIM_RESUMO_SETOR.getDescricao())){ 
			resumoSetor = false;
		}
	}

	private void localizarPagamentos(String linhaAtual) throws Exception {
		verificarIdentificadorPagamento(linhaAtual);
		localizarSecretaria(linhaAtual);
		localizarUnidadeTrabalho(linhaAtual);
		localizarMatricula(linhaAtual);
		localizarEventosPagamento(linhaAtual);
		finalizarPagamento(linhaAtual);
	}
	
	private void finalizarPagamento(String linhaAtual) {
		if(processamentoPagamentoAtivo  && !processamentoEventos && isTotalPagamento(linhaAtual)){
			setTotaisPagamento(linhaAtual);
		}
	}

	private boolean isTotalPagamento(String linhaAtual) {
		String[] palavras = linhaAtual.split(" ");
		int vezes = 0;
		for(String palavra : palavras){
			if(palavra.contains(",")){
				vezes++;
			}
		}
		return vezes==3;
	}

	private void setTotaisPagamento(String linhaAtual) {
		try {
			String[] valores = linhaAtual.split(" ");
			valores = removerValoresInvalidos(valores);
			pagamento.setTotalRemuneracao(0d);
			pagamento.setTotalProventos(Double.parseDouble(valores[0].replace(".","").replace(",", ".").trim()));
			pagamento.setTotalDescontos(Double.parseDouble(valores[1].replace(".","").replace(",", ".").trim()));
			pagamento.setTotalLiquido(Double.parseDouble(valores[2].replace(".","").replace(",", ".").trim()));
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar Totais de Pagamento. Linha: " + linhaAtual);
		}
	}

	private String[] removerValoresInvalidos(String[] valores) {
		String[] valoresValidos = new String[3];
		int posicao = 0;
		for(String valor : valores){
			if(valor.contains(",")){
				valoresValidos[posicao++] = valor;
			}
		}
		return valoresValidos;
	}

	private void localizarEventosPagamento(String linhaAtual) {
		verificarIdentificadorEvento(linhaAtual);
		verificarIdentificadorResumoSetor(linhaAtual);
		if(processamentoEventos && processamentoPagamentoAtivo && !resumoSetor && !linhaAtual.contains(IdentificadorArquivoTarget.SEPARADOR_ARQUIVO.getDescricao())){
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

	private void localizarUnidadeTrabalho(String linhaAtual) {
		if(linhaAtual.contains(IdentificadorArquivoTarget.FOLHA.getDescricao()) &&
				linhaAtual.contains(IdentificadorArquivoTarget.REFERENCIA.getDescricao())	){
			unidadeTrabalho = getUnidadeTrabalho(getUnidadeTrabalho(linhaAtual), linhaAtual);
		}
	}

	private void localizarSecretaria(String linhaAtual) {
		if(linhaAtual.contains(IdentificadorArquivoTarget.DEPARTAMENTO.getDescricao()) &&
		   linhaAtual.contains(IdentificadorArquivoTarget.COMPETENCIA.getDescricao())	){
			secretaria = getSecretaria(getSecretaria(linhaAtual), linhaAtual);
			setor = getSetor(getSetor(linhaAtual), linhaAtual);
		}
	}

	private UnidadeTrabalho getUnidadeTrabalho(String linhaAtual) throws ApplicationException {
		UnidadeTrabalho unidadeTrabalho = new UnidadeTrabalho();
		try {
			String codigo = linhaAtual.substring(
							linhaAtual.indexOf(IdentificadorArquivoTarget.FOLHA.getDescricao()) + IdentificadorArquivoTarget.FOLHA.getDescricao().length(),
							linhaAtual.indexOf(IdentificadorArquivoTarget.HIFEN.getDescricao())).trim();
			String descricaoUnidadeTrabalho = linhaAtual.substring(
					linhaAtual.indexOf(IdentificadorArquivoTarget.HIFEN.getDescricao()) + 1,
					linhaAtual.indexOf(IdentificadorArquivoTarget.REFERENCIA.getDescricao())).trim();
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
					linhaAtual.indexOf(IdentificadorArquivoTarget.HIFEN.getDescricao()) + 1,
					linhaAtual.indexOf(IdentificadorArquivoTarget.COMPETENCIA.getDescricao())).trim();
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
					linhaAtual.indexOf(IdentificadorArquivoTarget.HIFEN.getDescricao()) + 1,
					linhaAtual.indexOf(IdentificadorArquivoTarget.COMPETENCIA.getDescricao())).trim();
			setor.setCidade(getArquivoPagamento().getCidade());
			setor.setTipoArquivo(getArquivoPagamento().getTipoArquivo());
			setor.setDescricao(descricao);
			
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar o Setor. Linha: " + linhaAtual);
		}
		return setor;
	}

	private  void verificarIdentificadorPagamento(String linha) {
		if(linhaAnterior.contains(IdentificadorArquivoTarget.INICIO_PAGAMENTOS.getDescricao())
		   &&  linha.contains(IdentificadorArquivoTarget.SEPARADOR_ARQUIVO.getDescricao())	){
			processamentoPagamentoAtivo = true;
		}
		if(processamentoPagamentoAtivo && linhaAnterior.contains(IdentificadorArquivoTarget.SEPARADOR_ARQUIVO.getDescricao())
		   &&  linha.contains(IdentificadorArquivoTarget.FIM_PAGAMENTOS.getDescricao()) 	){
			processamentoPagamentoAtivo = false;
		}
	}
	
	private EventoPagamento getEventoPagamento(String linha, Evento evento) {
		EventoPagamento eventoPagamento = new EventoPagamento();
		try {
			eventoPagamento.setPagamento(pagamento);
			eventoPagamento.setEvento(evento);
			eventoPagamento.setValor(Double.parseDouble(getValorEvento(linha, linha.lastIndexOf(IdentificadorArquivoTarget.VIRGULA.getDescricao()))));
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar o Evento Pagamento. Linha: " + linha);
		}
		return eventoPagamento;
	}

	private String getValorEvento(String linha, int posicaoVirgula) {
		try {
			String linhaInvertida = new StringBuffer(linha.substring(0, posicaoVirgula)).reverse().toString();
			int posicaoEspacoLinhaInvertida = linhaInvertida.indexOf(IdentificadorArquivoTarget.ESPACO_NA_LINHA.getDescricao()) ;
			String valor = linha.substring(posicaoVirgula - posicaoEspacoLinhaInvertida, posicaoVirgula + 3);
			return valor.replace(".","").replace(",", ".");
		} catch (Exception e) {	
			throw new ApplicationException("Erro ao pegar o Valor Evento. Linha: " + linha);
		}
	}

	private  void localizarMatricula(String linhaAtual) throws Exception {
		if(processamentoPagamentoAtivo  &&	linhaAtual.contains(IdentificadorArquivoTarget.CPF.getDescricao())	){
			String numeroMatricula = getNumeroMatricula(linhaAtual);
			verificarPagamento(numeroMatricula);
			novaMatricula(numeroMatricula, linhaAtual);
		}
		if(processamentoPagamentoAtivo  &&	linhaAtual.contains(IdentificadorArquivoTarget.ADMISSAO.getDescricao())	){
			matricula.setDataAdmissao(getDataAdmissao(linhaAtual));
			matricula.setVinculo(getVinculo(getVinculo(linhaAtual), linhaAtual));
			matricula.setCargo(getCargo(getCargo(linhaAtual), linhaAtual));
			matricula.setCargaHoraria(getCargaHoraria(linhaAtual));
		}
	}

	private int getCargaHoraria(String linhaAtual) throws ApplicationException {
		int cargaHorariaNumero = 0;
		try {
			String cargaHoraria = linhaAtual.substring(linhaAtual.indexOf(
					IdentificadorArquivoTarget.CARGA_HORARIA.getDescricao()) + IdentificadorArquivoTarget.CARGA_HORARIA.getDescricao().length(),
					linhaAtual.length()).trim();
			cargaHorariaNumero = Integer.parseInt(cargaHoraria.trim());
		} catch (Exception e) {
			throw new ApplicationException ("Erro ao pegar o Carga Horária. Linha: " + linhaAnterior);
		}
		return cargaHorariaNumero;
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
					linhaAtual.indexOf(IdentificadorArquivoTarget.ADMISSAO.getDescricao()) + IdentificadorArquivoTarget.ADMISSAO.getDescricao().length(),
					linhaAtual.indexOf(IdentificadorArquivoTarget.CARGA_HORARIA.getDescricao())).trim());
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

	private  void novaMatricula(String numeroMatricula, String linhaAtual) throws ApplicationException {
		matricula = new Matricula();
		matricula.setSecretaria(secretaria);
		matricula.setNivelPagamento(nivelPagamento);
		matricula.setCargaHorariaPagamento(cargaHorariaPagamento);
		matricula.setSetor(setor);
		matricula.setUnidadeTrabalho(unidadeTrabalho);
		matricula.setNumeroMatricula(numeroMatricula);
		matricula.setNomeFuncionario(getNomeFuncionario(linhaAtual));;
		matriculas.add(matricula);
		pagamento.setMatricula(matricula);
	}

	private Cargo getCargo(String linhaAtual) throws ApplicationException {
		Cargo cargo = new Cargo();
		try {
			String descricao = linhaAtual.substring(0,
								linhaAtual.indexOf(IdentificadorArquivoTarget.HIFEN.getDescricao())).toUpperCase().trim();
			Integer numero = descricao.substring(0,2).hashCode();
			cargo.setCidade(getArquivoPagamento().getCidade());
			cargo.setTipoArquivo(getArquivoPagamento().getTipoArquivo());
			cargo.setNumero(numero); 
			cargo.setDescricao(descricao);
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar o Cargo. Linha: " + linhaAtual);
		}
		return cargo;
	}

	private String getNomeFuncionario(String linha) throws ApplicationException {
		String nome= "";
		try {
			nome = linha.substring(
					linha.indexOf(IdentificadorArquivoTarget.HIFEN.getDescricao()) + 1,
					linha.indexOf(IdentificadorArquivoTarget.CPF.getDescricao()) ).toUpperCase().trim();
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar o Nome do Funcionário. Linha: " + linha);
		}
		return nome ;
		
	}

	private Vinculo getVinculo(String linha) throws ApplicationException {
		Vinculo vinculo = new Vinculo();
		try {
			String descricao = linha.substring(
								linha.indexOf(IdentificadorArquivoTarget.HIFEN.getDescricao()) + 1,
								linha.indexOf(IdentificadorArquivoTarget.ADMISSAO.getDescricao()) ).trim();
			Integer numero = descricao.substring(0,2).hashCode();
			vinculo.setNumero(numero); 
			vinculo.setDescricao(descricao);
			vinculo.setCidade(getArquivoPagamento().getCidade());
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar o Vínculo. Linha: " + linha);
		}
		return vinculo;
	}
	
	public void setPagamentoService(PagamentoService pagamentoService) {
		this.pagamentoService = pagamentoService;
	}

	public void setEventoService(EventoService eventoService) {
		this.eventoService = eventoService;
	}

}


