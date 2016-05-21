package br.com.civitas.arquitetura.base.vo;

import java.util.Date;


public class LogAcessoVO implements Comparable<LogAcessoVO> {

	private Long quantidade;

	private String nomeEmpresa;
	
	private String usuario;
	
	private Date data;
	
	public void setNomeempresa(String nomeEmpresa){
		setNomeEmpresa(nomeEmpresa);
	}

	public Long getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Number quantidade) {
		this.quantidade = quantidade.longValue();
	}

	public String getNomeEmpresa() {
		return nomeEmpresa;
	}

	public void setNomeEmpresa(String nomeEmpresa) {
		this.nomeEmpresa = nomeEmpresa;
	}
	
	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	
	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public int compareTo(LogAcessoVO o) {
		return o.getQuantidade().compareTo(this.quantidade);
	}
}