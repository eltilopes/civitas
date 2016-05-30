package br.com.civitas.processamento.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.civitas.processamento.entity.ArquivoPagamento;
import br.com.civitas.processamento.entity.Cargo;
import br.com.civitas.processamento.entity.Evento;
import br.com.civitas.processamento.entity.EventoPagamento;
import br.com.civitas.processamento.entity.Matricula;
import br.com.civitas.processamento.entity.Pagamento;
import br.com.civitas.processamento.entity.Vinculo;
import br.com.civitas.processamento.enums.Identificador;
import br.com.civitas.processamento.utils.DiretorioProcessamento;

@Service
public class ProcessarService {

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
	private  List<Evento> eventos = new ArrayList<Evento>();
	private  boolean processamentoPagamentoAtivo = false;
	private  boolean processamentoEventos = false;
	private  boolean existeEventoTemp = false;
	private  String ultimaLinha = "";
	private  String linhaAnterior = "";
	private  String nomeArquivoTemporario;
	private double PROVENTOS = 0d;
	private double DESCONTOS = 0d;
	
	public List<Pagamento> getPagamentos(ArquivoPagamento arquivo){
		processar(arquivo);
		return pagamentos;
	}
	
	private void processar(ArquivoPagamento arquivo) {
		try {
			criarArquivoProcessamento(arquivo);
			iniciarValores(arquivo);
			PDDocument document = null;
			document = PDDocument.load(new File(nomeArquivoTemporario));
			document.getClass();
			if (!document.isEncrypted()) {
				PDFTextStripperByArea stripper = new PDFTextStripperByArea();
				stripper.setSortByPosition(true);
				PDFTextStripper Tstripper = new PDFTextStripper();
				String conteudoArquivo = Tstripper.getText(document);
				String nomeArquivoTemporario = arquivo.getNomeArquivo().substring(0, arquivo.getNomeArquivo().length() -3 ) + "txt";
				BufferedWriter buffWrite = new BufferedWriter(new FileWriter(nomeArquivoTemporario));
				buffWrite.append(conteudoArquivo);
				buffWrite.close();
				FileReader frEvento = new FileReader(nomeArquivoTemporario);
				BufferedReader brEvento = new BufferedReader(frEvento);
				limparValoresBanco();
				while (brEvento.ready()) {
					String linha = brEvento.readLine(); 
					localizarEvento(linha, arquivo);
				}
				brEvento.close();
				salvarEventos();
				FileReader fr = new FileReader(nomeArquivoTemporario);
				BufferedReader br = new BufferedReader(fr);
				while (br.ready()) {
					String linha = br.readLine(); 
					getTotaisSetores(linha);
					localizarPagamentos(linha, arquivo);
					linhaAnterior = linha;
				}
				pagamento.getMatricula().setVinculo(getVinculo(ultimaLinha, arquivo));
				pagamentos.add(pagamento);
				br.close();
				pagamentoService.inserirPagamentos(pagamentos,eventos, arquivo);
				File f = new File(nomeArquivoTemporario);  
				f.delete();
			}
			document.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void criarArquivoProcessamento(ArquivoPagamento arquivo) throws IOException {
		nomeArquivoTemporario = "";
		String filename = FilenameUtils.getName(arquivo.getFile().getFileName());
	    InputStream input = arquivo.getFile().getInputstream();
	    File file = new File(DiretorioProcessamento.getDiretorioTemporario(), filename);
	    OutputStream output = new FileOutputStream(file);
	    nomeArquivoTemporario = file.getAbsolutePath();
	    try {
	        IOUtils.copy(input, output);
	    } catch(Exception e){
	    	e.printStackTrace();
	    	nomeArquivoTemporario = null;
	    } finally {
	        IOUtils.closeQuietly(input);
	        IOUtils.closeQuietly(output);
	    }
		
	}

	private void iniciarValores(ArquivoPagamento arquivo) {
		pagamento = null;
		matricula = null;
		pagamentos = new ArrayList<Pagamento>();
		matriculas = new ArrayList<Matricula>();
		cargos = cargoService.buscarTipoArquivoCidade(arquivo.getCidade(), arquivo.getTipoArquivo());
		vinculos = vinculoService.buscarPorCidade(arquivo.getCidade());
		eventos = new ArrayList<Evento>();
		processamentoPagamentoAtivo = false;
		processamentoEventos = false;
		existeEventoTemp = false;
		ultimaLinha = "";
		linhaAnterior = "";
	}

	private void getTotaisSetores(String linha) {
		if(linha.contains("TOTAL DE DESCONTOS:")){
			DESCONTOS = DESCONTOS + Double.parseDouble(getValorEvento(linha, linha.lastIndexOf(Identificador.VIRGULA.getDescricao())));
		}
		if(linha.contains("TOTAL DE PROVENTOS:")){
			PROVENTOS = PROVENTOS + Double.parseDouble(getValorEvento(linha, linha.lastIndexOf(Identificador.VIRGULA.getDescricao())));
		}
		
	}

	private void salvarEventos() {
		List<Evento> eventosD = eventos.stream().distinct().collect(Collectors.toList());
		eventosD.forEach((e) -> {
			try {
				eventoService.save(e);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		eventos = eventoService.buscarTodos();
	}

	private void localizarEvento(String linha, ArquivoPagamento arquivo) {
		verificarIdentificadorEvento(linha);
		if(processamentoEventos && !(linha.contains(Identificador.INICIO_EVENTO.getDescricao()))){
			getEvento(linha, arquivo);
		}
	}
	
	private  void getEvento(String linha, ArquivoPagamento arquivo) {
		//TODO : melhorar a busca do evento
		try {
			existeEventoTemp = false;
			String chaveEvento = getChaveEvento(linha);
			eventos.forEach((e) -> {
				if(e.getChave().equals(chaveEvento)){
					existeEventoTemp = true;
				}
			});
			
			eventos.forEach((e) -> {
				if(e.getChave().equals(chaveEvento)){
					existeEventoTemp = true;
				}
			});
			if(!existeEventoTemp){
				eventos.add(new Evento(chaveEvento, arquivo.getCidade(), arquivo.getTipoArquivo()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(linha);
		}
	}

	private String getChaveEvento(String linha) {
		try {
			int posicaoInicialIdentificador = 3 ;
			int posicaoPrimeiraVirgula = linha.indexOf(Identificador.VIRGULA.getDescricao()) ;
			String linhaInvertida = new StringBuffer(linha.substring(0, posicaoPrimeiraVirgula)).reverse().toString();
			int posicaoEspacoLinhaInvertida = linhaInvertida.indexOf(Identificador.ESPACO_NA_LINHA.getDescricao()) ;
			return linha.substring(posicaoInicialIdentificador, posicaoPrimeiraVirgula - posicaoEspacoLinhaInvertida);
			
		} catch (Exception e) {
			System.out.println(linha);
		}
		return "";
	}
	
	private void verificarIdentificadorEvento(String linha) {
		if(linha.contains(Identificador.INICIO_EVENTO.getDescricao())){
			processamentoEventos = true;
		}
		if((linha.contains(Identificador.FIM_EVENTO.getDescricao()))){
			processamentoEventos = false;
		}
	}

	
	private  void limparValoresBanco() {
//		eventoPagamentoService.removeAll(eventoPagamentoService.buscarTodos());
//		pagamentoService.removeAll(pagamentoService.buscarTodos());
//		matriculaService.removeAll(matriculaService.buscarTodos());
//		arquivoService.removeAll(arquivoService.buscarTodos());
//		eventoService.removeAll(eventoService.buscarTodos());
//		cargoService.removeAll(cargoService.buscarTodos());
//		vinculoService.removeAll(vinculoService.buscarTodos());
	}

	private void localizarPagamentos(String linhaAtual, ArquivoPagamento arquivo) {
		localizarMatricula(linhaAtual, arquivo);
		verificarIdentificador(linhaAtual, arquivo.getNomeArquivo());
	}
	
	private  void verificarIdentificador(String linha, String nomeArquivo) {
		
		//TODO tirar os valores do codigo e colocar no banco amarrado a um tipo de arquivo os valores tipo PEB
		eventos.forEach((e) -> {
			if(processamentoPagamentoAtivo && !linha.contains(Identificador.CARGO.getDescricao())
					&& !linha.contains(Identificador.DATA_ADMISSAO.getDescricao())
					&& !linha.contains(Identificador.UNIDADE_TRABALHO.getDescricao())
					&& !linha.contains(Identificador.INICIO_EVENTO.getDescricao())
					&& !linha.contains(Identificador.DATA_INICIO_FIM.getDescricao())
					&& !linha.contains(Identificador.PEB_I.getDescricao())
					&& !linha.contains(Identificador.AREA_ATUACAO.getDescricao())
					&& getChaveEvento(linha).contains(e.getChave())){
				pagamento.getEventosPagamento().add(getEventoPagamento(linha, e));
			}
		});
		if(linha.contains(Identificador.TOTAIS_PAGAMENTO.getDescricao())){
			setTotaisPagamento(linha);
			processamentoPagamentoAtivo = false;
		}
	}
	
	private void setTotaisPagamento(String linha) {
		try {
			int inicioRemuneracao = linha.indexOf(Identificador.TOTAIS_PAGAMENTO.getDescricao()) + Identificador.TOTAIS_PAGAMENTO.getDescricao().length();
			int finalRemuneracao = linha.substring(inicioRemuneracao).indexOf(Identificador.VIRGULA.getDescricao()) + inicioRemuneracao +3 ;
			int finalLiquido = linha.substring(finalRemuneracao).indexOf(Identificador.VIRGULA.getDescricao()) + finalRemuneracao + 3;
			int finalProventos = linha.substring(finalLiquido).indexOf(Identificador.VIRGULA.getDescricao()) + finalLiquido + 3;
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
			if(!linha.contains(Identificador.PENSAO_ALIMENTICIA.getDescricao()) && ((linha.contains(Identificador.IRRF.getDescricao()) && evento.getChave().contains(Identificador.IRRF.getDescricao()))
					|| linha.contains(Identificador.ADICIONAL_FERIAS.getDescricao())
					|| linha.substring(linha.length()-1, linha.length()).equals(Identificador.PORCENTAGEM.getDescricao()))){
				eventoPagamento.setValor(Double.parseDouble(getValorEvento(linha, linha.indexOf(Identificador.VIRGULA.getDescricao()))));
			}else if(linha.contains(Identificador.IRRF.getDescricao()) && !evento.getChave().contains(Identificador.IRRF.getDescricao())){
				linha = getEventoIRRF(linha, evento);
				eventoPagamento.setValor(Double.parseDouble(getValorEvento(linha, linha.lastIndexOf(Identificador.VIRGULA.getDescricao()))));
			}else if(linha.contains(Identificador.PENSAO_ALIMENTICIA.getDescricao())){
				String linhaTemp = linha.replace("(16,50% LIQ)", "").replace("16,5%", "");
				eventoPagamento.setValor(Double.parseDouble(getValorEvento(linhaTemp, linhaTemp.lastIndexOf(Identificador.VIRGULA.getDescricao()))));
			}else {
				eventoPagamento.setValor(Double.parseDouble(getValorEvento(linha, linha.lastIndexOf(Identificador.VIRGULA.getDescricao()))));
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
		e.setEvento(getEvento(Identificador.IRRF));
		String linhaTemporaria = linha.substring(linha.indexOf(Identificador.IRRF.getDescricao())
				+ Identificador.IRRF.getDescricao().length());
		e.setValor(Double.parseDouble(getValorEvento(linhaTemporaria, linhaTemporaria.indexOf(Identificador.VIRGULA.getDescricao()))));
		pagamento.getEventosPagamento().add(e);
		return linha.substring(0, linha.indexOf(Identificador.IRRF.getDescricao()) - 3);
	}

	private Evento getEvento(Identificador irrf) {
		for(Evento e : eventos ){
			if(e.getChave().contains(irrf.getDescricao())){
				return e;
			}
		}
		return null;
	}

	private String getValorEvento(String linha, int posicaoVirgula) {
		try {
			String linhaInvertida = new StringBuffer(linha.substring(0, posicaoVirgula)).reverse().toString();
			int posicaoEspacoLinhaInvertida = linhaInvertida.indexOf(Identificador.ESPACO_NA_LINHA.getDescricao()) ;
			String valor = linha.substring(posicaoVirgula - posicaoEspacoLinhaInvertida, posicaoVirgula + 3);
			return valor.replace(".","").replace(",", ".");
		} catch (Exception e) {	
			System.out.println("erro ao pegar valor : " + linha);
		}
		return "0.0";
	}
		

	private  void localizarMatricula(String linhaAtual, ArquivoPagamento arquivo) {
		if(linhaAtual.contains(Identificador.CARGO.getDescricao())){
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
			int posicaoCargaHoraria = linha.indexOf(Identificador.CARGA_HORARIA.getDescricao());
			String valorNumeroVinculo = linha.substring(posicaoCargaHoraria - 3, posicaoCargaHoraria);
			vinculo.setNumero(Integer.parseInt(valorNumeroVinculo.trim())); 
			vinculo.setCidade(arquivo.getCidade());
			vinculoAuxiliar = getVinculo(vinculo);
			if(Objects.isNull(vinculoAuxiliar)){
				String palavraComNomeVinculo = linha.substring(0,posicaoCargaHoraria - 3);
				String palavraInvertida = new StringBuffer(palavraComNomeVinculo).reverse().toString();
				String nomeVinculoInvertido = palavraInvertida.substring(0, palavraInvertida.indexOf(Identificador.ESPACO_NA_LINHA.getDescricao()));
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
				(linhaAnterior.indexOf(Identificador.CARGA_HORARIA.getDescricao()) + Identificador.CARGA_HORARIA.getDescricao().length())
				,linhaAnterior.indexOf(Identificador.VINCULO.getDescricao())
				);
		return Integer.parseInt(cargaHoraria.trim());
	}

	private Cargo getCargo(String linhaAtual, ArquivoPagamento arquivo) {
		Cargo cargo = new Cargo();
		Cargo cargoAuxiliar = new Cargo();
		try {
			int tamanhoNomeCargo = Identificador.CARGO.getDescricao().length();
			String palavraComNumeroCargo = linhaAtual.substring(tamanhoNomeCargo);
			String valorNumeroCargo = palavraComNumeroCargo.substring(0, palavraComNumeroCargo.indexOf(Identificador.ESPACO_NA_LINHA.getDescricao()));
			cargo.setCidade(arquivo.getCidade());
			cargo.setTipoArquivo(arquivo.getTipoArquivo());
			cargo.setNumero(Integer.parseInt(valorNumeroCargo.trim())); 
			cargoAuxiliar = getCargoAuxiliar(cargo);
			if(Objects.isNull(cargoAuxiliar)){
				cargo.setDescricao(palavraComNumeroCargo.substring(palavraComNumeroCargo.indexOf(Identificador.ESPACO_NA_LINHA.getDescricao())).replace("- -", "").trim());
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


