package br.com.civitas.processamento.entity;

import java.util.ArrayList;
import java.util.List;

public class ResumoSetor {

	private List<EventoPagamento> eventosPagamento;
	
	private Double totalProventos = 0d;
	private Double totalDescontos = 0d;
	private Double totalLiquido = 0d;
	private Double totalRemuneracao = 0d;

	public ResumoSetor(){
		eventosPagamento = new ArrayList<EventoPagamento>();
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
	
}
