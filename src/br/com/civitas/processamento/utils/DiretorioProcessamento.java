package br.com.civitas.processamento.utils;

public class DiretorioProcessamento {
	
	private static final String DIRETORIO_TEMPORARIO = "/home/bruno/civitas/temporarios";
	private static final String DIRETORIO_PROCESSADO = "/home/bruno/civitas/processados";
	
	public static String getDiretorioTemporario() {
		return DIRETORIO_TEMPORARIO;
	}

	public static String getDiretorioProcessado() {
		return DIRETORIO_PROCESSADO;
	}
	
}
