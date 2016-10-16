package br.com.civitas.processamento.entity;

import java.util.HashMap;
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

@Entity
@Table(name = "tb_matricula_pagamento")
public class MatriculaPagamento implements IEntity {
	
	private static final long serialVersionUID = 7015736274198087591L;

	@Id
	@GeneratedValue(generator = "seq_matricula_pagamento", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "seq_matricula_pagamento", sequenceName = "seq_matricula_pagamento", allocationSize = 1)
	@Column(name = "ci_matricula_pagamento")
	private Long id;
	  
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="cd_matricula",nullable=false)
	private Matricula matricula;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="cd_ano",nullable=false)
	private Ano ano;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="cd_mes",nullable=false)
	private Mes mes;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="cd_cargo",nullable=false)
	private Cargo cargo;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="cd_secretaria",nullable=false)
	private Secretaria secretaria;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="cd_unidade_trabalho")
	private UnidadeTrabalho unidadeTrabalho;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="cd_nivel_pagamento")
	private NivelPagamento nivelPagamento;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="cd_carga_horaria_pagamento")
	private CargaHorariaPagamento cargaHorariaPagamento;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="cd_setor")
	private Setor setor ;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="cd_vinculo",nullable=false)
	private Vinculo vinculo;
	
	@Column(name = "nr_carga_horaria",nullable = false)
	private int cargaHoraria;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Cargo getCargo() {
		return cargo;
	}

	public void setCargo(Cargo cargo) {
		this.cargo = cargo;
	}

	public Secretaria getSecretaria() {
		return secretaria;
	}

	public void setSecretaria(Secretaria secretaria) {
		this.secretaria = secretaria;
	}

	public Setor getSetor() {
		return setor;
	}

	public void setSetor(Setor setor) {
		this.setor = setor;
	}

	public Vinculo getVinculo() {
		return vinculo;
	}

	public void setVinculo(Vinculo vinculo) {
		this.vinculo = vinculo;
	}

	public NivelPagamento getNivelPagamento() {
		return nivelPagamento;
	}

	public void setNivelPagamento(NivelPagamento nivelPagamento) {
		this.nivelPagamento = nivelPagamento;
	}

	public CargaHorariaPagamento getCargaHorariaPagamento() {
		return cargaHorariaPagamento;
	}

	public void setCargaHorariaPagamento(CargaHorariaPagamento cargaHorariaPagamento) {
		this.cargaHorariaPagamento = cargaHorariaPagamento;
	}

	public int getCargaHoraria() {
		return cargaHoraria;
	}

	public UnidadeTrabalho getUnidadeTrabalho() {
		return unidadeTrabalho;
	}

	public void setUnidadeTrabalho(UnidadeTrabalho unidadeTrabalho) {
		this.unidadeTrabalho = unidadeTrabalho;
	}

	public void setCargaHoraria(int cargaHoraria) {
		this.cargaHoraria = cargaHoraria;
	}

	public Matricula getMatricula() {
		return matricula;
	}

	public void setMatricula(Matricula matricula) {
		this.matricula = matricula;
	}

	@Override
	public Map<String, Object> notEmptyFields() {
		Map<String, Object> map = new HashMap<String, Object>();
		if(id != null )
			map.put("id", id);
		return map;
	}

	public Ano getAno() {
		return ano;
	}

	public void setAno(Ano ano) {
		this.ano = ano;
	}

	public Mes getMes() {
		return mes;
	}

	public void setMes(Mes mes) {
		this.mes = mes;
	}


}
