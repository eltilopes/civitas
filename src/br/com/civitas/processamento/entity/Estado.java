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
@Table(name = "tb_estado")
public class Estado implements IEntity {

	private static final long serialVersionUID = -8004735147257625629L;

	@Id
	@GeneratedValue(generator = "seq_estado", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "seq_estado", sequenceName = "seq_estado", allocationSize = 1)
	@Column(name = "ci_estado")
	private Long id;
	
	@Column(name = "ds_descricao" , nullable = false, length = 30)
	private String descricao;
	
	@Column(name = "ds_uf", nullable = false, length = 2)
	private String uf;
	
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

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	@Override
	public Map<String, Object> notEmptyFields() {
		// TODO Auto-generated method stub
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
		Estado other = (Estado) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}