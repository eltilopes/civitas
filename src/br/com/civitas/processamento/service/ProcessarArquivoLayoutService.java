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
import br.com.civitas.processamento.entity.CargaHorariaPagamento;
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
import br.com.civitas.processamento.interfac.IProcessarArquivoPagamento;

@Service
public class ProcessarArquivoLayoutService extends ProcessarArquivoPagamento implements IProcessarArquivoPagamento {

	private Pagamento pagamento;
	private Matricula matricula;
	private Secretaria secretaria;
	private NivelPagamento nivelPagamento;
	private Setor setor;
	private Setor setorResumo;
	private Secretaria secretariaResumo;
	private ResumoSetor resumoSetor;
	private List<Pagamento> pagamentos;
	private List<ResumoSetor> resumosSetores;
	private List<String> nomeEventos;
	private boolean processamentoPagamento = false;
	private boolean processamentoEventos = false;
	private boolean processamentoResumo = false;
	private String ultimaLinha = "";
	private String linhaAnterior = "";
	private String descricaoLinha;
	
	public List<ResumoSetor> processar(ArquivoPagamento arquivoPagamento) throws Exception {
		setArquivoPagamento(arquivoPagamento);
		processar();
		return resumosSetores;
	}

	private void processar() throws Exception {
		iniciarArquivos();
		iniciarValores();
		carregarEventos();
		carregarPagamentos();
		finalizarArquivos();
	}

	private void carregarPagamentos() throws Exception {
		nomeEventos = getEventos().stream().map(evento -> evento.getChave()).collect(Collectors.toList());
		BufferedReader br = new BufferedReader(getFilReaderPagamento());
		while (br.ready()) {
			String linha = br.readLine();
			localizarPagamentos(linha);
			linhaAnterior = linha;
		}
		pagamento.getMatricula().getMatriculaPagamento().setVinculo(getVinculo(getVinculo(ultimaLinha), ultimaLinha));
		pagamentos.add(pagamento);
		br.close();
		getPagamentoService().inserirPagamentos(pagamentos, getEventos(), getArquivoPagamento());
		getResumoSetorService().insertAll(resumosSetores);
	}

	private void carregarEventos() throws IOException {
		BufferedReader brEvento = new BufferedReader(getFileReaderEvento());
		while (brEvento.ready()) {
			String linha = brEvento.readLine();
			localizarEvento(linha);
		}
		brEvento.close();
	}

	private void iniciarValores() {
		pagamento = null;
		matricula = null;
		pagamentos = new ArrayList<Pagamento>();
		nivelPagamento = null;
		secretaria = null;
		setor = null;
		resumoSetor = null;
		resumosSetores = new ArrayList<ResumoSetor>();
		setMatriculas(getMatriculaService().buscarPorCidade(getArquivoPagamento().getCidade()));
		setNiveisPagamento(getNivelPagamentoService().buscarTipoArquivoCidadeAno(getArquivoPagamento().getCidade(),
				getArquivoPagamento().getTipoArquivo(), getArquivoPagamento().getAno()));
		setCargasHorariaPagamento(getCargaHorariaPagamentoService()
				.buscarTipoArquivoCidade(getArquivoPagamento().getCidade(), getArquivoPagamento().getTipoArquivo()));
		setUnidadesTrabalho(getUnidadeTrabalhoService().buscarTipoArquivoCidade(getArquivoPagamento().getCidade(),
				getArquivoPagamento().getTipoArquivo()));
		setSetores(getSetorService().buscarTipoArquivoCidade(getArquivoPagamento().getCidade(),
				getArquivoPagamento().getTipoArquivo()));
		setSecretarias(getSecretariaService().buscarTipoArquivoCidade(getArquivoPagamento().getCidade(),
				getArquivoPagamento().getTipoArquivo()));
		setCargos(getCargoService().buscarTipoArquivoCidade(getArquivoPagamento().getCidade(),
				getArquivoPagamento().getTipoArquivo()));
		setVinculos(getVinculoService().buscarPorCidade(getArquivoPagamento().getCidade()));
		setEventos(getEventoService().buscarTipoArquivoCidade(getArquivoPagamento().getCidade(),
				getArquivoPagamento().getTipoArquivo()));
		processamentoPagamento = false;
		processamentoEventos = false;
		processamentoResumo = false;
		ultimaLinha = "";
		linhaAnterior = "";
	}

	private void localizarEvento(String linha) {
		verificarIdentificadorEvento(linha);
		if (processamentoEventos && !(linha.contains(IdentificadorArquivoLayout.INICIO_EVENTO.getDescricao()))) {
			getEvento(linha, getChaveEvento(linha));
		}
	}

	private String getChaveEvento(String linha) {
		String linhaAtual = linha;
		try {
			linhaAtual = retirarValorEntreParenteses(linhaAtual);
			linhaAtual = retirarValorEntreAsteriscos(linhaAtual);
			int posicaoInicialIdentificador = 3;
			int posicaoPrimeiraVirgula = linhaAtual.indexOf(IdentificadorArquivoLayout.VIRGULA.getDescricao());
			String linhaInvertida = new StringBuffer(linhaAtual.substring(0, posicaoPrimeiraVirgula)).reverse()
					.toString();
			int posicaoEspacoLinhaInvertida = linhaInvertida
					.indexOf(IdentificadorArquivoLayout.ESPACO_NA_LINHA.getDescricao());
			return linhaAtual
					.substring(posicaoInicialIdentificador, posicaoPrimeiraVirgula - posicaoEspacoLinhaInvertida)
					.trim();

		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar o Chave Evento. Linha: " + linha);
		}
	}

	private void verificarIdentificadorEvento(String linha) {
		if (linha.contains(IdentificadorArquivoLayout.INICIO_EVENTO.getDescricao())) {
			processamentoEventos = true;
		}
		if ((linha.contains(IdentificadorArquivoLayout.FIM_EVENTO.getDescricao()))) {
			processamentoEventos = false;
		}
	}

	private void verificarIdentificadorPagamento(String linha) {
		if (linha.contains(IdentificadorArquivoLayout.INICIO_EVENTO.getDescricao())) {
			processamentoPagamento = true;
		}
		if ((linha.contains(IdentificadorArquivoLayout.FIM_EVENTO.getDescricao()))) {
			processamentoPagamento = false;
		}
	}

	private void verificarIdentificadorResumo(String linha) {
		if (linha.contains(IdentificadorArquivoLayout.INICIO_RESUMO.getDescricao())) {
			processamentoResumo = true;
			resumoSetor = new ResumoSetor();
			setorResumo = setor;
			secretariaResumo = secretaria;
		}
		if (processamentoResumo && linha.contains(IdentificadorArquivoLayout.FIM_RESUMO.getDescricao())) {
			processamentoResumo = false;
		}
	}

	private void verificarResumoSetor() {
		List<Pagamento> pagamentosSetor = pagamentos.stream()
				.filter(p -> p.getMatriculaPagamento().getSetor().equals(setorResumo) 
						&& p.getMatriculaPagamento().getSecretaria().equals(secretariaResumo))
				.collect(Collectors.toCollection(ArrayList<Pagamento>::new));
		resumoSetor.setQuantidadePagamentos(pagamentosSetor.size());
		resumoSetor.setSetor(setorResumo);
		resumoSetor.setSecretaria(secretariaResumo);
		resumoSetor.setArquivoPagamento(getArquivoPagamento());
		pagamentosSetor.stream().forEach(p -> {
			resumoSetor.setSomatorioRemuneracao(resumoSetor.getSomatorioRemuneracao() + p.getTotalRemuneracao());
			resumoSetor.setSomatorioDescontos(resumoSetor.getSomatorioDescontos() + p.getTotalDescontos());
			resumoSetor.setSomatorioProventos(resumoSetor.getSomatorioProventos() + p.getTotalProventos());
		});
		resumoSetor.arredondarValoresResumo();
		resumosSetores.add(resumoSetor);
		verificarTotaisResumo();
		resumoSetor = null;
	}

	private void verificarTotaisResumo() {
		System.out.println("Resumo Setor: " + secretariaResumo.getDescricao() + "/" + setorResumo.getDescricao() 
		+ ". Quantidade Pagamentos: " + resumoSetor.getQuantidadePagamentos());

		System.out.println("Total Descontos: " + resumoSetor.getTotalDescontos());
		System.out.println("Total Proventos: " + resumoSetor.getTotalProventos());
		System.out.println("Total Remuneração: " + resumoSetor.getTotalRemuneracao());
		System.out.println("Resumo Descontos: " + resumoSetor.getSomatorioDescontos());
		System.out.println("Resumo Proventos: " + resumoSetor.getSomatorioProventos());
		System.out.println("Resumo Remuneração: " + resumoSetor.getSomatorioRemuneracao());
	}

	private void localizarPagamentos(String linhaAtual) throws Exception {
		localizarSecretariaSetor(linhaAtual);
		localizarMatricula(linhaAtual);
		localizarNivelGrupoPagamento(linhaAtual);
		localizarUnidadeTrabalho(linhaAtual);
		verificarIdentificador(linhaAtual);
		verificarResumo(linhaAtual);
	}

	private void verificarResumo(String linhaAtual) {
		verificarIdentificadorResumo(linhaAtual);
		if (processamentoResumo && Util.valorContemNumero(linhaAtual)) {
			getTotaisResumo(linhaAtual);
		}
		if (processamentoPagamento && !processamentoResumo && Objects.nonNull(resumoSetor)) {
			verificarResumoSetor();
		}
	}

	private void getTotaisResumo(String linhaAtual) {
		if (linhaAtual.contains(IdentificadorArquivoLayout.TOTAL_DESCONTOS.getDescricao())) {
			resumoSetor.setTotalDescontos(getValorTotalResumo(linhaAtual));
		}
		if (linhaAtual.contains(IdentificadorArquivoLayout.TOTAL_PROVENTOS.getDescricao())) {
			resumoSetor.setTotalProventos(getValorTotalResumo(linhaAtual));
		}
		if (linhaAtual.contains(IdentificadorArquivoLayout.TOTAL_REMUNERACAO.getDescricao())) {
			resumoSetor.setTotalRemuneracao(getValorTotalResumo(linhaAtual));
		}
	}

	private Double getValorTotalResumo(String linhaAtual) {
		int posicaoPrimeiroNumero = Util.posicaoPrimeiraNumero(linhaAtual);
		linhaAtual = linhaAtual.substring(posicaoPrimeiroNumero - 1, linhaAtual.length());
		return Double.parseDouble(
				getValorEvento(linhaAtual, linhaAtual.indexOf(IdentificadorArquivoLayout.VIRGULA.getDescricao())));
	}

	private void localizarNivelGrupoPagamento(String linhaAtual) {
		if (linhaAtual.contains(IdentificadorArquivoLayout.NIVEL.getDescricao())
				&& !linhaAtual.contains(IdentificadorArquivoLayout.NOME_CARGO.getDescricao())) {
			nivelPagamento = getNivelPagamento(getNivelPagamento(linhaAtual), linhaAtual);
			matricula.getMatriculaPagamento().setNivelPagamento(nivelPagamento);
			matricula.getMatriculaPagamento().setCargaHorariaPagamento(
					getCargaHorariaPagamento(getCargaHorariaPagamento(linhaAtual), linhaAtual));
		}
	}

	private void localizarUnidadeTrabalho(String linhaAtual) {
		if (linhaAtual.contains(IdentificadorArquivoLayout.UNIDADE_TRABALHO.getDescricao()) && temUnidadeTrabalho(linhaAtual)) {
			matricula.getMatriculaPagamento()
					.setUnidadeTrabalho(getUnidadeTrabalho(getUnidadeTrabalho(linhaAtual), linhaAtual));
		}
	}

	private boolean temUnidadeTrabalho(String linhaAtual) {
		return StringUtils.notNullOrEmpty(linhaAtual.replace(IdentificadorArquivoLayout.UNIDADE_TRABALHO.getDescricao(), "")
		.replace(IdentificadorArquivoLayout.UNIDADE_TRABALHO_2_HIFEN.getDescricao(), "")
		.replace(IdentificadorArquivoLayout.UNIDADE_TRABALHO_2.getDescricao(), "")
		.replace(IdentificadorArquivoLayout.HIFEN.getDescricao(), "").trim());
	}

	private void localizarSecretariaSetor(String linhaAtual) {
		if (linhaAtual.contains(IdentificadorArquivoLayout.PAGINA.getDescricao())) {
			secretaria = getSecretaria(getSecretaria(linhaAtual), linhaAtual);
			setor = getSetor(getSetor(linhaAtual), linhaAtual);
		} else if (linhaAnterior.contains(IdentificadorArquivoLayout.AGRUPAMENTO_GERAL.getDescricao())) {
			secretaria = getSecretaria(getSecretaria(linhaAnterior), linhaAnterior);
		}
	}

	private CargaHorariaPagamento getCargaHorariaPagamento(String linhaAtual) throws ApplicationException {
		CargaHorariaPagamento cargaHorariaPagamento = new CargaHorariaPagamento();
		try {
			String codigo = linhaAtual.substring(0, linhaAtual.indexOf(IdentificadorArquivoLayout.HIFEN.getDescricao()))
					.trim();
			String descricao = linhaAtual.substring(
					linhaAtual.indexOf(IdentificadorArquivoLayout.HIFEN.getDescricao())
							+ IdentificadorArquivoLayout.HIFEN.getDescricao().length(),
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
			String codigo = linhaAtual
					.substring(linhaAtual.lastIndexOf(IdentificadorArquivoLayout.HIFEN.getDescricao()) - 4,
							linhaAtual.lastIndexOf(IdentificadorArquivoLayout.HIFEN.getDescricao()))
					.trim();
			String descricao = linhaAtual
					.substring(linhaAtual.lastIndexOf(IdentificadorArquivoLayout.HIFEN.getDescricao())
							+ IdentificadorArquivoLayout.HIFEN.getDescricao().length(), linhaAtual.length())
					.trim();
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
			String codigo = linhaAtual
					.substring(linhaAtual.indexOf(IdentificadorArquivoLayout.ESPACO_NA_LINHA.getDescricao()));
			codigo = codigo.trim();
			codigo = codigo.substring(0, codigo.indexOf(IdentificadorArquivoLayout.ESPACO_NA_LINHA.getDescricao())).trim();
			String descricaoUnidadeTrabalho;
			if(linhaAtual.contains(IdentificadorArquivoLayout.UNIDADE_TRABALHO_2_HIFEN.getDescricao())){
				descricaoUnidadeTrabalho = linhaAtual.substring(linhaAtual.indexOf(codigo) + codigo.length(),
						linhaAtual.indexOf(IdentificadorArquivoLayout.UNIDADE_TRABALHO_2_HIFEN.getDescricao())).trim();
			}else{
				descricaoUnidadeTrabalho = linhaAtual.substring(linhaAtual.indexOf(codigo) + codigo.length(),
						linhaAtual.indexOf(IdentificadorArquivoLayout.UNIDADE_TRABALHO_2.getDescricao())).trim();
			}
			Integer inicioCodigoUnidadeDois = temInteiro(descricaoUnidadeTrabalho);
			if (Objects.nonNull(inicioCodigoUnidadeDois)) {
				descricaoUnidadeTrabalho = descricaoUnidadeTrabalho.substring(0, inicioCodigoUnidadeDois).trim();
			}
			if(descricaoUnidadeTrabalho.substring(0,1).equals(IdentificadorArquivoLayout.HIFEN.getDescricao())){
				descricaoUnidadeTrabalho = descricaoUnidadeTrabalho.replace(IdentificadorArquivoLayout.HIFEN.getDescricao(), "").trim();
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

	public Integer temInteiro(String descricao) {
		char[] c = descricao.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (Character.isDigit(c[i])) {
				return i;
			}
		}
		return null;
	}

	private Secretaria getSecretaria(String linhaAtual) throws ApplicationException {
		Secretaria secretaria = new Secretaria();
		try {
			String descricao = "";
			if (linhaAnterior.contains(IdentificadorArquivoLayout.AGRUPAMENTO_SETOR.getDescricao())) {
				descricao = linhaAtual.substring(
						linhaAtual.indexOf(IdentificadorArquivoLayout.ESPACO_NA_LINHA.getDescricao()),
						linhaAtual.indexOf(IdentificadorArquivoLayout.PAGINA.getDescricao()));
				descricao = descricao
						.substring(0, descricao.lastIndexOf(IdentificadorArquivoLayout.ESPACO_NA_LINHA.getDescricao()))
						.trim();
			} else if (linhaAnterior.contains(IdentificadorArquivoLayout.AGRUPAMENTO_GERAL.getDescricao())) {
				descricao = linhaAtual.substring(
						linhaAtual.indexOf(IdentificadorArquivoLayout.AGRUPAMENTO_GERAL.getDescricao())
								+ IdentificadorArquivoLayout.AGRUPAMENTO_GERAL.getDescricao().length(),
						linhaAtual.length());
				descricao = descricao.substring(descricao.indexOf(IdentificadorArquivoLayout.HIFEN.getDescricao()) + 1,
						descricao.length()).trim();
			} else if (linhaAtual.contains(IdentificadorArquivoLayout.PAGINA.getDescricao())) {
				descricao = linhaAtual.substring(
						linhaAtual.indexOf(IdentificadorArquivoLayout.ESPACO_NA_LINHA.getDescricao()),
						linhaAtual.indexOf(IdentificadorArquivoLayout.PAGINA.getDescricao()));
				descricao = descricao
						.substring(0, descricao.lastIndexOf(IdentificadorArquivoLayout.ESPACO_NA_LINHA.getDescricao()))
						.trim();
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
			String descricao = linhaAtual
					.substring(linhaAtual.indexOf(IdentificadorArquivoLayout.PAGINA.getDescricao()));
			descricao = descricao
					.substring(descricao.indexOf(IdentificadorArquivoLayout.ESPACO_NA_LINHA.getDescricao()),
							descricao.length() - 3)
					.trim();
			String codigo = linhaAtual
					.substring(linhaAtual.indexOf(IdentificadorArquivoLayout.PAGINA.getDescricao()));
			codigo = codigo.substring(Util.posicaoPrimeiraNumero(codigo), codigo.indexOf(descricao)).trim();
			setor.setCodigo(Integer.parseInt(codigo));
			setor.setCidade(getArquivoPagamento().getCidade());
			setor.setTipoArquivo(getArquivoPagamento().getTipoArquivo());
			setor.setDescricao(descricao);

		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar o Setor. Linha: " + linhaAtual);
		}
		return setor;
	}

	private void verificarIdentificador(String linha) {
		getDiasTrabalhados(linha);
		verificarIdentificadorPagamento(linha);
		if (processamentoPagamento && !linha.contains(IdentificadorArquivoLayout.CARGO.getDescricao())
				&& !linha.contains(IdentificadorArquivoLayout.INICIO_EVENTO.getDescricao())) {
			for (Evento e : getEventos()) {
				if (getChaveEvento(linha).equals(e.getChave())) {
					getEventoPagamento(linha, e);
				}
			}
		}
		if (linha.contains(IdentificadorArquivoLayout.TOTAIS_PAGAMENTO.getDescricao())) {
			setTotaisPagamento(linha);
			processamentoPagamento = false;
		}
	}

	private void setTotaisPagamento(String linha) {
		try {
			int inicioRemuneracao = linha.indexOf(IdentificadorArquivoLayout.TOTAIS_PAGAMENTO.getDescricao())
					+ IdentificadorArquivoLayout.TOTAIS_PAGAMENTO.getDescricao().length();
			int finalRemuneracao = linha.substring(inicioRemuneracao)
					.indexOf(IdentificadorArquivoLayout.VIRGULA.getDescricao()) + inicioRemuneracao + 3;
			int finalLiquido = linha.substring(finalRemuneracao)
					.indexOf(IdentificadorArquivoLayout.VIRGULA.getDescricao()) + finalRemuneracao + 3;
			int finalProventos = linha.substring(finalLiquido)
					.indexOf(IdentificadorArquivoLayout.VIRGULA.getDescricao()) + finalLiquido + 3;
			int finalDescontos = linha.length();
			pagamento.setTotalRemuneracao(Double.parseDouble(
					linha.substring(inicioRemuneracao, finalRemuneracao).replace(".", "").replace(",", ".")));
			pagamento.setTotalLiquido(Double
					.parseDouble(linha.substring(finalRemuneracao, finalLiquido).replace(".", "").replace(",", ".")));
			pagamento.setTotalProventos(Double
					.parseDouble(linha.substring(finalLiquido, finalProventos).replace(".", "").replace(",", ".")));
			pagamento.setTotalDescontos(Double
					.parseDouble(linha.substring(finalProventos, finalDescontos).replace(".", "").replace(",", ".")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getEventoPagamento(String linhaOriginal, Evento evento) {
		String linha = retirarValorEntreParenteses(linhaOriginal);
		linha = retirarValorEntreAsteriscos(linha);
		Evento eventoLinha = new Evento();
		for (String linhaAtual : getEventosLinha(linha, evento)) {
			linhaAtual = linhaAtual.trim();
			if (StringUtils.notNullOrEmpty(linhaAtual)) {
				for (Evento e : getEventos()) {
					if (e.getChave().equals(getChaveEvento(linhaAtual))) {
						eventoLinha = e;
					}
				}
				try {
					EventoPagamento eventoPagamento = new EventoPagamento();
					eventoPagamento.setPagamento(pagamento);
					eventoPagamento.setEvento(eventoLinha);
					eventoPagamento.setValor(Double.parseDouble(getValorEvento(linhaAtual,
							linhaAtual.indexOf(IdentificadorArquivoLayout.VIRGULA.getDescricao()))));
					pagamento.getEventosPagamento().add(eventoPagamento);
				} catch (Exception e) {
					throw new ApplicationException("Erro ao pegar o Evento Pagamento. Linha: " + linhaOriginal);
				}
			}
		}
	}

	private List<String> getEventosLinha(String linha, Evento evento) {
		List<String> eventosLinha = new ArrayList<String>();
		descricaoLinha = linha.replace(evento.getChave(), "");
		nomeEventos.sort((p1, p2) -> p2.compareTo(p1));
		List<String> resultado = nomeEventos.stream().filter(nomeCargo -> descricaoLinha.contains(nomeCargo))
				.collect(Collectors.toList());
		if (!resultado.isEmpty()) {
			descricaoLinha = linha;
			for (String nomeEvento : resultado) {
				String novoEvento = descricaoLinha.substring(0, descricaoLinha.indexOf(nomeEvento) - 3);
				descricaoLinha = descricaoLinha.replace(novoEvento, "");
				eventosLinha.add(novoEvento);
			}
			eventosLinha.add(descricaoLinha);
		} else {
			eventosLinha.add(linha);
		}
		return eventosLinha;
	}

	private void getDiasTrabalhados(String linhaAtual) {
		if (linhaAtual.contains(IdentificadorArquivoLayout.SALARIO_BASE.getDescricao())) {
			String dias = linhaAtual
					.substring(linhaAtual.indexOf(IdentificadorArquivoLayout.VIRGULA.getDescricao()) + 3).trim();
			dias = dias.substring(0, dias.indexOf("d") + 1).trim();
			pagamento.setDiasTrabalhados(dias);
		}
	}

	private String retirarValorEntreParenteses(String linha) {
		if (linha.contains(IdentificadorArquivoLayout.PARENTESE_INICIAL.getDescricao())
				&& linha.contains(IdentificadorArquivoLayout.PARENTESE_FINAL.getDescricao())) {
			try {
				int posicaoInicialParentese = linha
						.indexOf(IdentificadorArquivoLayout.PARENTESE_INICIAL.getDescricao());
				int posicaoFinalParentese = linha.indexOf(IdentificadorArquivoLayout.PARENTESE_FINAL.getDescricao());
				String valorEntreParenteses = linha.substring(posicaoInicialParentese, posicaoFinalParentese + 1);
				return linha.replace(valorEntreParenteses, "");
			} catch (Exception e) {
				throw new ApplicationException("Erro ao pegar o Evento Pagamento. Linha: " + linha);
			}
		}
		return linha;
	}

	private String retirarValorEntreAsteriscos(String linha) {
		if (StringUtils.contarVezesPalavraNaFrase(linha,
				"\\" + IdentificadorArquivoLayout.ASTERISCO.getDescricao()) >= 1) {
			try {
				int posicaoInicialAsterisco = linha.indexOf(IdentificadorArquivoLayout.ASTERISCO.getDescricao());
				int posicaoFinalAsterisco = linha.lastIndexOf(IdentificadorArquivoLayout.ASTERISCO.getDescricao());
				String valorEntreAsteriscos = linha.substring(posicaoInicialAsterisco, posicaoFinalAsterisco + 1);
				return linha.replace(valorEntreAsteriscos, "");
			} catch (Exception e) {
				throw new ApplicationException("Erro ao pegar o Evento Pagamento. Linha: " + linha);
			}
		}
		return linha;
	}

	private String getValorEvento(String linha, int posicaoVirgula) {
		try {
			String linhaInvertida = new StringBuffer(linha.substring(0, posicaoVirgula)).reverse().toString();
			int posicaoEspacoLinhaInvertida = linhaInvertida
					.indexOf(IdentificadorArquivoLayout.ESPACO_NA_LINHA.getDescricao());
			String valor = linha.substring(posicaoVirgula - posicaoEspacoLinhaInvertida, posicaoVirgula + 3);
			return valor.replace(".", "").replace(",", ".");
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar o Valor Evento. Linha: " + linha);
		}
	}

	private void localizarMatricula(String linhaAtual) throws Exception {

		if (linhaAtual.contains(IdentificadorArquivoLayout.CARGO.getDescricao())) {
			ultimaLinha = linhaAnterior;
			if (linhaAtual.contains(IdentificadorArquivoLayout.INICIO_EVENTO.getDescricao())) {
				processamentoPagamento = true;
			}
			String numeroMatricula = getNumeroMatricula();
			if (Objects.nonNull(pagamento) && Objects.nonNull(matricula)) {
				pagamentos.add(pagamento);
			}
			pagamento = new Pagamento();
			getMatricula(numeroMatricula, linhaAtual);
		}
		if (linhaAtual.contains(IdentificadorArquivoLayout.DATA_ADMISSAO.getDescricao())) {
			matricula.setDataAdmissao(getDataAdmissao(linhaAtual));
		}
	}

	private Date getDataAdmissao(String linhaAtual) {
		try {
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			return (Date) formatter.parse(linhaAtual.substring(
					linhaAtual.indexOf(IdentificadorArquivoLayout.DATA_ADMISSAO.getDescricao()) - 10,
					linhaAtual.indexOf(IdentificadorArquivoLayout.DATA_ADMISSAO.getDescricao())));
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar a Data de Admissão. Linha: " + linhaAtual);
		}
	}

	private String getNumeroMatricula() throws Exception {
		String numeroMatricula = linhaAnterior.substring(0, 8);
		Integer numero = 0;
		try {
			numero = Integer.parseInt(numeroMatricula.replace("-", ""));
		} catch (Exception e) {
			try {
				Vinculo vinculo = getVinculo(linhaAnterior);
				String nomeInvertido = new StringBuffer(
						linhaAnterior.substring(0, linhaAnterior.indexOf(vinculo.getDescricao())).trim()).reverse()
								.toString();
				numero = nomeInvertido.hashCode();
				numeroMatricula = numero.toString().replace("-", "").substring(0, 8);
			} catch (Exception e2) {
				numeroMatricula = numero.toString().replace("-", "");
			}
		}
		return numeroMatricula;
	}

	private void getMatricula(String numeroMatricula, String linhaAtual) {
		matricula = getMatricula(new Matricula(numeroMatricula));
		if (Objects.isNull(matricula)) {
			novaMatricula(numeroMatricula, linhaAtual);
			matricula = getMatricula(matricula, linhaAtual);
		}
		setMatriculaPagamento(linhaAtual);
		matricula.setNomeFuncionario(getNomeFuncionario(linhaAnterior));
		matricula = getMatriculaService().update(matricula);
		pagamento.setMatriculaPagamento(matricula.getMatriculaPagamento());
		pagamento.setMatricula(matricula);
	}

	private void novaMatricula(String numeroMatricula, String linhaAtual) throws ApplicationException {
		matricula = new Matricula();
		matricula.setNumeroMatricula(numeroMatricula);
		matricula.setNomeFuncionario(numeroMatricula);
	}

	private void setMatriculaPagamento(String linhaAtual) {
		matricula.setMatriculaPagamento(new MatriculaPagamento());
		matricula.getMatriculaPagamento().setSecretaria(secretaria);
		matricula.getMatriculaPagamento().setSetor(setor);
		matricula.getMatriculaPagamento().setCargo(getCargo(getCargo(linhaAtual), linhaAtual));
		matricula.getMatriculaPagamento().setVinculo(getVinculo(getVinculo(linhaAnterior), linhaAnterior));
		matricula.getMatriculaPagamento().setCargaHoraria(getCargaHoraria());
		matricula.getMatriculaPagamento().setAno(getArquivoPagamento().getAno());
		matricula.getMatriculaPagamento().setMes(getArquivoPagamento().getMes());
		matricula.getMatriculaPagamento().setMatricula(matricula);
		getMatriculaPagamentoService().salvar(matricula.getMatriculaPagamento());
	}

	private Cargo getCargo(String linhaAtual) throws ApplicationException {
		Cargo cargo = new Cargo();
		try {
			int tamanhoNomeCargo = IdentificadorArquivoLayout.CARGO.getDescricao().length();
			String palavraComNumeroCargo = linhaAtual.substring(tamanhoNomeCargo);
			String valorNumeroCargo = palavraComNumeroCargo.substring(0,
					palavraComNumeroCargo.indexOf(IdentificadorArquivoLayout.ESPACO_NA_LINHA.getDescricao()));
			cargo.setCidade(getArquivoPagamento().getCidade());
			cargo.setTipoArquivo(getArquivoPagamento().getTipoArquivo());
			cargo.setNumero(Integer.parseInt(valorNumeroCargo.trim()));
			cargo.setDescricao(
					palavraComNumeroCargo
							.substring(palavraComNumeroCargo
									.indexOf(IdentificadorArquivoLayout.ESPACO_NA_LINHA.getDescricao()))
							.replace("- -", "").trim());
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar o Cargo. Linha: " + linhaAtual);
		}
		return cargo;
	}

	private String getNomeFuncionario(String linha) throws ApplicationException {
		String nome = "";
		try {
			Integer posicaoInicial = getPosicaoInicialNomeFuncionario(linha);
			nome = linha
					.subSequence(posicaoInicial,
							linha.indexOf(matricula.getMatriculaPagamento().getVinculo().getDescricao()))
					.toString().trim();
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar o Nome do Funcionário. Linha: " + linha);
		}
		return nome;

	}

	private Integer getPosicaoInicialNomeFuncionario(String linha) {
		return linha.indexOf(matricula.getNumeroMatricula()) == -1 ? 0
				: linha.indexOf(matricula.getNumeroMatricula()) + matricula.getNumeroMatricula().length();
	}

	private Vinculo getVinculo(String linha) throws ApplicationException {
		Vinculo vinculo = new Vinculo();
		try {
			int posicaoCargaHoraria = linha.indexOf(IdentificadorArquivoLayout.CARGA_HORARIA.getDescricao());
			String valorNumeroVinculo = linha.substring(posicaoCargaHoraria - 3, posicaoCargaHoraria);
			vinculo.setNumero(Integer.parseInt(valorNumeroVinculo.trim()));
			vinculo.setCidade(getArquivoPagamento().getCidade());
			String palavraComNomeVinculo = linha.substring(0, posicaoCargaHoraria - 3);
			String palavraInvertida = new StringBuffer(palavraComNomeVinculo).reverse().toString();
			String nomeVinculoInvertido = palavraInvertida.substring(0,
					palavraInvertida.indexOf(IdentificadorArquivoLayout.ESPACO_NA_LINHA.getDescricao()));
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
					(linhaAnterior.indexOf(IdentificadorArquivoLayout.CARGA_HORARIA.getDescricao())
							+ IdentificadorArquivoLayout.CARGA_HORARIA.getDescricao().length()),
					linhaAnterior.indexOf(IdentificadorArquivoLayout.VINCULO.getDescricao()));
			cargaHorariaNumero = Integer.parseInt(cargaHoraria.trim());
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar o Carga Horária. Linha: " + linhaAnterior);
		}
		return cargaHorariaNumero;
	}

}
