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
import br.com.civitas.processamento.interfac.IProcessarArquivoPagamento;

@Service
public class ProcessarArquivoLayoutService extends ProcessarArquivoPagamento implements IProcessarArquivoPagamento{

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
	private  Setor setor;
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
		nivelPagamento = null;
		secretaria = null;
		setor = null;
		setNiveisPagamento(nivelPagamentoService.buscarTipoArquivoCidadeAno(getArquivoPagamento().getCidade(), 
				getArquivoPagamento().getTipoArquivo(),getArquivoPagamento().getAno()));
		setCargasHorariaPagamento(cargaHorariaPagamentoService.buscarTipoArquivoCidade(getArquivoPagamento().getCidade(), getArquivoPagamento().getTipoArquivo()));
		setUnidadesTrabalho(unidadeTrabalhoService.buscarTipoArquivoCidade(getArquivoPagamento().getCidade(), getArquivoPagamento().getTipoArquivo()));
		setSetores(setorService.buscarTipoArquivoCidade(getArquivoPagamento().getCidade(), getArquivoPagamento().getTipoArquivo()));
		setSecretarias(secretariaService.buscarTipoArquivoCidade(getArquivoPagamento().getCidade(), getArquivoPagamento().getTipoArquivo()));
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

	private void verificarIdentificadorPagamento(String linha) {
		if(linha.contains(IdentificadorArquivoLayout.INICIO_EVENTO.getDescricao())){
			processamentoPagamentoAtivo = true;
		}
		if((linha.contains(IdentificadorArquivoLayout.FIM_EVENTO.getDescricao()))){
			processamentoPagamentoAtivo = false;
		}
	}

	private void localizarPagamentos(String linhaAtual) throws Exception {
		localizarSecretariaSetor(linhaAtual);
		localizarMatricula(linhaAtual);
		localizarNivelGrupoPagamento(linhaAtual);
		localizarUnidadeTrabalho(linhaAtual);
		verificarIdentificador(linhaAtual, getArquivoPagamento().getNomeArquivo());
	}
	
	private void localizarNivelGrupoPagamento(String linhaAtual) {
		if(linhaAtual.contains(IdentificadorArquivoLayout.NIVEL.getDescricao()) 
				&& !linhaAtual.contains(IdentificadorArquivoLayout.NOME_CARGO.getDescricao())){
			nivelPagamento = getNivelPagamento(getNivelPagamento(linhaAtual), linhaAtual);
			matricula.setNivelPagamento(nivelPagamento);
			matricula.setCargaHorariaPagamento(getCargaHorariaPagamento(getCargaHorariaPagamento(linhaAtual), linhaAtual));
		}
	}

	private void localizarUnidadeTrabalho(String linhaAtual) {
		if(linhaAtual.contains(IdentificadorArquivoLayout.UNIDADE_TRABALHO.getDescricao())){
			matricula.setUnidadeTrabalho(getUnidadeTrabalho(getUnidadeTrabalho(linhaAtual), linhaAtual));
		}
	}

	private void localizarSecretariaSetor(String linhaAtual) {
		if(linhaAtual.contains(IdentificadorArquivoLayout.PAGINA.getDescricao())){
			secretaria = getSecretaria(getSecretaria(linhaAtual), linhaAtual);
			setor = getSetor(getSetor(linhaAtual), linhaAtual);
		}else if(linhaAnterior.contains(IdentificadorArquivoLayout.AGRUPAMENTO_GERAL.getDescricao())){
			secretaria = getSecretaria(getSecretaria(linhaAnterior), linhaAnterior);
		}
	}

	private CargaHorariaPagamento getCargaHorariaPagamento(String linhaAtual) throws ApplicationException {
		CargaHorariaPagamento cargaHorariaPagamento = new CargaHorariaPagamento();
		try {
			String codigo = linhaAtual.substring(
					0,linhaAtual.indexOf(IdentificadorArquivoLayout.HIFEN.getDescricao())).trim();
			String descricao = linhaAtual.substring(linhaAtual.indexOf(IdentificadorArquivoLayout.HIFEN.getDescricao()) + IdentificadorArquivoLayout.HIFEN.getDescricao().length(), 
					linhaAtual.indexOf(nivelPagamento.getCodigo())).trim();
			cargaHorariaPagamento.setCidade(getArquivoPagamento().getCidade());
			cargaHorariaPagamento.setTipoArquivo(getArquivoPagamento().getTipoArquivo());
			cargaHorariaPagamento.setDescricao(descricao); 
			cargaHorariaPagamento.setCodigo(codigo); 
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar a Nivel Pagamento. Linha: " + linhaAtual);
		}
		return cargaHorariaPagamento;
	}
	
	private NivelPagamento getNivelPagamento(String linhaAtual) throws ApplicationException {
		NivelPagamento nivelPagamento = new NivelPagamento();
		try {
			String codigo = linhaAtual.substring(
					linhaAtual.lastIndexOf(IdentificadorArquivoLayout.HIFEN.getDescricao()) - 4, 
					linhaAtual.lastIndexOf(IdentificadorArquivoLayout.HIFEN.getDescricao())).trim();
			String descricao = linhaAtual.substring(linhaAtual.lastIndexOf(IdentificadorArquivoLayout.HIFEN.getDescricao()) + IdentificadorArquivoLayout.HIFEN.getDescricao().length(), 
					linhaAtual.length()).trim();
			nivelPagamento.setCidade(getArquivoPagamento().getCidade());
			nivelPagamento.setTipoArquivo(getArquivoPagamento().getTipoArquivo());
			nivelPagamento.setDescricao(descricao); 
			nivelPagamento.setAno(getArquivoPagamento().getAno());
			nivelPagamento.setSecretaria(secretaria);
			nivelPagamento.setCodigo(codigo); 
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar a Nivel Pagamento. Linha: " + linhaAtual);
		}
		return nivelPagamento;
	}

	private UnidadeTrabalho getUnidadeTrabalho(String linhaAtual) throws ApplicationException {
		UnidadeTrabalho unidadeTrabalho = new UnidadeTrabalho();
		try {
			String codigo = linhaAtual.substring(linhaAtual.indexOf(IdentificadorArquivoLayout.ESPACO_NA_LINHA.getDescricao()));
			codigo = codigo.trim();
			codigo = codigo.substring(
					0,	codigo.indexOf(IdentificadorArquivoLayout.ESPACO_NA_LINHA.getDescricao())).trim();
			String descricaoUnidadeTrabalho = linhaAtual.substring(linhaAtual.indexOf(codigo) + codigo.length(), 
					linhaAtual.indexOf(IdentificadorArquivoLayout.UNIDADE_TRABALHO_2.getDescricao())).trim();
			Integer inicioCodigoUnidadeDois = temInteiro(descricaoUnidadeTrabalho);
			if(Objects.nonNull(inicioCodigoUnidadeDois)){
				descricaoUnidadeTrabalho = descricaoUnidadeTrabalho.substring(0,inicioCodigoUnidadeDois).trim();
			}
			unidadeTrabalho.setCidade(getArquivoPagamento().getCidade());
			unidadeTrabalho.setTipoArquivo(getArquivoPagamento().getTipoArquivo());
			unidadeTrabalho.setDescricao(descricaoUnidadeTrabalho); 
			unidadeTrabalho.setCodigo(Integer.parseInt(codigo)); 
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar a Unidade Trabalho. Linha: " + linhaAtual);
		}
		return unidadeTrabalho;
	}
	
	public Integer temInteiro( String descricao ) {
	    char[] c = descricao.toCharArray();
	    for ( int i = 0; i < c.length; i++ ){
	        if ( Character.isDigit( c[ i ] ) ) {
	            return i;
	        }
	    }
	    return null;
	}

	private Secretaria getSecretaria(String linhaAtual) throws ApplicationException {
		Secretaria secretaria = new Secretaria();
		try {
			String descricao = "";
			if(linhaAnterior.contains(IdentificadorArquivoLayout.AGRUPAMENTO_SETOR.getDescricao())){
				descricao = linhaAtual.substring(
						linhaAtual.indexOf(IdentificadorArquivoLayout.ESPACO_NA_LINHA.getDescricao()),
						linhaAtual.indexOf(IdentificadorArquivoLayout.PAGINA.getDescricao()));
				descricao = descricao.substring(
						0,	descricao.lastIndexOf(IdentificadorArquivoLayout.ESPACO_NA_LINHA.getDescricao())).trim();
			}else if(linhaAnterior.contains(IdentificadorArquivoLayout.AGRUPAMENTO_GERAL.getDescricao())){
				descricao = linhaAtual.substring(
						linhaAtual.indexOf(IdentificadorArquivoLayout.AGRUPAMENTO_GERAL.getDescricao()) + IdentificadorArquivoLayout.AGRUPAMENTO_GERAL.getDescricao().length(),
						linhaAtual.length()); 
				descricao = descricao.substring(
						descricao.indexOf(IdentificadorArquivoLayout.HIFEN.getDescricao()) + 1,
						descricao.length()).trim();
			}
			
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
					linhaAtual.indexOf(IdentificadorArquivoLayout.PAGINA.getDescricao()));
			descricao = descricao.substring(
					descricao.indexOf(IdentificadorArquivoLayout.ESPACO_NA_LINHA.getDescricao()),
					descricao.length() - 3).trim();
			setor.setCidade(getArquivoPagamento().getCidade());
			setor.setTipoArquivo(getArquivoPagamento().getTipoArquivo());
			setor.setDescricao(descricao);
			
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar o Setor. Linha: " + linhaAtual);
		}
		return setor;
	}

	private  void verificarIdentificador(String linha, String nomeArquivo) {
		getDiasTrabalhados(linha);
		verificarIdentificadorPagamento(linha);
		for(Evento e : getEventos()){
			if(processamentoPagamentoAtivo && !linha.contains(IdentificadorArquivoLayout.CARGO.getDescricao())
					&& !linha.contains(IdentificadorArquivoLayout.INICIO_EVENTO.getDescricao())
					&& getChaveEvento(linha).contains(e.getChave())){
				pagamento.getEventosPagamento().add(getEventoPagamento(linha, e));
			}
		}
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

	private void getDiasTrabalhados(String linhaAtual) {
		if(linhaAtual.contains(IdentificadorArquivoLayout.SALARIO_BASE.getDescricao())){
			String dias = linhaAtual.substring(
					linhaAtual.indexOf(IdentificadorArquivoLayout.VIRGULA.getDescricao()) + 3).trim();
			dias = dias.substring(0, dias.indexOf("d") + 1).trim();
			pagamento.setDiasTrabalhados(dias);
		}
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
		if(linhaAtual.contains(IdentificadorArquivoLayout.DATA_ADMISSAO.getDescricao())){
			matricula.setDataAdmissao(getDataAdmissao(linhaAtual));
		}
	}
	
	private Date getDataAdmissao(String linhaAtual) {
		try {
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			return (Date)formatter.parse(linhaAtual.substring(
					linhaAtual.indexOf(IdentificadorArquivoLayout.DATA_ADMISSAO.getDescricao()) -10
					,linhaAtual.indexOf(IdentificadorArquivoLayout.DATA_ADMISSAO.getDescricao())));
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar a Data de Admissão. Linha: " + linhaAtual);
		}
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
		matricula.setSecretaria(secretaria);
		matricula.setSetor(setor);
		matricula.setNumeroMatricula(numeroMatricula);
		matricula.setCargo(getCargo(getCargo(linhaAtual), linhaAtual));
		matricula.setVinculo(getVinculo(getVinculo(linhaAnterior), linhaAnterior));
		matricula.setCargaHoraria(getCargaHoraria());
		matricula.setNomeFuncionario(getNomeFuncionario(linhaAnterior));;
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


