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
@Table(name = "tb_cidade")
public class Cidade implements IEntity {

	private static final long serialVersionUID = 8046820495466523634L;

	@Id
	@GeneratedValue(generator = "seq_cidade", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "seq_cidade", sequenceName = "seq_cidade", allocationSize = 1)
	@Column(name = "ci_cidade")
	private Long id;
	
	@Column(name = "ds_descricao",nullable = false, length = 30)
	private String descricao;

	@Column(name="fl_ativa",nullable = false)
	private Boolean ativa;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cd_estado", nullable = false)
	private Estado estado;
	
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
		this.descricao = descricao.toUpperCase();
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Boolean getAtiva() {
		return ativa;
	}
	public void setAtiva(Boolean ativa) {
		this.ativa = ativa;
	}
	@Override
	public Map<String, Object> notEmptyFields() {
		Map<String, Object> map = new HashMap<String, Object>();
		if(id != null ){
			map.put("id", id);
		}if(descricao != null && descricao != ""){
			map.put("descricao", descricao);
		}if(estado != null && estado.getId() != null ){
			map.put("estado.id",estado.getId() );
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
		Cidade other = (Cidade) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}