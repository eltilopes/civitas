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
@Table(name = "tb_ano")
public class Ano implements IEntity {

	private static final long serialVersionUID = -8346457972094997255L;

	@Id
	@GeneratedValue(generator = "seq_ano", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "seq_ano", sequenceName = "seq_ano", allocationSize = 1)
	@Column(name = "ci_ano")
	private Long id;
	
	@Column(name = "nr_ano",nullable = false, length = 30)
	private int ano;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public int getAno() {
		return ano;
	}
	public void setAno(int ano) {
		this.ano = ano;
	}
	@Override
	public Map<String, Object> notEmptyFields() {
		// TODO Auto-generated method stub
		return null;
	}

}