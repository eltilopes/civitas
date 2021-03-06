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
@Table(name = "tb_setor")
public class Setor implements IEntity {

	private static final long serialVersionUID = -3159942624831179553L;

	@Id
	@GeneratedValue(generator = "seq_setor", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "seq_setor", sequenceName = "seq_setor", allocationSize = 1)
	@Column(name = "ci_setor")
	private Long id;
	
	@Column(name = "ds_descricao",nullable = false, length = 30)
	private String descricao;
	
	@Column(name = "nr_codigo", length = 3)
	private Integer codigo;

	@Column(name = "nr_tipo_arquivo",nullable = false)
	@Enumerated(EnumType.ORDINAL)
	private TipoArquivo tipoArquivo;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cd_cidade", nullable = false)
	private Cidade cidade;
	
	/*@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cd_secretaria", nullable = false)
	private Secretaria secretaria;*/

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
	public Integer getCodigo() {
		return codigo;
	}
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	/*public Secretaria getSecretaria() {
		return secretaria;
	}
	public void setSecretaria(Secretaria secretaria) {
		this.secretaria = secretaria;
	}*/
	
	@Override
	public Map<String, Object> notEmptyFields() {
		return null;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Setor other = (Setor) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		if (descricao == null) {
			if (other.descricao != null)
				return false;
		} else if (!descricao.equals(other.descricao))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}