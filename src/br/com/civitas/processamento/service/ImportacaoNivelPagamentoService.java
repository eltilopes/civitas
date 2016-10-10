package br.com.civitas.processamento.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.civitas.arquitetura.ApplicationException;
import br.com.civitas.arquitetura.util.Util;
import br.com.civitas.helpers.utils.StringUtils;
import br.com.civitas.processamento.entity.Ano;
import br.com.civitas.processamento.entity.Cidade;
import br.com.civitas.processamento.entity.NivelPagamento;
import br.com.civitas.processamento.entity.Secretaria;
import br.com.civitas.processamento.enums.TipoArquivo;
import br.com.civitas.processamento.utils.DiretorioProcessamento;

@Service
public class ImportacaoNivelPagamentoService implements Serializable {

	private static final long serialVersionUID = -6205452183309104737L;
	private static final String DELIMITADOR_CSV = "\t";
	private static final String PONTO_VIRGULA_CSV = ";";

	@Autowired
	private  NivelPagamentoService nivelPagamentoService;
	
	private UploadedFile file;
	private String nomeArquivo;
	private int quantidadeLinhasComNiveis;
	private String nomeArquivoTemporario;
	private List<NivelPagamento> niveisPagamento;
	
	private Ano ano;
	private Cidade cidade;
	private Secretaria secretaria;
	private TipoArquivo tipoArquivo;
	
	public Map<String, Object> importarArquivo(String nomeArquivo, UploadedFile file, Ano ano, Cidade cidade, Secretaria secretaria,
			TipoArquivo tipoArquivo) throws Exception {
		Map<String, Object> informacoesImportacao = new HashMap<String, Object>();
		iniciarValores(file, nomeArquivo,  ano, cidade, secretaria, tipoArquivo);
		importarValores();
		salvarNiveis();
		informacoesImportacao.put("quantidadeLinhasComNiveis", quantidadeLinhasComNiveis);
		informacoesImportacao.put("niveisPagamento", niveisPagamento);
		return informacoesImportacao;
	}

	private void iniciarValores(UploadedFile file, String nomeArquivo, Ano ano, Cidade cidade, Secretaria secretaria, 
			TipoArquivo tipoArquivo) {
		this.file = file;
		this.nomeArquivo = nomeArquivo;
		this.ano = ano;
		this.cidade = cidade;
		this.secretaria = secretaria;
		this.tipoArquivo = tipoArquivo;
		quantidadeLinhasComNiveis = 1;
		niveisPagamento = new ArrayList<NivelPagamento>();
	}

	private void salvarNiveis() {
		quantidadeLinhasComNiveis = niveisPagamento.size();
		if(!niveisPagamento.isEmpty()){
			String codigos = "";
			String descricoes = "";
			String salariosBase = "";
			for(NivelPagamento np : niveisPagamento){
				codigos = codigos + "'" + np.getCodigo() + "',";
				descricoes = descricoes + "'" + np.getDescricao() + "',";
				salariosBase = salariosBase + np.getSalarioBase() + ",";
			}
			List<NivelPagamento> listaNiveisRemocao = removerNiveisExistentes(codigos.substring(0, codigos.length() - 1), 
					descricoes.substring(0, descricoes.length() - 1), salariosBase.substring(0, salariosBase.length() - 1));
			niveisPagamento.removeAll(listaNiveisRemocao);
			if(!niveisPagamento.isEmpty()){
				nivelPagamentoService.saveOrUpdateAll(niveisPagamento);
			}
		}
	}

	private List<NivelPagamento> removerNiveisExistentes(String codigos, String descricoes, String salariosBase) {
		List<NivelPagamento> niveisExistentes = nivelPagamentoService.buscarTipoArquivoCidadeAnoSecretariaCodigoDescricaoSalario(
				cidade, tipoArquivo, ano, secretaria, codigos, descricoes, salariosBase);
		List<NivelPagamento> listaNiveisRemocao = new ArrayList<NivelPagamento>();
		if(!niveisExistentes.isEmpty()){
			for(NivelPagamento existente : niveisExistentes){
				for(NivelPagamento novo : niveisPagamento){
					if(novo.getCidade().getId().equals(existente.getCidade().getId()) &&
							novo.getAno().getId().equals(existente.getAno().getId()) &&
							novo.getSecretaria().getId().equals(existente.getSecretaria().getId()) &&
							novo.getTipoArquivo().getCodigo()==existente.getTipoArquivo().getCodigo() && 
							novo.getDescricao().equals(existente.getDescricao()) && 
							novo.getSalarioBase().equals(existente.getSalarioBase()) &&
							novo.getCodigo().equals(existente.getCodigo())){
						listaNiveisRemocao.add(novo);
					}
				}
			}
		}
		return listaNiveisRemocao;
	}

	private void importarValores() throws Exception {
		criarArquivoImportacao();
		try (BufferedReader br = new BufferedReader(new FileReader(nomeArquivoTemporario))){
			String linha;
			boolean primeiraLinha = true;
			boolean linhaComValores = true;
			while ((linha = br.readLine()) != null && linhaComValores ) {
				linhaComValores = linhaContemValores(linha);
				if (primeiraLinha) {
					primeiraLinha = false;
				}else{
					niveisPagamento.add(getNivelPagamento(linha, quantidadeLinhasComNiveis));
				}quantidadeLinhasComNiveis++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationException("Ocorreu erro ao importar o arquivo. Contate o administrador!");
		}finally {
			deletarArquivos();
		}	
	}

	private boolean linhaContemValores(String linha) {
		return StringUtils.notNullOrEmpty(linha) && linha.length() > 3 ; //A linha deve possuir no minimo 3 virgulas
	}

	private int contarVezesPalavraNaFrase(String frase, String palavra) {
		  return (frase.split(palavra)).length -1;   
		}
	
	private NivelPagamento getNivelPagamento(String linha, int numeroLinha) {
		String delimitador = getDelimitador(linha);
		String[] valores = linha.split(delimitador);
		NivelPagamento nivelPagamento = new NivelPagamento(ano, cidade, secretaria, tipoArquivo);
		nivelPagamento.setDescricao(getDescricao(valores[1].trim(),nivelPagamento, numeroLinha));
		nivelPagamento.setCodigo(getCodigo(valores[0].trim(),nivelPagamento));
		nivelPagamento.setSalarioBase(getSalarioBase(valores[2], numeroLinha));
		return nivelPagamento;
	}

	private String getDelimitador(String linha) {
		if(contarVezesPalavraNaFrase(linha, PONTO_VIRGULA_CSV) >= 2){
			return PONTO_VIRGULA_CSV;
		}else if(contarVezesPalavraNaFrase(linha, DELIMITADOR_CSV) >= 2){
			return DELIMITADOR_CSV;
		}
		throw new ApplicationException("O arquivo esta com delimitador inválido. Veja os delimitadores permitidos no Manual de Importação.");
	}

	private Double getSalarioBase(String salarioBase, int numeroLinha) {
		Double valor = 0D;
		try {
			valor = Double.parseDouble(salarioBase.trim().replace(".","").replace(",", "."));
		} catch (Exception e) {
			throw new ApplicationException("Valor do Salário Base do Nível de Pagamento inválido. Linha: "+ numeroLinha);
		}
		return valor;
	}

	private String getCodigo(String codigo, NivelPagamento nivelPagamento) {
		if (StringUtils.notNullOrEmpty(codigo)) {
			return codigo;
		}
		return Util.getNumeroHashCode(nivelPagamento.getDescricao(), nivelPagamento.getTamanhoMinimoCodigo()).toString();
	}

	private String getDescricao(String descricao, NivelPagamento nivelPagamento, int numeroLinha) {
		if (!StringUtils.notNullOrEmpty(descricao) ) {
			throw new ApplicationException("Descrição do Nível de Pagamento inválido. Linha: "+ numeroLinha);
		}else if (descricao.length() <= nivelPagamento.getTamanhoMinimoCodigo()) {
			throw new ApplicationException("Tamanho da Descrição do Nível de Pagamento inválido. Linha: "+ numeroLinha);
		}
		return descricao;
	}
	
	private void criarArquivoImportacao() throws IOException {
		String filename = FilenameUtils.getName(nomeArquivo);
		InputStream input = file.getInputstream();
		File file = new File(DiretorioProcessamento.getDiretorioTemporario(), filename);
		OutputStream output = new FileOutputStream(file);
		nomeArquivoTemporario = file.getAbsolutePath();
		try {
			IOUtils.copy(input, output);
		} catch (Exception e) {
			e.printStackTrace();
			nomeArquivoTemporario = null;
		} finally {
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(output);
		}
	}
	
	private void deletarArquivos() {
		new File(nomeArquivoTemporario).delete();
	}
	
}
