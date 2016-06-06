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
@Table(name = "tb_unidade_trabalho")
public class UnidadeTrabalho implements IEntity {

	private static final long serialVersionUID = -7051106340716930774L;

	@Id
	@GeneratedValue(generator = "seq_unidade_trabalho", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "seq_unidade_trabalho", sequenceName = "seq_unidade_trabalho", allocationSize = 1)
	@Column(name = "ci_unidade_trabalho")
	private Long id;
	
	@Column(name = "nr_codigo",nullable = false)
	private Integer codigo;

	@Column(name = "ds_descricao",nullable = false)
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
	public Integer getCodigo() {
		return codigo;
	}
	public void setCodigo(Integer codigo) {
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
	@Override
	public Map<String, Object> notEmptyFields() {
		return null;
	}

}