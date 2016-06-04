package br.com.civitas.processamento.entity;

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
@Table(name = "tb_secretaria")
public class Secretaria implements IEntity {

	private static final long serialVersionUID = 4814130300251687458L;

	@Id
	@GeneratedValue(generator = "seq_secretaria", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "seq_secretaria", sequenceName = "seq_secretaria", allocationSize = 1)
	@Column(name = "ci_secretaria")
	private Long id;
	
	@Column(name = "ds_descricao",nullable = false, length = 30)
	private String descricao;
	
	@Column(name = "nr_tipo_arquivo",nullable = false)
	@Enumerated(EnumType.ORDINAL)
	private TipoArquivo tipoArquivo;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cd_cidade", nullable = false)
	private Cidade cidade;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	@Override
	public Map<String, Object> notEmptyFields() {
		return null;
	}

}