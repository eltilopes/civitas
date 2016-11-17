package br.com.civitas.processamento.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResumoSetor {

	private List<EventoPagamento> eventosPagamento;
	private Integer quantidadePagamentos;
	private Secretaria secretaria;
	private Setor setor;
	private Double totalProventos = 0d;
	private Double totalDescontos = 0d;
	private Double totalLiquido = 0d;
	private Double totalRemuneracao = 0d;
	private Map<String, Double> totaisPagamentos;

	public ResumoSetor(){
		eventosPagamento = new ArrayList<EventoPagamento>();
	}

	public Setor getSetor() {
		return setor;
	}

	public void setSetor(Setor setor) {
		this.setor = setor;
	}

	public List<EventoPagamento> getEventosPagamento() {
		return eventosPagamento;
	}

	public void setEventosPagamento(List<EventoPagamento> eventosPagamento) {
		this.eventosPagamento = eventosPagamento;
	}

	public Double getTotalProventos() {
		return totalProventos;
	}

	public void setTotalProventos(Double totalProventos) {
		this.totalProventos = totalProventos;
	}

	public Double getTotalDescontos() {
		return totalDescontos;
	}

	public void setTotalDescontos(Double totalDescontos) {
		this.totalDescontos = totalDescontos;
	}

	public Double getTotalLiquido() {
		return totalLiquido;
	}

	public void setTotalLiquido(Double totalLiquido) {
		this.totalLiquido = totalLiquido;
	}

	public Double getTotalRemuneracao() {
		return totalRemuneracao;
	}

	public void setTotalRemuneracao(Double totalRemuneracao) {
		this.totalRemuneracao = totalRemuneracao;
	}

	public Integer getQuantidadePagamentos() {
		return quantidadePagamentos;
	}

	public void setQuantidadePagamentos(Integer quantidadePagamentos) {
		this.quantidadePagamentos = quantidadePagamentos;
	}

	public Map<String, Double> getTotaisPagamentos() {
		return totaisPagamentos;
	}

	public void setTotaisPagamentos(Map<String, Double> totaisPagamentos) {
		this.totaisPagamentos = totaisPagamentos;
	}

	public Secretaria getSecretaria() {
		return secretaria;
	}

	public void setSecretaria(Secretaria secretaria) {
		this.secretaria = secretaria;
	}
	
}
