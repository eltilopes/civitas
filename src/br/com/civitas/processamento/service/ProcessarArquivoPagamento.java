package br.com.civitas.processamento.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import br.com.civitas.processamento.entity.ArquivoPagamento;
import br.com.civitas.processamento.utils.DiretorioProcessamento;

public abstract class ProcessarArquivoPagamento {

	private  String nomeArquivoTemporario;
	private FileReader arquivoEvento;
	private FileReader arquivoPagamento;
	private ArquivoPagamento arquivo;
	private PDDocument document = null;
	
	public void iniciarArquivos( ) {
		try {
			criarArquivoProcessamento();
			document = PDDocument.load(new File(nomeArquivoTemporario));
			document.getClass();
			if (!document.isEncrypted()) {
				PDFTextStripperByArea stripper = new PDFTextStripperByArea();
				stripper.setSortByPosition(true);
				PDFTextStripper Tstripper = new PDFTextStripper();
				String conteudoArquivo = Tstripper.getText(document);
				String nomeArquivoTemporario = DiretorioProcessamento.getDiretorioTemporario() + "/" + arquivo.getNomeArquivo().substring(0, arquivo.getNomeArquivo().length() -3 ) + "txt";
				BufferedWriter buffWrite = new BufferedWriter(new FileWriter(nomeArquivoTemporario));
				buffWrite.append(conteudoArquivo);
				buffWrite.close();
				arquivoEvento = new FileReader(nomeArquivoTemporario);
				arquivoPagamento = new FileReader(nomeArquivoTemporario);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void criarArquivoProcessamento() throws IOException {
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

	
	public void finalizarArquivos( ) throws IOException {
		arquivoEvento.close();
		arquivoPagamento.close();
		File f = new File(nomeArquivoTemporario);  
		f.delete();
		document.close();
	}

	public FileReader getArquivoEvento() {
		return arquivoEvento;
	}

	public void setArquivoEvento(FileReader arquivoEvento) {
		this.arquivoEvento = arquivoEvento;
	}

	public FileReader getArquivoPagamento() {
		return arquivoPagamento;
	}

	public void setArquivoPagamento(FileReader arquivoPagamento) {
		this.arquivoPagamento = arquivoPagamento;
	}

	public ArquivoPagamento getArquivo() {
		return arquivo;
	}

	public void setArquivo(ArquivoPagamento arquivo) {
		this.arquivo = arquivo;
	}

}
