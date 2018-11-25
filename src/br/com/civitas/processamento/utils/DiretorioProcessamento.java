package br.com.civitas.processamento.utils;

public class DiretorioProcessamento {
	
	private static final String DIRETORIO_TEMPORARIO_WINDOWS = "C:/CIVITAS/TEMPORARIO";
	private static final String DIRETORIO_PROCESSADO_WINDOWS = "C:/CIVITAS/PROCESSADO";
	private static final String DIRETORIO_TEMPORARIO_LINUX = "/opt/CIVITAS/ARQUIVOS/TEMPORARIOS";
	private static final String DIRETORIO_PROCESSADO_LINUX = "/opt/CIVITAS/ARQUIVOS/PROCESSADOS";
	private static final String WINDOWS = "WINDOWS";
	
	public static String getDiretorioTemporario() {
		return isWindows() ? DIRETORIO_TEMPORARIO_WINDOWS : DIRETORIO_TEMPORARIO_LINUX;
	}

	public static String getDiretorioProcessado() {
		return isWindows() ? DIRETORIO_PROCESSADO_WINDOWS : DIRETORIO_PROCESSADO_LINUX;
	}
	
	private static boolean isWindows(){
		return System.getProperty("os.name").toUpperCase().contains(WINDOWS);
	}
}
