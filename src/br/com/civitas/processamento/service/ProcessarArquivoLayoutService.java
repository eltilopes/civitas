package br.com.civitas.processamento.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	private  List<Cargo> cargos;
	private  List<Vinculo> vinculos;
	private  boolean processamentoPagamentoAtivo = false;
	private  boolean processamentoEventos = false;
	private  String ultimaLinha = "";
	private  String linhaAnterior = "";
	
	public void processar(ArquivoPagamento arquivo) throws IOException{
		setArquivo(arquivo);
		processar();
	}
	
	private void processar() throws IOException {
		iniciarArquivos();
		iniciarValores();
		carregarEventos();
		carregarPagamentos();
		finalizarArquivos();
	}

	private void carregarPagamentos() throws IOException {
		BufferedReader br = new BufferedReader(getArquivoPagamento());
		while (br.ready()) {
			String linha = br.readLine(); 
			localizarPagamentos(linha, getArquivo());
			linhaAnterior = linha;
		}
		pagamento.getMatricula().setVinculo(getVinculo(ultimaLinha, getArquivo()));
		pagamentos.add(pagamento);
		br.close();
		pagamentoService.inserirPagamentos(pagamentos,getEventos(), getArquivo());
	}

	private void carregarEventos() throws IOException{
		BufferedReader brEvento = new BufferedReader(getArquivoEvento());
		while (brEvento.ready()) {
			String linha = brEvento.readLine(); 
			localizarEvento(linha, getArquivo());
		}
		brEvento.close();
	}
	

	private void iniciarValores( ) {
		pagamento = null;
		matricula = null;
		pagamentos = new ArrayList<Pagamento>();
		matriculas = new ArrayList<Matricula>();
		cargos = cargoService.buscarTipoArquivoCidade(getArquivo().getCidade(), getArquivo().getTipoArquivo());
		vinculos = vinculoService.buscarPorCidade(getArquivo().getCidade());
		setEventos(eventoService.buscarTipoArquivoCidade(getArquivo().getCidade(), getArquivo().getTipoArquivo()));
		processamentoPagamentoAtivo = false;
		processamentoEventos = false;
		ultimaLinha = "";
		linhaAnterior = "";
	}

	private void localizarEvento(String linha, ArquivoPagamento arquivo) {
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
			System.out.println(linha);
		}
		return "";
	}
	
	private void verificarIdentificadorEvento(String linha) {
		if(linha.contains(IdentificadorArquivoLayout.INICIO_EVENTO.getDescricao())){
			processamentoEventos = true;
		}
		if((linha.contains(IdentificadorArquivoLayout.FIM_EVENTO.getDescricao()))){
			processamentoEventos = false;
		}
	}

	private void localizarPagamentos(String linhaAtual, ArquivoPagamento arquivo) {
		localizarMatricula(linhaAtual, arquivo);
		verificarIdentificador(linhaAtual, arquivo.getNomeArquivo());
	}
	
	private  void verificarIdentificador(String linha, String nomeArquivo) {
		
		//TODO tirar os valores do codigo e colocar no banco amarrado a um tipo de arquivo os valores tipo PEB
		getEventos().forEach((e) -> {
			if(processamentoPagamentoAtivo && !linha.contains(IdentificadorArquivoLayout.CARGO.getDescricao())
					&& !linha.contains(IdentificadorArquivoLayout.DATA_ADMISSAO.getDescricao())
					&& !linha.contains(IdentificadorArquivoLayout.UNIDADE_TRABALHO.getDescricao())
					&& !linha.contains(IdentificadorArquivoLayout.INICIO_EVENTO.getDescricao())
					&& !linha.contains(IdentificadorArquivoLayout.DATA_INICIO_FIM.getDescricao())
					&& !linha.contains(IdentificadorArquivoLayout.PEB_I.getDescricao())
					&& !linha.contains(IdentificadorArquivoLayout.AREA_ATUACAO.getDescricao())
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
					|| linha.substring(linha.length()-1, linha.length()).equals(IdentificadorArquivoLayout.PORCENTAGEM.getDescricao()))){
				eventoPagamento.setValor(Double.parseDouble(getValorEvento(linha, linha.indexOf(IdentificadorArquivoLayout.VIRGULA.getDescricao()))));
			}else if(linha.contains(IdentificadorArquivoLayout.IRRF.getDescricao()) && !evento.getChave().contains(IdentificadorArquivoLayout.IRRF.getDescricao())){
				linha = getEventoIRRF(linha, evento);
				eventoPagamento.setValor(Double.parseDouble(getValorEvento(linha, linha.lastIndexOf(IdentificadorArquivoLayout.VIRGULA.getDescricao()))));
			}else if(linha.contains(IdentificadorArquivoLayout.PENSAO_ALIMENTICIA.getDescricao())){
				String linhaTemp = linha.replace("(16,50% LIQ)", "").replace("16,5%", "");
				eventoPagamento.setValor(Double.parseDouble(getValorEvento(linhaTemp, linhaTemp.lastIndexOf(IdentificadorArquivoLayout.VIRGULA.getDescricao()))));
			}else {
				eventoPagamento.setValor(Double.parseDouble(getValorEvento(linha, linha.lastIndexOf(IdentificadorArquivoLayout.VIRGULA.getDescricao()))));
			}
		} catch (Exception e) {
			System.out.println(linha);
		}
		return eventoPagamento;
	}
//
//	private Integer getPosicaoNumero(String letra) {
//		for (int i = 0; i < letra.length(); i++) {
//			if (Character.isDigit(letra.charAt(i)) == true) {
//				return i;
//			}
//		}
//		return null;
//	}
//
//	private Integer getPosicaoLetra(String letra) {
//		for (int i = 0; i < letra.length(); i++) {
//			if (Character.isLetter(letra.charAt(i)) == true) {
//				return i;
//			}
//		}
//		return null;
//	}

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
			System.out.println("erro ao pegar valor : " + linha);
		}
		return "0.0";
	}
		

	private  void localizarMatricula(String linhaAtual, ArquivoPagamento arquivo) {
		if(linhaAtual.contains(IdentificadorArquivoLayout.CARGO.getDescricao())){
			ultimaLinha = linhaAnterior;
			processamentoPagamentoAtivo = true;
			String numeroMatricula = linhaAnterior.substring(0,8);
			if(Objects.nonNull(pagamento) && Objects.nonNull(matricula) && !matricula.getNumeroMatricula().equals(numeroMatricula)){
				pagamentos.add(pagamento);
			}
			pagamento = new Pagamento();
			novaMatricula(numeroMatricula, linhaAtual, arquivo);
		}
//		if(linhaAtual.contains(Identificador.DATA_ADMISSAO.getDescricao())){
//		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
//		java.sql.Date data = new java.sql.Date(format.parse(dataStr).getTime());
//			matricula.setDataAdmissao(getDataAdmissao(linhaAtual));
//			matricula.setContaCorrente(getContaCorrente(linhaAtual));;
//		}
	}
	
	private  void novaMatricula(String numeroMatricula, String linhaAtual, ArquivoPagamento arquivo) {
		matricula = new Matricula();
		matricula.setNumeroMatricula(numeroMatricula);
		matricula.setCargo(getCargo(linhaAtual, arquivo));
		matricula.setVinculo(getVinculo(linhaAnterior, arquivo));
		matricula.setCargaHoraria(getCargaHoraria());
		matricula.setNomeFuncionario(getNomeFuncionario(linhaAnterior));;
		matricula.setObservacao(linhaAnterior);
		matriculas.add(matricula);
		pagamento.setMatricula(matricula);
	}

	private String getNomeFuncionario(String linha) {
		String nome= "";
		try {
			nome = linha.subSequence(
					linha.indexOf(matricula.getNumeroMatricula()) + matricula.getNumeroMatricula().length(),
					linha.indexOf(matricula.getVinculo().getDescricao())).toString().trim();
		} catch (Exception e) {
			System.out.println(linha);
		}
		return nome ;
		
	}

	private Vinculo getVinculo(String linha, ArquivoPagamento arquivo) {
		Vinculo vinculo = new Vinculo();
		Vinculo vinculoAuxiliar = new Vinculo();
		try {
			int posicaoCargaHoraria = linha.indexOf(IdentificadorArquivoLayout.CARGA_HORARIA.getDescricao());
			String valorNumeroVinculo = linha.substring(posicaoCargaHoraria - 3, posicaoCargaHoraria);
			vinculo.setNumero(Integer.parseInt(valorNumeroVinculo.trim())); 
			vinculo.setCidade(arquivo.getCidade());
			vinculoAuxiliar = getVinculo(vinculo);
			if(Objects.isNull(vinculoAuxiliar)){
				String palavraComNomeVinculo = linha.substring(0,posicaoCargaHoraria - 3);
				String palavraInvertida = new StringBuffer(palavraComNomeVinculo).reverse().toString();
				String nomeVinculoInvertido = palavraInvertida.substring(0, palavraInvertida.indexOf(IdentificadorArquivoLayout.ESPACO_NA_LINHA.getDescricao()));
				vinculo.setDescricao(new StringBuffer(nomeVinculoInvertido).reverse().toString().toUpperCase().trim());
				vinculo = vinculoService.save(vinculo);
				vinculos.add(vinculo);
			}else{
				vinculo = vinculoAuxiliar;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return vinculo;
	}

	private Vinculo getVinculo(Vinculo vinculo) {
		for(Vinculo v :vinculos){
			if(v.getCidade().getId().equals(vinculo.getCidade().getId()) && v.getNumero().equals(vinculo.getNumero())){
				vinculo = v;
				return vinculo;
			}
		}
		return null;
	}

	private int getCargaHoraria() {
		String cargaHoraria = linhaAnterior.substring(
				(linhaAnterior.indexOf(IdentificadorArquivoLayout.CARGA_HORARIA.getDescricao()) + IdentificadorArquivoLayout.CARGA_HORARIA.getDescricao().length())
				,linhaAnterior.indexOf(IdentificadorArquivoLayout.VINCULO.getDescricao())
				);
		return Integer.parseInt(cargaHoraria.trim());
	}

	private Cargo getCargo(String linhaAtual, ArquivoPagamento arquivo) {
		Cargo cargo = new Cargo();
		Cargo cargoAuxiliar = new Cargo();
		try {
			int tamanhoNomeCargo = IdentificadorArquivoLayout.CARGO.getDescricao().length();
			String palavraComNumeroCargo = linhaAtual.substring(tamanhoNomeCargo);
			String valorNumeroCargo = palavraComNumeroCargo.substring(0, palavraComNumeroCargo.indexOf(IdentificadorArquivoLayout.ESPACO_NA_LINHA.getDescricao()));
			cargo.setCidade(arquivo.getCidade());
			cargo.setTipoArquivo(arquivo.getTipoArquivo());
			cargo.setNumero(Integer.parseInt(valorNumeroCargo.trim())); 
			cargoAuxiliar = getCargoAuxiliar(cargo);
			if(Objects.isNull(cargoAuxiliar)){
				cargo.setDescricao(palavraComNumeroCargo.substring(palavraComNumeroCargo.indexOf(IdentificadorArquivoLayout.ESPACO_NA_LINHA.getDescricao())).replace("- -", "").trim());
				cargo = cargoService.save(cargo);
				cargos.add(cargo);
			}else{
				cargo = cargoAuxiliar;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return cargo;
	}

	private Cargo getCargoAuxiliar(Cargo cargo) {
		for(Cargo c : cargos){
			if(c.getCidade().getId().equals(cargo.getCidade().getId()) && 
					   c.getTipoArquivo().getCodigo()==cargo.getTipoArquivo().getCodigo() && 
					   c.getNumero().equals(cargo.getNumero())){
				cargo = c;
				return cargo;
			}
		}
		return null;
	}

	public void setPagamentoService(PagamentoService pagamentoService) {
		this.pagamentoService = pagamentoService;
	}

	public void setEventoService(EventoService eventoService) {
		this.eventoService = eventoService;
	}

}


