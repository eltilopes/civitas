package br.com.civitas.processamento.entity;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.civitas.arquitetura.entity.IEntity;
import br.com.civitas.arquitetura.util.Util;

@Entity
@Table(name = "tb_resumo_setor")
public class ResumoSetor implements IEntity {

	private static final long serialVersionUID = 8741202569448671593L;
	
	@Id
	@GeneratedValue(generator = "seq_resumo_setor", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "seq_resumo_setor", sequenceName = "seq_resumo_setor", allocationSize = 1)
	@Column(name = "ci_resumo_setor")  
	private Long id;
	  
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="cd_arquivo_pagamento",nullable=false)
	private ArquivoPagamento arquivoPagamento;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="cd_secretaria",nullable=false)
	private Secretaria secretaria;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="cd_setor",nullable=false)
	private Setor setor;
	
	@Column(name = "nr_quantidade_pagamentos", nullable = false)
	private Integer quantidadePagamentos;

	@Column(name = "nr_total_proventos", nullable = false)
	private Double totalProventos = 0d;
	
	@Column(name = "nr_total_descontos", nullable = false)  
	private Double totalDescontos = 0d;
	
	@Column(name = "nr_total_liquido", nullable = false)  
	private Double totalLiquido = 0d;
	
	@Column(name = "nr_total_remuneracao", nullable = false)  
	private Double totalRemuneracao = 0d;

	@Column(name = "nr_somatorio_proventos", nullable = false)
	private Double somatorioProventos = 0d;
	
	@Column(name = "nr_somatorio_descontos", nullable = false)  
	private Double somatorioDescontos = 0d;
	
	@Column(name = "nr_somatorio_liquido", nullable = false)  
	private Double somatorioLiquido = 0d;
	
	@Column(name = "nr_somatorio_remuneracao", nullable = false)  
	private Double somatorioRemuneracao = 0d;

	public ArquivoPagamento getArquivoPagamento() {
		return arquivoPagamento;
	}

	public void setArquivoPagamento(ArquivoPagamento arquivoPagamento) {
		this.arquivoPagamento = arquivoPagamento;
	}

	public Setor getSetor() {
		return setor;
	}

	public void setSetor(Setor setor) {
		this.setor = setor;
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

	public Secretaria getSecretaria() {
		return secretaria;
	}

	public void setSecretaria(Secretaria secretaria) {
		this.secretaria = secretaria;
	}

	public Double getSomatorioProventos() {
		return somatorioProventos;
	}

	public void setSomatorioProventos(Double somatorioProventos) {
		this.somatorioProventos = somatorioProventos;
	}

	public Double getSomatorioDescontos() {
		return somatorioDescontos;
	}

	public void setSomatorioDescontos(Double somatorioDescontos) {
		this.somatorioDescontos = somatorioDescontos;
	}

	public Double getSomatorioLiquido() {
		return somatorioLiquido;
	}

	public void setSomatorioLiquido(Double somatorioLiquido) {
		this.somatorioLiquido = somatorioLiquido;
	}

	public Double getSomatorioRemuneracao() {
		return somatorioRemuneracao;
	}

	public void setSomatorioRemuneracao(Double somatorioRemuneracao) {
		this.somatorioRemuneracao = somatorioRemuneracao;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void arredondarValoresResumo(){
		somatorioDescontos = Util.arredondarDoubleTeto(somatorioDescontos, 2);
		somatorioLiquido = Util.arredondarDoubleTeto(somatorioLiquido, 2);
		somatorioRemuneracao = Util.arredondarDoubleTeto(somatorioRemuneracao, 2);
		somatorioProventos = Util.arredondarDoubleTeto(somatorioProventos, 2);
	}
		
	public boolean valoresResumoConferidos(){
		return totalDescontos.equals(somatorioDescontos) && totalProventos.equals(somatorioProventos) && totalRemuneracao.equals(somatorioRemuneracao) && totalLiquido.equals(somatorioLiquido) ;
	}
	
	@Override
	public Map<String, Object> notEmptyFields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getId() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
