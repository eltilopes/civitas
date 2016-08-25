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
@Table(name = "tb_cargo")
public class Cargo implements IEntity {

	private static final long serialVersionUID = -8346457972094997255L;

	@Id
	@GeneratedValue(generator = "seq_cargo", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "seq_cargo", sequenceName = "seq_cargo", allocationSize = 1)
	@Column(name = "ci_cargo")
	private Long id;
	
	@Column(name = "nr_cargo",nullable = false, length = 2)
	private Integer numero;

	@Column(name = "ds_descricao",nullable = false, length = 30)
	private String descricao;
	
	@Column(name = "ds_linha_cargo")
	private String linhaCargo;
	
	@Column(name="fl_ativo",nullable = false)
	private Boolean ativo = true;

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
	public String getLinhaCargo() {
		return linhaCargo;
	}
	public void setLinhaCargo(String linhaCargo) {
		this.linhaCargo = linhaCargo;
	}
	public Boolean getAtivo() {
		return ativo;
	}
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
	
	@Override
	public Map<String, Object> notEmptyFields() {
		Map<String, Object> map = new HashMap<String, Object>();
		if(id != null ){
			map.put("id", id);
		}if(ativo != null ){
			map.put("ativo", ativo);
		}if(descricao != null && descricao != ""){
			map.put("descricao", descricao);
		}if(cidade != null && cidade.getId() != null ){
			map.put("cidade.id",cidade.getId() );
		}if(tipoArquivo != null  ){
			map.put("tipoArquivo",tipoArquivo.getCodigo() );
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
		Cargo other = (Cargo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}