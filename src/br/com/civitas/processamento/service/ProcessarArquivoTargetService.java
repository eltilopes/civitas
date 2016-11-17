package br.com.civitas.processamento.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.Normalizer;
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
import br.com.civitas.processamento.entity.NivelPagamento;
import br.com.civitas.processamento.entity.Pagamento;
import br.com.civitas.processamento.entity.ResumoSetor;
import br.com.civitas.processamento.entity.Secretaria;
import br.com.civitas.processamento.entity.Setor;
import br.com.civitas.processamento.entity.UnidadeTrabalho;
import br.com.civitas.processamento.entity.Vinculo;
import br.com.civitas.processamento.enums.IdentificadorArquivoLayout;
import br.com.civitas.processamento.enums.IdentificadorArquivoTarget;
import br.com.civitas.processamento.interfac.IProcessarArquivoPagamento;

@Service
public class ProcessarArquivoTargetService extends ProcessarArquivoPagamento implements IProcessarArquivoPagamento{

	private  Pagamento pagamento;
	private  Matricula matricula;
	private  Secretaria secretaria;
	private  UnidadeTrabalho unidadeTrabalho;
	private  Setor setor;
	private  Cargo cargo;
	private  List<Pagamento> pagamentos;
	private  boolean processamentoPagamentoAtivo = false;
	private  boolean processamentoEventos = false;
	private  boolean resumoSetor = false;
	private  String linhaAnterior = "";
	private  List<String> nomesCargos;
	private String descricaoNivelPagamento;
	
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
		unidadeTrabalho = null;
		pagamentos = new ArrayList<Pagamento>();
		setMatriculas(getMatriculaService().buscarPorCidade(getArquivoPagamento().getCidade()));
		setNiveisPagamento(getNivelPagamentoService().buscarTipoArquivoCidadeAno(getArquivoPagamento().getCidade(), 
				getArquivoPagamento().getTipoArquivo(),getArquivoPagamento().getAno()));
		setCargasHorariaPagamento(getCargaHorariaPagamentoService().buscarTipoArquivoCidade(getArquivoPagamento().getCidade(), getArquivoPagamento().getTipoArquivo()));
		setUnidadesTrabalho(getUnidadeTrabalhoService().buscarTipoArquivoCidade(getArquivoPagamento().getCidade(), getArquivoPagamento().getTipoArquivo()));
		setSetores(getSetorService().buscarTipoArquivoCidade(getArquivoPagamento().getCidade(), getArquivoPagamento().getTipoArquivo()));
		setSecretarias(getSecretariaService().buscarTipoArquivoCidade(getArquivoPagamento().getCidade(), getArquivoPagamento().getTipoArquivo()));
		setCargos(getCargoService().buscarTipoArquivoCidade(getArquivoPagamento().getCidade(), getArquivoPagamento().getTipoArquivo()));
		setVinculos(getVinculoService().buscarPorCidade(getArquivoPagamento().getCidade()));
		setEventos(getEventoService().buscarTipoArquivoCidade(getArquivoPagamento().getCidade(), getArquivoPagamento().getTipoArquivo()));
		nomesCargos = getCargos().stream().map(cargo -> cargo.getDescricao()).collect(Collectors.toList());
		processamentoPagamentoAtivo = false;
		processamentoEventos = false;
		resumoSetor = false;
		linhaAnterior = "";
		descricaoNivelPagamento = "";
	}

	private void localizarEvento(String linha) {
		verificarIdentificadorEvento(linha);
		verificarIdentificadorResumoSetor(linha);
		if(processamentoEventos && !resumoSetor && !linha.contains(IdentificadorArquivoTarget.INICIO_EVENTO.getDescricao())){
			getEvento(linha, getChaveEvento(linha));
		}
	}

	private String getChaveEvento(String linha) {
		if(linha.contains(IdentificadorArquivoTarget.ANUENIO.getDescricao())){
			return IdentificadorArquivoTarget.ANUENIO.getDescricao();
		}
		try {
			int posicaoInicialIdentificador = 0 ;
			int posicaoPrimeiraVirgula = linha.lastIndexOf(IdentificadorArquivoLayout.VIRGULA.getDescricao()) ;
			String linhaInvertida = new StringBuffer(linha.substring(0, posicaoPrimeiraVirgula)).reverse().toString();
			int posicaoEspacoLinhaInvertida = linhaInvertida.indexOf(IdentificadorArquivoLayout.ESPACO_NA_LINHA.getDescricao()) ;
			posicaoEspacoLinhaInvertida = posicaoEspacoLinhaInvertida  + 1 + linhaInvertida.substring(posicaoEspacoLinhaInvertida + 1).indexOf(IdentificadorArquivoLayout.ESPACO_NA_LINHA.getDescricao()) ;
			return linha.substring(posicaoInicialIdentificador, posicaoPrimeiraVirgula - posicaoEspacoLinhaInvertida).trim();
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar o Chave Evento. Linha: " + linha);
		}
	}
	
	private void verificarIdentificadorEvento(String linha) {
		if(linhaAnterior.contains(IdentificadorArquivoTarget.INICIO_EVENTO.getDescricao()) && 
				(linha.contains(IdentificadorArquivoTarget.SALARIO_BASE.getDescricao()) || 
				 linha.contains(IdentificadorArquivoTarget.VENCIMENTO_BASE.getDescricao())		)){
			processamentoEventos = true;
		}
		if(processamentoEventos && (linha.contains(IdentificadorArquivoTarget.PIS_PASEP.getDescricao()) || linha.contains(IdentificadorArquivoTarget.FIM_EVENTO.getDescricao()) || fimPagina(linha)) ){
			processamentoEventos = false;
		}
	}

	private boolean fimPagina(String linha) {
		return linha.toUpperCase().contains(removerAcentos(getArquivoPagamento().getCidade().getDescricao().toUpperCase()));
	}
	
	public String removerAcentos(String str) {
		return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
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
		localizarSecretariaSetor(linhaAtual);
		localizarUnidadeTrabalho(linhaAtual);
		localizarMatricula(linhaAtual);
		localizarNivelPagamento(linhaAtual);
		localizarDataAdmissao(linhaAtual); 
		localizarCargaHoraria(linhaAtual); 
		localizarVinculo(linhaAtual); 
		localizarEventosPagamento(linhaAtual);
		finalizarPagamento(linhaAtual);
	}
	
	private void localizarNivelPagamento(String linhaAtual) {
		if(linhaAtual.contains(IdentificadorArquivoTarget.NIVEL_PAGAMENTO.getDescricao()) 	){
			matricula.getMatriculaPagamento().setNivelPagamento(getNivelPagamento(getNivelPagamento(linhaAtual), linhaAtual));
		}
	}

	private NivelPagamento getNivelPagamento(String linhaAtual) {
		NivelPagamento nivelPagamento = new NivelPagamento();
		try {
			descricaoNivelPagamento = linhaAtual.substring(linhaAtual.indexOf(IdentificadorArquivoTarget.NIVEL_PAGAMENTO.getDescricao()) 
					+ IdentificadorArquivoTarget.NIVEL_PAGAMENTO.getDescricao().length(),linhaAtual.length()).trim() ;
			if(descricaoNivelPagamento.toUpperCase().contains(IdentificadorArquivoTarget.CARGO.getDescricao().toUpperCase())){
				descricaoNivelPagamento = descricaoNivelPagamento.substring(0,descricaoNivelPagamento.indexOf(IdentificadorArquivoTarget.CARGO.getDescricao()) ) + " " + 
							descricaoNivelPagamento.substring(descricaoNivelPagamento.indexOf(IdentificadorArquivoTarget.CARGO.getDescricao()) , descricaoNivelPagamento.length()).trim(); 
			}
			descricaoNivelPagamento = descricaoNivelPagamento
					.replace(IdentificadorArquivoTarget.CARGO.getDescricao(), "")
					.replace(IdentificadorArquivoTarget.VIRGULA.getDescricao(), "")
					.trim();
			descricaoNivelPagamento = descricaoNivelPagamento.equals(IdentificadorArquivoTarget.HIFEN.getDescricao()) ? "" : descricaoNivelPagamento;
			descricaoNivelPagamento = descricaoNivelPagamento.substring(Util.posicaoPrimeiraLetra(descricaoNivelPagamento), descricaoNivelPagamento.length()).trim();
			descricaoNivelPagamento = descricaoNivelPagamento.indexOf(IdentificadorArquivoTarget.HIFEN.getDescricao())==0 ? 
					descricaoNivelPagamento.substring(1, descricaoNivelPagamento.length()).trim() : descricaoNivelPagamento;
			if(StringUtils.notNullOrEmpty(descricaoNivelPagamento)){
				List<String> resultado = nomesCargos.stream()
						  .filter(nomeCargo -> descricaoNivelPagamento.contains(nomeCargo))
						  .collect(Collectors.toList()); 
				resultado.stream().forEach(r -> descricaoNivelPagamento = descricaoNivelPagamento.replace(r, ""));
			}
			nivelPagamento.setCidade(getArquivoPagamento().getCidade());
			nivelPagamento.setTipoArquivo(getArquivoPagamento().getTipoArquivo());
			nivelPagamento.setDescricao(descricaoNivelPagamento.trim()); 
			nivelPagamento.setAno(getArquivoPagamento().getAno());
			nivelPagamento.setSecretaria(secretaria);
			nivelPagamento.setCodigo(StringUtils.notNullOrEmpty(descricaoNivelPagamento)? Util.getNumeroHashCode(descricaoNivelPagamento, nivelPagamento.getTamanhoMinimoCodigo()) + "" : ""); 
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar o Nível Pagamento. Linha: " + linhaAtual);
		}
		return nivelPagamento;
	}

	private void finalizarPagamento(String linhaAtual) {
		if(linhaAtual.contains(IdentificadorArquivoTarget.TOTAIS.getDescricao())){
			if( !processamentoPagamentoAtivo  && !processamentoEventos && isTotalPagamento(linhaAtual)){
				setTotaisPagamento(linhaAtual);
			}
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
			pagamento.setTotalLiquido(Double.parseDouble(valores[2].replace(IdentificadorArquivoTarget.CPF.getDescricao(), "").replace(".","").replace(",", ".").trim()));
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
		if(linhaAtual.contains(IdentificadorArquivoTarget.VENCIMENTO_BASE.getDescricao())){
			System.out.println(linhaAtual);
		}
		verificarIdentificadorEvento(linhaAtual);
		verificarIdentificadorResumoSetor(linhaAtual);
		if(processamentoEventos && processamentoPagamentoAtivo && !resumoSetor && !linhaAtual.contains(IdentificadorArquivoTarget.INICIO_EVENTO.getDescricao())){
			localizarDiasTrabalhados(linhaAtual);
			for(Evento evento : getEventos()){
				if(getChaveEvento(linhaAtual.toUpperCase()).equals(evento.getNome().toUpperCase())){
						pagamento.getEventosPagamento().add(getEventoPagamento(linhaAtual, evento));
				}
			}		
		}	
	}

	private void localizarDiasTrabalhados(String linhaAtual) {
		if(linhaAtual.contains(IdentificadorArquivoTarget.SALARIO_BASE.getDescricao())){
			getDiasTrabalhados(linhaAtual,IdentificadorArquivoTarget.SALARIO_BASE.getDescricao() );
		}else if(linhaAtual.contains(IdentificadorArquivoTarget.VENCIMENTO_BASE_LEI.getDescricao())){
			getDiasTrabalhados(linhaAtual, IdentificadorArquivoTarget.VENCIMENTO_BASE_LEI.getDescricao());
		}else if(linhaAtual.contains(IdentificadorArquivoTarget.VENCIMENTO_BASE.getDescricao())){
			getDiasTrabalhados(linhaAtual, IdentificadorArquivoTarget.VENCIMENTO_BASE.getDescricao());
		}else if(linhaAtual.contains(IdentificadorArquivoTarget.LICENCA_MATERNIDADE.getDescricao())){
			getDiasTrabalhados(linhaAtual, IdentificadorArquivoTarget.LICENCA_MATERNIDADE.getDescricao());
		}
	}

	private void getDiasTrabalhados(String linhaAtual, String descricaoIdentificador) {
		String dias = linhaAtual.substring(
				linhaAtual.indexOf(descricaoIdentificador) 
				+ descricaoIdentificador.length()).trim();
		dias = dias.substring(0, dias.indexOf(IdentificadorArquivoTarget.ESPACO_NA_LINHA.getDescricao())).trim();
		pagamento.setDiasTrabalhados(Util.valorContemNumero(dias) ? dias : pagamento.getDiasTrabalhados());
	}

	private void localizarSecretariaSetor(String linhaAtual) {
		if(linhaAtual.contains(IdentificadorArquivoTarget.ORGAO.getDescricao()) 	){
			secretaria = getSecretaria(getSecretaria(linhaAtual), linhaAtual);
			setor = getSetor(getSetor(), linhaAnterior);
		}
	}

	private void localizarUnidadeTrabalho(String linhaAtual) {
		if(linhaAtual.contains(IdentificadorArquivoTarget.LOTACAO.getDescricao()) 	){
			unidadeTrabalho = getUnidadeTrabalho(getUnidadeTrabalho(linhaAtual), linhaAtual);
		}
	}

	private UnidadeTrabalho getUnidadeTrabalho(String linha) throws ApplicationException {
		UnidadeTrabalho unidadeTrabalho = new UnidadeTrabalho();
		try {
			String unidadeTrabalhoString = linha.substring(linha.indexOf(IdentificadorArquivoTarget.LOTACAO.getDescricao()) + IdentificadorArquivoTarget.LOTACAO.getDescricao().length(), linha.length()).trim();
			String codigo = unidadeTrabalhoString.substring(0, unidadeTrabalhoString.indexOf(IdentificadorArquivoTarget.HIFEN.getDescricao()));
			unidadeTrabalhoString = unidadeTrabalhoString.replace(IdentificadorArquivoTarget.HIFEN.getDescricao(), "").length() < 2 ? 
					"" :  unidadeTrabalhoString.substring(unidadeTrabalhoString.indexOf(IdentificadorArquivoTarget.HIFEN.getDescricao()) 
							+ IdentificadorArquivoTarget.HIFEN.getDescricao().length(), 
							unidadeTrabalhoString.length()).trim();
			if(StringUtils.notNullOrEmpty(unidadeTrabalhoString)){
				unidadeTrabalho.setCidade(getArquivoPagamento().getCidade());
				unidadeTrabalho.setTipoArquivo(getArquivoPagamento().getTipoArquivo());
				unidadeTrabalho.setDescricao(unidadeTrabalhoString); 
				unidadeTrabalho.setCodigo(Integer.parseInt(codigo)); 
			}else {
				unidadeTrabalho = null;
			}
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar a Unidade Trabalho. Linha: " + linha);
		}
		return unidadeTrabalho;
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
	
	private Setor getSetor() throws ApplicationException {
		Setor setor = new Setor();
		try {
			String descricao = new StringBuffer(linhaAnterior.trim()).reverse().toString();
			setor.setCidade(getArquivoPagamento().getCidade());
			setor.setTipoArquivo(getArquivoPagamento().getTipoArquivo());
			setor.setDescricao(new StringBuffer(descricao.substring(Util.posicaoPrimeiraLetra(descricao) + 1, descricao.length())).reverse().toString());
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar o Setor. Linha: " + linhaAnterior);
		}
		return setor;
	}
	
	private  void verificarIdentificadorPagamento(String linha) {
		if(localizarCargo(linha)){
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
		if(processamentoPagamentoAtivo  &&	localizarCargo(linhaAtual)	){
			cargo = getCargo();
			String numeroMatricula = getNumeroMatricula();
			verificarPagamento(numeroMatricula);
			getMatricula(numeroMatricula, linhaAtual);
		}
	}

	private void localizarDataAdmissao(String linhaAtual) {
		if(processamentoPagamentoAtivo  &&	linhaAtual.contains(IdentificadorArquivoTarget.ADMISSAO.getDescricao())	){
			matricula.setDataAdmissao(getDataAdmissao(linhaAtual));
		}
	}

	private void localizarVinculo(String linhaAtual) {
		if(processamentoPagamentoAtivo  &&	linhaAtual.contains(IdentificadorArquivoTarget.VINCULO.getDescricao())	){
			matricula.getMatriculaPagamento().setVinculo(getVinculo(getVinculo(linhaAtual), linhaAtual));}
	}
	
	private void localizarCargaHoraria(String linhaAtual) {
		if(processamentoPagamentoAtivo  &&	linhaAtual.contains(IdentificadorArquivoTarget.CARGA_HORARIA.getDescricao())	){
			matricula.getMatriculaPagamento().setCargaHoraria(getCargaHoraria(linhaAtual));	
		}
	}

	private boolean localizarCargo(String linha ) {
		return ((linha.contains(IdentificadorArquivoTarget.VINCULO.getDescricao())
				|| linha.contains(IdentificadorArquivoTarget.LOTACAO.getDescricao())
				|| linha.contains(IdentificadorArquivoTarget.ADMISSAO.getDescricao()))

				&& !linhaAnterior.contains(IdentificadorArquivoTarget.TOTAL_ORCAMENTARIO.getDescricao())
				&& !linhaAnterior.contains(IdentificadorArquivoTarget.EMISSAO.getDescricao())
				&& !linhaAnterior.contains(IdentificadorArquivoTarget.TIPO.getDescricao())
				&& !linhaAnterior.contains(IdentificadorArquivoTarget.FUNDO_RESERVA.getDescricao())
				&& !linhaAnterior.contains(IdentificadorArquivoTarget.PAGAMENTO_BANCO.getDescricao()));
	}
	
	private int getCargaHoraria(String linhaAtual) throws ApplicationException {
		int cargaHorariaNumero = 0;
		try {
			String cargaHoraria = linhaAtual.substring(0,linhaAtual.indexOf(IdentificadorArquivoTarget.CARGA_HORARIA.getDescricao())).trim();
			cargaHoraria = cargaHoraria.substring(cargaHoraria.lastIndexOf(IdentificadorArquivoTarget.ESPACO_NA_LINHA.getDescricao(), cargaHoraria.length())).trim();
			cargaHorariaNumero = Integer.parseInt(cargaHoraria);
		} catch (Exception e) {
			throw new ApplicationException ("Erro ao pegar o Carga Horária. Linha: " + linhaAtual);
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
		String numeroMatricula = linhaAnterior.substring(linhaAnterior.indexOf(cargo.getDescricao()) + cargo.getDescricao().length(), linhaAnterior.length()).trim();
		Integer numero = 0;
		try {
			numero = Integer.parseInt(numeroMatricula.replace("-", ""));
		} catch (Exception e) {
			try {
				numero =  Util.getNumeroHashCode(numeroMatricula, 8);
				numeroMatricula = numero.toString();
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
	
	private  void novaMatricula(String numeroMatricula, String linhaAtual) throws ApplicationException {
		matricula = new Matricula();
		matricula.setNumeroMatricula(numeroMatricula);
		matricula.setNomeFuncionario(getNomeFuncionario());
	}

	private void setMatriculaPagamento(String linhaAtual) {
		matricula.setMatriculaPagamento(new MatriculaPagamento());
		matricula.getMatriculaPagamento().setSecretaria(secretaria);
		matricula.getMatriculaPagamento().setUnidadeTrabalho(unidadeTrabalho);
		matricula.getMatriculaPagamento().setSetor(setor);
		matricula.getMatriculaPagamento().setCargo(cargo);
		matricula.getMatriculaPagamento().setAno(getArquivoPagamento().getAno());
		matricula.getMatriculaPagamento().setMes(getArquivoPagamento().getMes());
		matricula.getMatriculaPagamento().setMatricula(matricula);
		getMatriculaPagamentoService().salvar(matricula.getMatriculaPagamento());
	}
	
	private Cargo getCargo() {
		for(Cargo cargo : getCargos()){
			if(linhaAnterior.contains(cargo.getDescricao())){
				return cargo;
			}
		}
		return null;
	}

	private String getNomeFuncionario() throws ApplicationException {
		return linhaAnterior.substring(0,linhaAnterior.indexOf(cargo.getDescricao())).trim();
	}

	private Vinculo getVinculo(String linha) throws ApplicationException {
		Vinculo vinculo = new Vinculo();
		try {
			String descricao = linha.substring(10, linha.indexOf(IdentificadorArquivoTarget.ADMISSAO.getDescricao()) ).trim();
			descricao = descricao.replace(matricula.getMatriculaPagamento().getCargaHoraria() + "", "").replace(IdentificadorArquivoTarget.CARGA_HORARIA.getDescricao(), "").trim();
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


