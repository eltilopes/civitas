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
import br.com.civitas.processamento.enums.IdentificadorArquivoLayout;
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
			linhaAnterior = linha;
		}
		linhaAnterior = "";
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
		if(processamentoEventos && !resumoSetor && !linha.contains(IdentificadorArquivoTarget.INICIO_EVENTO.getDescricao())){
			getEvento(linha, getChaveEvento(linha));
		}
	}

	private String getChaveEvento(String linha) {
		try {
			int posicaoInicialIdentificador = 0 ;
			int posicaoPrimeiraVirgula = linha.indexOf(IdentificadorArquivoLayout.VIRGULA.getDescricao()) ;
			String linhaInvertida = new StringBuffer(linha.substring(0, posicaoPrimeiraVirgula)).reverse().toString();
			int posicaoEspacoLinhaInvertida = linhaInvertida.indexOf(IdentificadorArquivoLayout.ESPACO_NA_LINHA.getDescricao()) ;
			posicaoEspacoLinhaInvertida = posicaoEspacoLinhaInvertida  + 1 + linhaInvertida.substring(posicaoEspacoLinhaInvertida + 1).indexOf(IdentificadorArquivoLayout.ESPACO_NA_LINHA.getDescricao()) ;
			return linha.substring(posicaoInicialIdentificador, posicaoPrimeiraVirgula - posicaoEspacoLinhaInvertida).trim();
			
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar o Chave Evento. Linha: " + linha);
		}
	}
	
	private void verificarIdentificadorEvento(String linha) {
		if(linha.contains(IdentificadorArquivoTarget.INICIO_EVENTO.getDescricao()) && linhaAnterior.contains(IdentificadorArquivoTarget.VINCULO.getDescricao())){
			processamentoEventos = true;
		}
		if((linha.contains(IdentificadorArquivoTarget.FIM_EVENTO.getDescricao()) || fimPagina(linha)) && processamentoEventos ){
			processamentoEventos = false;
		}
	}

	private boolean fimPagina(String linha) {
		return linha.toUpperCase().contains(getArquivoPagamento().getCidade().getDescricao().toUpperCase());
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
		if(processamentoEventos && processamentoPagamentoAtivo && !resumoSetor && !linhaAtual.contains(IdentificadorArquivoTarget.INICIO_EVENTO.getDescricao())){
			for(Evento evento : getEventos()){
				if(linhaAtual.toUpperCase().contains(evento.getNome().toUpperCase())){
						pagamento.getEventosPagamento().add(getEventoPagamento(linhaAtual, evento));
				}
			}		
		}	
	}

	private void localizarSecretaria(String linhaAtual) {
		if(linhaAtual.contains(IdentificadorArquivoTarget.ORGAO.getDescricao()) 	){
			secretaria = getSecretaria(getSecretaria(linhaAtual), linhaAtual);
			unidadeTrabalho = getUnidadeTrabalho(getUnidadeTrabalho(linhaAnterior), linhaAnterior);
		}
	}

	private UnidadeTrabalho getUnidadeTrabalho(String linha) throws ApplicationException {
		UnidadeTrabalho unidadeTrabalho = new UnidadeTrabalho();
		try {
			int posicaoPrimeiroNumero = posicaoPrimeiroNumero(linha);
			String codigo = linha.substring(posicaoPrimeiroNumero, linha.length()).trim();
			String descricaoUnidadeTrabalho = linha.substring(0, posicaoPrimeiroNumero).trim();
			unidadeTrabalho.setCidade(getArquivoPagamento().getCidade());
			unidadeTrabalho.setTipoArquivo(getArquivoPagamento().getTipoArquivo());
			unidadeTrabalho.setDescricao(descricaoUnidadeTrabalho); 
			unidadeTrabalho.setCodigo(Integer.parseInt(codigo)); 
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar a Unidade Trabalho. Linha: " + linha);
		}
		return unidadeTrabalho;
	}
	
	private int posicaoPrimeiroNumero(String linha) {
	    char[] c = linha.toCharArray();
	    for ( int i = 0; i < c.length; i++ ){
	        if ( Character.isDigit( c[ i ] ) ) {
	            return i;
	        }
	    }
	    return linha.length();
	}

	private Secretaria getSecretaria(String linhaAtual) throws ApplicationException {
		Secretaria secretaria = new Secretaria();
		try {
			String descricao = linhaAtual.substring(
					linhaAtual.indexOf(IdentificadorArquivoTarget.ESPACO_NA_LINHA.getDescricao()) + 1,
					linhaAtual.indexOf(IdentificadorArquivoTarget.ORGAO.getDescricao())).trim();
			secretaria.setCidade(getArquivoPagamento().getCidade());
			secretaria.setTipoArquivo(getArquivoPagamento().getTipoArquivo());
			secretaria.setDescricao(descricao);
			
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar a Secretaria. Linha: " + linhaAtual);
		}
		return secretaria;
	}
	
	private  void verificarIdentificadorPagamento(String linha) {
		if(linha.contains(IdentificadorArquivoTarget.INICIO_PAGAMENTOS.getDescricao())){
			processamentoPagamentoAtivo = true;
		}
		if(processamentoPagamentoAtivo  &&  linha.contains(IdentificadorArquivoTarget.FIM_PAGAMENTOS.getDescricao()) 	){
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
		if(processamentoPagamentoAtivo  &&	linhaAtual.contains(IdentificadorArquivoTarget.VINCULO.getDescricao())	){
			String numeroMatricula = getNumeroMatricula();
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
			return (Date)formatter.parse(linhaAtual.substring(0,10).trim());
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar a Data de Admissão. Linha: " + linhaAtual);
		}
	}

	private String getNumeroMatricula() throws Exception {
		String numeroMatricula = linhaAnterior.substring(0,5).trim();
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

		System.out.println(linhaAnterior);
//		String[] palavras = linhaAnterior.split(" ");
//		for(String palavra : palavras){
//			JLabel label = new JLabel(palavra);
//			System.out.println(palavra + " = " + label.getFont().isBold() );
//			System.out.println(palavra + " = " + label.getFont().getName() );
//			System.out.println(palavra + " = " + label.getFont().getFontName() );
//			System.out.println(palavra + " = " + label.getFont().getStyle() );
//			System.out.println(palavra + " = " + label.getFont().getSize() );
//		}
		
		
//		String nome= "";
//		try {
//			nome = linha.substring(
//					linha.indexOf(IdentificadorArquivoTarget.HIFEN.getDescricao()) + 1,
//					linha.indexOf(IdentificadorArquivoTarget.CPF.getDescricao()) ).toUpperCase().trim();
//		} catch (Exception e) {
//			throw new ApplicationException("Erro ao pegar o Nome do Funcionário. Linha: " + linha);
//		}
//		return nome ;
		return linha;
		
	}

	private Vinculo getVinculo(String linha) throws ApplicationException {
		Vinculo vinculo = new Vinculo();
		try {
			String descricao = linha.substring(11, linha.indexOf(IdentificadorArquivoTarget.ADMISSAO.getDescricao()) ).trim();
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


