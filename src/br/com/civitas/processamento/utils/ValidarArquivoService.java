package br.com.civitas.processamento.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.Objects;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.civitas.arquitetura.ApplicationException;
import br.com.civitas.processamento.entity.ArquivoPagamento;
import br.com.civitas.processamento.enums.TipoArquivo;
import br.com.civitas.processamento.service.ArquivoPagamentoService;

@Service
public class ValidarArquivoService {

	private static final String TIPO_ARQUIVO_PDF = "PDF";
	private UploadedFile file;
	private ArquivoPagamento arquivo;
	private String nomeArquivoPdfTemporario;
	private String nomeArquivoTxtTemporario;
	
	private boolean finalValidacao;
	private boolean lancarExcessaoCidade;
	private boolean lancarExcessaoMes;
	private boolean lancarExcessaoTipoArquivo;
	private boolean lancarExcessaoAno;

	@Autowired
	private ArquivoPagamentoService arquivoService;

	public void validarArquivo(UploadedFile file, ArquivoPagamento arquivo) throws IOException {
		iniciarValores(file, arquivo);
		validarTipoArquivo();
		validarArquivoJaProcessado();
		validarArquivoCidadeMesAno();
	}

	private void iniciarValores(UploadedFile file, ArquivoPagamento arquivo) {
		this.file = file;
		this.arquivo = arquivo;
		finalValidacao = false;
		lancarExcessaoCidade = true;
		lancarExcessaoMes = true;
		lancarExcessaoTipoArquivo = true;
		lancarExcessaoAno = true;
		nomeArquivoPdfTemporario = null;
		nomeArquivoTxtTemporario = null;
	}

	private void validarTipoArquivo() {
		String extensao = file.getFileName().substring(file.getFileName().length() - 3, file.getFileName().length());
		if (!extensao.toUpperCase().equals(TIPO_ARQUIVO_PDF)) {
			throw new ApplicationException("Tipo Arquivo N�o Suportado. Somente PDF!");
		}
	}

	private void validarArquivoJaProcessado() {
		if (Objects.nonNull(arquivoService.buscarPorArquivo(arquivo))) {
			throw new ApplicationException("Arquivo j� Processado!");
		}
	}

	private void validarArquivoCidadeMesAno() throws IOException {
		criarArquivoProcessamento();
		PDDocument document = null;
		document = PDDocument.load(new File(nomeArquivoPdfTemporario));
		document.getClass();
		int numeroLinha = 1;
		if (!document.isEncrypted()) {
			BufferedReader brEvento = prepararArquivoValidacao(document);
			while (brEvento.ready() && !finalValidacao) {
				String linha = brEvento.readLine();
				validarArquivoCidadeMesAno(linha,arquivo.getTipoArquivo() );
				if (numeroLinha == arquivo.getTipoArquivo().getFimProcessamento()) finalValidacao = true;
				numeroLinha++;
			}
			brEvento.close();
		}
		document.close();
		deletarArquivos();
		tratarExcecoes();
	}

	private void validarArquivoCidadeMesAno(String linha, TipoArquivo tipoArquivo) {
		if (linha.toUpperCase().contains(removerAcentos(arquivo.getCidade().getDescricao().toUpperCase()))
				&& lancarExcessaoCidade) {
			lancarExcessaoCidade = false;
		}
		if (removerAcentos(linha.toUpperCase()).contains(tipoArquivo.getChaveValidacao().toUpperCase())
				&& lancarExcessaoTipoArquivo) {
			lancarExcessaoTipoArquivo = false;
		}
		if (linha.toUpperCase().contains(arquivo.getMes().getDescricao().toUpperCase()) && lancarExcessaoMes) {
			lancarExcessaoMes = false;
		}
		if (linha.toUpperCase().contains(arquivo.getAno().getAno().toString()) && lancarExcessaoAno) {
			lancarExcessaoAno = false;
		}
		if(TipoArquivo.ARQUIVO_TARGET.equals(tipoArquivo) && lancarExcessaoMes  && linha.contains(getChaveMesAno()) ){
			lancarExcessaoMes = false;
		}
	}

	private String getChaveMesAno() {
		return arquivo.getMes().getNumero().toString().length() == 1 ?
				"0" + arquivo.getMes().getNumero() + "/" + arquivo.getAno().getAno() :
				arquivo.getMes().getNumero() + "/" + arquivo.getAno().getAno() 	;
	}

	private BufferedReader prepararArquivoValidacao(PDDocument document) throws IOException, FileNotFoundException {
		PDFTextStripperByArea stripper = new PDFTextStripperByArea();
		stripper.setSortByPosition(true);
		PDFTextStripper Tstripper = new PDFTextStripper();
		String conteudoArquivo = Tstripper.getText(document);
		nomeArquivoTxtTemporario = this.nomeArquivoPdfTemporario.substring(0, this.nomeArquivoPdfTemporario.length() - 3) + "txt";
		BufferedWriter buffWrite = new BufferedWriter(new FileWriter(nomeArquivoTxtTemporario));
		buffWrite.append(conteudoArquivo);
		buffWrite.close();
		FileReader frEvento = new FileReader(nomeArquivoTxtTemporario);
		BufferedReader brEvento = new BufferedReader(frEvento);
		return brEvento;
	}

	private void deletarArquivos() {
		File fileTxt = new File(nomeArquivoTxtTemporario);
		fileTxt.delete();
		File filePdf = new File(nomeArquivoPdfTemporario);
		filePdf.delete();
	}

	private void tratarExcecoes() {
		if (lancarExcessaoTipoArquivo) {
			throw new ApplicationException("Arquivo n�o pertence ao Tipo de Arquivo informado! '"
					+ arquivo.getTipoArquivo().getDescricao().toUpperCase() + "' ! ");
		}
		if (lancarExcessaoCidade) {
			throw new ApplicationException(
					"Arquivo n�o � da cidade '" + arquivo.getCidade().getDescricao().toUpperCase() + "' ! ");
		}
		if (lancarExcessaoMes) {
			throw new ApplicationException(
					"Arquivo n�o � do m�s '" + arquivo.getMes().getDescricao().toUpperCase() + "' ! ");
		}
		if (lancarExcessaoAno) {
			throw new ApplicationException("Arquivo n�o � do ano '" + arquivo.getAno().getAno() + "' ! ");
		}
	}

	private void criarArquivoProcessamento() throws IOException {
		nomeArquivoPdfTemporario = "";
		String filename = FilenameUtils
				.getName(new String(arquivo.getFile().getFileName().getBytes(Charset.defaultCharset()), "UTF-8"));
		InputStream input = arquivo.getFile().getInputstream();
		File file = new File(DiretorioProcessamento.getDiretorioTemporario(), filename);
		OutputStream output = new FileOutputStream(file);
		nomeArquivoPdfTemporario = file.getAbsolutePath();
		try {
			IOUtils.copy(input, output);
		} catch (Exception e) {
			e.printStackTrace();
			nomeArquivoPdfTemporario = null;
		} finally {
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(output);
		}
	}

	public String removerAcentos(String str) {
		return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
	}
}
