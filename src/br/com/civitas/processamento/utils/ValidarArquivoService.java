package br.com.civitas.processamento.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
	private String nomeArquivoTemporario;
	
	@Autowired
	private  ArquivoPagamentoService arquivoService ;
	
	public void validarArquivo(UploadedFile file, ArquivoPagamento arquivo) throws IOException{
		this.file = file;
		this.arquivo = arquivo;
		validarTipoArquivo();
		validarArquivoJaProcessado();
		validarArquivoCidadeMes();
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
	
	private void validarArquivoCidadeMes() throws IOException{
		if(arquivo.getTipoArquivo().equals(TipoArquivo.ARQUIVO_LAYOUT)){
			criarArquivoProcessamento();
			PDDocument document = null;
			document = PDDocument.load(new File(nomeArquivoTemporario));
			document.getClass();
			boolean terceiraLinha = false;
			boolean lancarExcessaoCidade = true;
			boolean lancarExcessaoMes = true;
			boolean lancarExcessaoTipoArquivo = true;
			boolean lancarExcessaoAno = true;
			String nomeArquivoTemporario = "" ;
			int numeroLinha = 1;
			if (!document.isEncrypted()) {
				PDFTextStripperByArea stripper = new PDFTextStripperByArea();
				stripper.setSortByPosition(true);
				PDFTextStripper Tstripper = new PDFTextStripper();
				String conteudoArquivo = Tstripper.getText(document);
				nomeArquivoTemporario = this.nomeArquivoTemporario.substring(0, this.nomeArquivoTemporario.length() -3 ) + "txt";
				BufferedWriter buffWrite = new BufferedWriter(new FileWriter(nomeArquivoTemporario));
				buffWrite.append(conteudoArquivo);
				buffWrite.close();
				FileReader frEvento = new FileReader(nomeArquivoTemporario);
				BufferedReader brEvento = new BufferedReader(frEvento);
				while (brEvento.ready() && !terceiraLinha) {
					String linha = brEvento.readLine();
					if(linha.toUpperCase().contains(removerAcentos(arquivo.getCidade().getDescricao().toUpperCase())) && lancarExcessaoCidade){
						lancarExcessaoCidade = false;
					}	
					if(linha.toUpperCase().contains(TipoArquivo.ARQUIVO_LAYOUT.getChaveValidacao().toUpperCase()) && lancarExcessaoTipoArquivo){
						lancarExcessaoTipoArquivo = false;
					}	
					if(linha.toUpperCase().contains(arquivo.getMes().getDescricao().toUpperCase()) && lancarExcessaoMes){
						lancarExcessaoMes = false;
					}
					if(linha.toUpperCase().contains(arquivo.getAno().getAno().toString()) && lancarExcessaoAno){
						lancarExcessaoAno = false;
					}
					if(numeroLinha==3){
						terceiraLinha = true;
					}
					numeroLinha++;
					
				}
				brEvento.close();
				
			}
			document.close();
			File fileTxt = new File(nomeArquivoTemporario);  
			fileTxt.delete();
			File filePdf = new File(this.nomeArquivoTemporario);  
			filePdf.delete();
			if(lancarExcessaoTipoArquivo){
				throw new ApplicationException("Arquivo n�o pertence ao Tipo de Arquivo informado! '" + arquivo.getTipoArquivo().getDescricao().toUpperCase() + "' ! ");
			}
			if(lancarExcessaoCidade){
				throw new ApplicationException("Arquivo n�o � da cidade '"  + arquivo.getCidade().getDescricao().toUpperCase() + "' ! ");
			}
			if(lancarExcessaoMes){
				throw new ApplicationException("Arquivo n�o � do m�s '"  + arquivo.getMes().getDescricao().toUpperCase() + "' ! ");
			}
			if(lancarExcessaoAno){
				throw new ApplicationException("Arquivo n�o � do ano '"  + arquivo.getAno().getAno() + "' ! ");
			}
		}	
	}
	
	private void criarArquivoProcessamento() throws IOException {
		nomeArquivoTemporario = "";
		String filename = FilenameUtils.getName(new String(arquivo.getFile().getFileName().getBytes(Charset.defaultCharset()), "UTF-8"));
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
	
	public static String removerAcentos(String str) {
	    return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
	}
}
