package br.com.civitas.arquitetura.report;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface ReportCommand {

	/**
	 * Recupera o caminho do relatório a ser executado
	 * @return Caminho do relatório
	 */
	public String getReport();
	/**
	 * Recupera e converte os parâmetros da requisição  para tipos adequados
	 * utilizados pelo relatório
	 * @param request Requisição corrente
	 * @return Map com parâmetros utilizados pelo relatório convertidos.
	 */
	public Map<String,Object> getParameters(HttpServletRequest request);

}
