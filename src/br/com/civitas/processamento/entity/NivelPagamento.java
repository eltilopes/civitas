package br.com.civitas.processamento.entity;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.civitas.arquitetura.entity.IEntity;
import br.com.civitas.processamento.enums.TipoArquivo;

@Entity
@Table(name = "tb_nivel_pagamento")
public class NivelPagamento implements IEntity {

	private static final long serialVersionUID = 6417498733647775985L;

	@Id
	@GeneratedValue(generator = "seq_nivel_pagamento", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "seq_nivel_pagamento", sequenceName = "seq_nivel_pagamento", allocationSize = 1)
	@Column(name = "ci_nivel_pagamento")
	private Long id;
	
	@Column(name = "ds_codigo",nullable = false)
	private String codigo;

	@Column(name = "ds_descricao",nullable = false)
	private String descricao;
	
	@Column(name = "nr_salario_base")  
	private Double salarioBase;

	@Column(name = "nr_tipo_arquivo",nullable = false)
	@Enumerated(EnumType.ORDINAL)
	private TipoArquivo tipoArquivo;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cd_cidade", nullable = false)
	private Cidade cidade;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cd_ano", nullable = false)
	private Ano ano;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cd_secretaria", nullable = false)
	private Secretaria secretaria;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public TipoArquivo getTipoArquivo() {
		return tipoArquivo;
	}
	public void setTipoArquivo(TipoArquivo tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}
	public Cidade getCidade() {
		return cidade;
	}
	public void setCidade(Cidade cidade) {
		this.cidade = cidade;
	}
	public Double getSalarioBase() {
		return salarioBase;
	}
	public void setSalarioBase(Double salarioBase) {
		this.salarioBase = salarioBase;
	}
	public Ano getAno() {
		return ano;
	}
	public void setAno(Ano ano) {
		this.ano = ano;
	}
	public Secretaria getSecretaria() {
		return secretaria;
	}
	public void setSecretaria(Secretaria secretaria) {
		this.secretaria = secretaria;
	}
	@Override
	public Map<String, Object> notEmptyFields() {
		Map<String, Object> map = new HashMap<String, Object>();
		if(id != null ){
			map.put("id", id);
		}if(descricao != null && descricao != ""){
			map.put("descricao", descricao);
		}if(cidade != null && cidade.getId() != null ){
			map.put("cidade.id",cidade.getId() );
		}if(ano != null && ano.getId() != null ){
			map.put("ano.id",ano.getId() );
		}if(secretaria != null && secretaria.getId() != null ){
			map.put("secretaria.id",secretaria.getId() );
		}
		return map;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NivelPagamento other = (NivelPagamento) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}