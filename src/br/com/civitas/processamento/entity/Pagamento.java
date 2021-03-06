package br.com.civitas.processamento.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.com.civitas.arquitetura.entity.IEntity;

@Entity
@Table(name = "tb_pagamento")
public class Pagamento implements IEntity {

	private static final long serialVersionUID = -778110219284043128L;

	@Id
	@GeneratedValue(generator = "seq_pagamento", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "seq_pagamento", sequenceName = "seq_pagamento", allocationSize = 1)
	@Column(name = "ci_pagamento")  
	private Long id;
	  
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="cd_matricula",nullable=false)
	private Matricula matricula;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="cd_arquivo",nullable=false)
	private ArquivoPagamento arquivo;
	
	@OneToMany(mappedBy = "pagamento", fetch = FetchType.LAZY)
	private List<EventoPagamento> eventosPagamento;
	
	@Transient
	private List<EventoPagamento> eventosPagamentoSelecionados;

	@Column(name = "nr_total_proventos", nullable = false)
	private Double totalProventos = 0d;
	
	@Column(name = "nr_total_descontos", nullable = false)  
	private Double totalDescontos = 0d;
	
	@Column(name = "nr_total_liquido", nullable = false)  
	private Double totalLiquido = 0d;
	
	@Column(name = "nr_total_remuneracao", nullable = false)  
	private Double totalRemuneracao = 0d;

	@Column(name = "nr_dias_trabalhados")
	private Double diasTrabalhados;
	  
	@Transient
	private MatriculaPagamento matriculaPagamento;
	
	public Pagamento(){
		eventosPagamento = new ArrayList<EventoPagamento>();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Matricula getMatricula() {
		return matricula;
	}

	public void setMatricula(Matricula matricula) {
		this.matricula = matricula;
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

	public ArquivoPagamento getArquivo() {
		return arquivo;
	}

	public void setArquivo(ArquivoPagamento arquivo) {
		this.arquivo = arquivo;
	}

	public MatriculaPagamento getMatriculaPagamento() {
		return matriculaPagamento;
	}

	public void setMatriculaPagamento(MatriculaPagamento matriculaPagamento) {
		this.matriculaPagamento = matriculaPagamento;
	}

	public Double getDiasTrabalhados() {
		return diasTrabalhados;
	}

	public void setDiasTrabalhados(Double diasTrabalhados) {
		this.diasTrabalhados = diasTrabalhados;
	}

	public List<EventoPagamento> getEventosPagamentoSelecionados() {
		return eventosPagamentoSelecionados;
	}

	public void setEventosPagamentoSelecionados(List<EventoPagamento> eventosPagamentoSelecionados) {
		this.eventosPagamentoSelecionados = eventosPagamentoSelecionados;
	}

	@Override
	public Map<String, Object> notEmptyFields() {
		Map<String, Object> map = new HashMap<String, Object>();
		if(id != null ){
			map.put("id", id);
		}if(arquivo != null && arquivo.getId() != null ){
			map.put("arquivo.id",arquivo.getId());
		}
		return map;
	}

}
