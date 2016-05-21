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

@Entity
@Table(name = "tb_vinculo")
public class Vinculo implements IEntity {

	private static final long serialVersionUID = 7193590733826194577L;

	@Id
	@GeneratedValue(generator = "seq_vinculo", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "seq_vinculo", sequenceName = "seq_vinculo", allocationSize = 1)
	@Column(name = "ci_vinculo")
	private Long id;
	
	@Column(name = "nr_vinculo",nullable = false, length = 2)
	private Integer numero;

	@Column(name = "ds_descricao",nullable = false, length = 30)
	private String descricao;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cd_cidade", nullable = false)
	private Cidade cidade;

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
	public Cidade getCidade() {
		return cidade;
	}
	public void setCidade(Cidade cidade) {
		this.cidade = cidade;
	}
	@Override
	public Map<String, Object> notEmptyFields() {
		// TODO Auto-generated method stub
		return null;
	}

}