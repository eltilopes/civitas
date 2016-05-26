package br.com.civitas.arquitetura.base.vo;

import java.util.Map;

public class EmailVO {

	private String[] destinos;

	private String remetente;

	private String assunto;

	private String mensagem;

	private boolean html = true;

	private Map<String, String> anexos;

	public EmailVO() {};

	public String[] getDestino() {
		return destinos;
	}

	public void setDestino(String... destinos) {
		this.destinos = destinos;
	}

	public String getRemetente() {
		return remetente;
	}

	public void setRemetente(String remetente) {
		this.remetente = remetente;
	}

	public String getAssunto() {
		return assunto;
	}

	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public Map<String, String> getAnexos() {
		return anexos;
	}

	public void setAnexos(Map<String, String> anexos) {
		this.anexos = anexos;
	}

	public boolean isHtml() {
		return html;
	}

	public void setHtml(boolean html) {
		this.html = html;
	}

}
