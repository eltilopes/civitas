package br.com.civitas.processamento.utils;

public class DiretorioProcessamento {
	
	private static final String DIRETORIO_TEMPORARIO = "C:/CIVITAS/TEMPORARIO";
	private static final String DIRETORIO_PROCESSADO = "C:/CIVITAS/PROCESSADO";
	
	public static String getDiretorioTemporario() {
		return DIRETORIO_TEMPORARIO;
	}

	public static String getDiretorioProcessado() {
		return DIRETORIO_PROCESSADO;
	}
	
}
