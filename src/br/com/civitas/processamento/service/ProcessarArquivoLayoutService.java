package br.com.civitas.processamento.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.civitas.arquitetura.ApplicationException;
import br.com.civitas.processamento.entity.ArquivoPagamento;
import br.com.civitas.processamento.entity.Cargo;
import br.com.civitas.processamento.entity.Evento;
import br.com.civitas.processamento.entity.EventoPagamento;
import br.com.civitas.processamento.entity.Matricula;
import br.com.civitas.processamento.entity.Pagamento;
import br.com.civitas.processamento.entity.Vinculo;
import br.com.civitas.processamento.enums.IdentificadorArquivoLayout;
import br.com.civitas.processamento.interfac.IProcessarArquivoPagamento;

@Service
public class ProcessarArquivoLayoutService extends ProcessarArquivoPagamento implements IProcessarArquivoPagamento{

	@Autowired
	private  PagamentoService pagamentoService;
	
	@Autowired
	private  CargoService cargoService;
	
	@Autowired
	private  VinculoService vinculoService;
	
	@Autowired
	private  EventoService eventoService ;
	
	private  Pagamento pagamento;
	private  Matricula matricula;
	private  List<Pagamento> pagamentos;
	private  List<Matricula> matriculas;
	private  boolean processamentoPagamentoAtivo = false;
	private  boolean processamentoEventos = false;
	private  String ultimaLinha = "";
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
		BufferedReader br = new BufferedReader(getFilReaderPagamento());
		while (br.ready()) {
			String linha = br.readLine(); 
			localizarPagamentos(linha);
			linhaAnterior = linha;
		}
		pagamento.getMatricula().setVinculo(getVinculo(getVinculo(ultimaLinha),ultimaLinha));
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
		pagamentos = new ArrayList<Pagamento>();
		matriculas = new ArrayList<Matricula>();
		setCargos(cargoService.buscarTipoArquivoCidade(getArquivoPagamento().getCidade(), getArquivoPagamento().getTipoArquivo()));
		setVinculos(vinculoService.buscarPorCidade(getArquivoPagamento().getCidade()));
		setEventos(eventoService.buscarTipoArquivoCidade(getArquivoPagamento().getCidade(), getArquivoPagamento().getTipoArquivo()));
		processamentoPagamentoAtivo = false;
		processamentoEventos = false;
		ultimaLinha = "";
		linhaAnterior = "";
	}

	private void localizarEvento(String linha) {
		verificarIdentificadorEvento(linha);
		if(processamentoEventos && !(linha.contains(IdentificadorArquivoLayout.INICIO_EVENTO.getDescricao()))){
			getEvento(linha, getChaveEvento(linha));
		}
	}

	private String getChaveEvento(String linha) {
		try {
			int posicaoInicialIdentificador = 3 ;
			int posicaoPrimeiraVirgula = linha.indexOf(IdentificadorArquivoLayout.VIRGULA.getDescricao()) ;
			String linhaInvertida = new StringBuffer(linha.substring(0, posicaoPrimeiraVirgula)).reverse().toString();
			int posicaoEspacoLinhaInvertida = linhaInvertida.indexOf(IdentificadorArquivoLayout.ESPACO_NA_LINHA.getDescricao()) ;
			return linha.substring(posicaoInicialIdentificador, posicaoPrimeiraVirgula - posicaoEspacoLinhaInvertida);
			
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar o Chave Evento. Linha: " + linha);
		}
	}
	
	private void verificarIdentificadorEvento(String linha) {
		if(linha.contains(IdentificadorArquivoLayout.INICIO_EVENTO.getDescricao())){
			processamentoEventos = true;
		}
		if((linha.contains(IdentificadorArquivoLayout.FIM_EVENTO.getDescricao()))){
			processamentoEventos = false;
		}
	}

	private void localizarPagamentos(String linhaAtual) throws Exception {
		localizarMatricula(linhaAtual);
		verificarIdentificador(linhaAtual, getArquivoPagamento().getNomeArquivo());
	}
	
	private  void verificarIdentificador(String linha, String nomeArquivo) {
		getEventos().forEach((e) -> {
			if(processamentoPagamentoAtivo && !linha.contains(IdentificadorArquivoLayout.CARGO.getDescricao())
					&& getChaveEvento(linha).contains(e.getChave())){
				pagamento.getEventosPagamento().add(getEventoPagamento(linha, e));
			}
		});
		if(linha.contains(IdentificadorArquivoLayout.TOTAIS_PAGAMENTO.getDescricao())){
			setTotaisPagamento(linha);
			processamentoPagamentoAtivo = false;
		}
	}
	
	private void setTotaisPagamento(String linha) {
		try {
			int inicioRemuneracao = linha.indexOf(IdentificadorArquivoLayout.TOTAIS_PAGAMENTO.getDescricao()) + IdentificadorArquivoLayout.TOTAIS_PAGAMENTO.getDescricao().length();
			int finalRemuneracao = linha.substring(inicioRemuneracao).indexOf(IdentificadorArquivoLayout.VIRGULA.getDescricao()) + inicioRemuneracao +3 ;
			int finalLiquido = linha.substring(finalRemuneracao).indexOf(IdentificadorArquivoLayout.VIRGULA.getDescricao()) + finalRemuneracao + 3;
			int finalProventos = linha.substring(finalLiquido).indexOf(IdentificadorArquivoLayout.VIRGULA.getDescricao()) + finalLiquido + 3;
			int finalDescontos = linha.length();
			pagamento.setTotalRemuneracao(Double.parseDouble(linha.substring(inicioRemuneracao, finalRemuneracao).replace(".","").replace(",", ".")));
			pagamento.setTotalLiquido(Double.parseDouble(linha.substring(finalRemuneracao, finalLiquido).replace(".","").replace(",", ".")));
			pagamento.setTotalProventos(Double.parseDouble(linha.substring(finalLiquido, finalProventos).replace(".","").replace(",", ".")));
			pagamento.setTotalDescontos(Double.parseDouble(linha.substring(finalProventos, finalDescontos).replace(".","").replace(",", ".")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private EventoPagamento getEventoPagamento(String linha, Evento evento) {
		EventoPagamento eventoPagamento = new EventoPagamento();
		try {
			eventoPagamento.setPagamento(pagamento);
			eventoPagamento.setEvento(evento);
			if(!linha.contains(IdentificadorArquivoLayout.PENSAO_ALIMENTICIA.getDescricao()) && ((linha.contains(IdentificadorArquivoLayout.IRRF.getDescricao()) && evento.getChave().contains(IdentificadorArquivoLayout.IRRF.getDescricao()))
					|| linha.contains(IdentificadorArquivoLayout.ADICIONAL_FERIAS.getDescricao())
					|| linha.contains(IdentificadorArquivoLayout.TERCO_FERIAS.getDescricao())
					|| linha.substring(linha.length()-1, linha.length()).equals(IdentificadorArquivoLayout.PORCENTAGEM.getDescricao()))){
				eventoPagamento.setValor(Double.parseDouble(getValorEvento(linha, linha.indexOf(IdentificadorArquivoLayout.VIRGULA.getDescricao()))));
			}else if(linha.contains(IdentificadorArquivoLayout.IRRF.getDescricao()) && !evento.getChave().contains(IdentificadorArquivoLayout.IRRF.getDescricao())){
				linha = getEventoIRRF(linha, evento);
				eventoPagamento.setValor(Double.parseDouble(getValorEvento(linha, linha.lastIndexOf(IdentificadorArquivoLayout.VIRGULA.getDescricao()))));
			}else if(linha.contains(IdentificadorArquivoLayout.PENSAO_ALIMENTICIA.getDescricao())){
				String linhaTemp = retirarValorEntreParenteses(linha);
				eventoPagamento.setValor(Double.parseDouble(getValorEvento(linhaTemp, linhaTemp.indexOf(IdentificadorArquivoLayout.VIRGULA.getDescricao()))));
			}else {
				eventoPagamento.setValor(Double.parseDouble(getValorEvento(linha, linha.lastIndexOf(IdentificadorArquivoLayout.VIRGULA.getDescricao()))));
			}
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar o Evento Pagamento. Linha: " + linha);
		}
		return eventoPagamento;
	}

	private String retirarValorEntreParenteses(String linha) {
		try {
			int posicaoInicialParentese = linha.indexOf(IdentificadorArquivoLayout.PARENTESE_INICIAL.getDescricao());
			int posicaoFinalParentese = linha.indexOf(IdentificadorArquivoLayout.PARENTESE_FINAL.getDescricao());
			String valorEntreParenteses = linha.substring(posicaoInicialParentese , posicaoFinalParentese + 1);
			return linha.replace(valorEntreParenteses, "");
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar o Evento Pagamento. Linha: " + linha);
		}
	}

	private String getEventoIRRF(String linha, Evento evento) {
		EventoPagamento e = new EventoPagamento();
		e.setPagamento(pagamento);
		e.setEvento(getEvento(IdentificadorArquivoLayout.IRRF));
		String linhaTemporaria = linha.substring(linha.indexOf(IdentificadorArquivoLayout.IRRF.getDescricao())
				+ IdentificadorArquivoLayout.IRRF.getDescricao().length());
		e.setValor(Double.parseDouble(getValorEvento(linhaTemporaria, linhaTemporaria.indexOf(IdentificadorArquivoLayout.VIRGULA.getDescricao()))));
		pagamento.getEventosPagamento().add(e);
		return linha.substring(0, linha.indexOf(IdentificadorArquivoLayout.IRRF.getDescricao()) - 3);
	}

	private Evento getEvento(IdentificadorArquivoLayout irrf) {
		for(Evento e : getEventos() ){
			if(e.getChave().contains(irrf.getDescricao())){
				return e;
			}
		}
		return null;
	}

	private String getValorEvento(String linha, int posicaoVirgula) {
		try {
			String linhaInvertida = new StringBuffer(linha.substring(0, posicaoVirgula)).reverse().toString();
			int posicaoEspacoLinhaInvertida = linhaInvertida.indexOf(IdentificadorArquivoLayout.ESPACO_NA_LINHA.getDescricao()) ;
			String valor = linha.substring(posicaoVirgula - posicaoEspacoLinhaInvertida, posicaoVirgula + 3);
			return valor.replace(".","").replace(",", ".");
		} catch (Exception e) {	
			throw new ApplicationException("Erro ao pegar o Valor Evento. Linha: " + linha);
		}
	}
		

	private  void localizarMatricula(String linhaAtual) throws Exception {
		if(linhaAtual.toUpperCase().contains("WASHINGTON LUIZ GOMES")){
			System.out.println(linhaAtual);
		}
		if(linhaAtual.contains(IdentificadorArquivoLayout.CARGO.getDescricao())){
			ultimaLinha = linhaAnterior;
			if(linhaAtual.contains(IdentificadorArquivoLayout.INICIO_EVENTO.getDescricao())){
				processamentoPagamentoAtivo = true;
			}
			String numeroMatricula = getNumeroMatricula();
			if(Objects.nonNull(pagamento) && Objects.nonNull(matricula) && !matricula.getNumeroMatricula().equals(numeroMatricula)){
				pagamentos.add(pagamento);
			}
			pagamento = new Pagamento();
			novaMatricula(numeroMatricula, linhaAtual);
		}
//		if(linhaAtual.contains(Identificador.DATA_ADMISSAO.getDescricao())){
//		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
//		java.sql.Date data = new java.sql.Date(format.parse(dataStr).getTime());
//			matricula.setDataAdmissao(getDataAdmissao(linhaAtual));
//			matricula.setContaCorrente(getContaCorrente(linhaAtual));;
//		}
	}
	
	private String getNumeroMatricula() throws Exception {
		String numeroMatricula = linhaAnterior.substring(0,8);
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
		matricula.setNumeroMatricula(numeroMatricula);
		matricula.setCargo(getCargo(getCargo(linhaAtual), linhaAtual));
		matricula.setVinculo(getVinculo(getVinculo(linhaAnterior), linhaAnterior));
		matricula.setCargaHoraria(getCargaHoraria());
		matricula.setNomeFuncionario(getNomeFuncionario(linhaAnterior));;
		matricula.setObservacao(linhaAnterior);
		matriculas.add(matricula);
		pagamento.setMatricula(matricula);
	}

	private Cargo getCargo(String linhaAtual) throws ApplicationException {
		Cargo cargo = new Cargo();
		try {
			int tamanhoNomeCargo = IdentificadorArquivoLayout.CARGO.getDescricao().length();
			String palavraComNumeroCargo = linhaAtual.substring(tamanhoNomeCargo);
			String valorNumeroCargo = palavraComNumeroCargo.substring(0, palavraComNumeroCargo.indexOf(IdentificadorArquivoLayout.ESPACO_NA_LINHA.getDescricao()));
			cargo.setCidade(getArquivoPagamento().getCidade());
			cargo.setTipoArquivo(getArquivoPagamento().getTipoArquivo());
			cargo.setNumero(Integer.parseInt(valorNumeroCargo.trim())); 
			cargo.setDescricao(palavraComNumeroCargo.substring(palavraComNumeroCargo.indexOf(IdentificadorArquivoLayout.ESPACO_NA_LINHA.getDescricao())).replace("- -", "").trim());
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar o Cargo. Linha: " + linhaAtual);
		}
		return cargo;
	}

	private String getNomeFuncionario(String linha) throws ApplicationException {
		String nome= "";
		try {
			Integer posicaoInicial = getPosicaoInicialNomeFuncionario(linha);
			nome = linha.subSequence(
				posicaoInicial,
				linha.indexOf(matricula.getVinculo().getDescricao())
			).toString().trim();
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
			int posicaoCargaHoraria = linha.indexOf(IdentificadorArquivoLayout.CARGA_HORARIA.getDescricao());
			String valorNumeroVinculo = linha.substring(posicaoCargaHoraria - 3, posicaoCargaHoraria);
			vinculo.setNumero(Integer.parseInt(valorNumeroVinculo.trim())); 
			vinculo.setCidade(getArquivoPagamento().getCidade());
			String palavraComNomeVinculo = linha.substring(0,posicaoCargaHoraria - 3);
			String palavraInvertida = new StringBuffer(palavraComNomeVinculo).reverse().toString();
			String nomeVinculoInvertido = palavraInvertida.substring(0, palavraInvertida.indexOf(IdentificadorArquivoLayout.ESPACO_NA_LINHA.getDescricao()));
			vinculo.setDescricao(new StringBuffer(nomeVinculoInvertido).reverse().toString().toUpperCase().trim());
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar o Vínculo. Linha: " + linha);
		}
		return vinculo;
	}
	
	private int getCargaHoraria() throws ApplicationException {
		String cargaHoraria = "";
		int cargaHorariaNumero = 0;
		try {
			cargaHoraria = linhaAnterior.substring(
					(linhaAnterior.indexOf(IdentificadorArquivoLayout.CARGA_HORARIA.getDescricao()) + IdentificadorArquivoLayout.CARGA_HORARIA.getDescricao().length())
					,linhaAnterior.indexOf(IdentificadorArquivoLayout.VINCULO.getDescricao())
					);
			cargaHorariaNumero = Integer.parseInt(cargaHoraria.trim());
		} catch (Exception e) {
			throw new ApplicationException ("Erro ao pegar o Carga Horária. Linha: " + linhaAnterior);
		}
		return cargaHorariaNumero;
	}

	public void setPagamentoService(PagamentoService pagamentoService) {
		this.pagamentoService = pagamentoService;
	}

	public void setEventoService(EventoService eventoService) {
		this.eventoService = eventoService;
	}

}


