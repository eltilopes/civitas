package br.com.civitas.processamento.entity;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.civitas.arquitetura.entity.IEntity;

@Entity
@Table(name = "tb_mes")
public class Mes implements IEntity {

	private static final long serialVersionUID = -8346457972094997255L;

	@Id
	@GeneratedValue(generator = "seq_mes", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "seq_mes", sequenceName = "seq_mes", allocationSize = 1)
	@Column(name = "ci_mes")
	private Long id;
	
	@Column(name = "nr_mes",nullable = false, length = 2)
	private Integer numero;

	@Column(name = "ds_descricao",nullable = false, length = 30)
	private String descricao;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getNumero() {
		return numero;
	}
	public void setNumero(Integer numero) {
		this.numero = numero;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	@Override
	public Map<String, Object> notEmptyFields() {
		// TODO Auto-generated method stub
		return null;
	}

}