package br.com.civitas.arquitetura.base;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.com.civitas.arquitetura.entity.IEntity;

@Entity
@Table(name = "tb_permissao")
public class Permissao implements IEntity {

	private static final long serialVersionUID = 5780200585136873750L;

	@Id
	@GeneratedValue(generator = "seq_permissao", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "seq_permissao", sequenceName = "seq_permissao", allocationSize = 1)
	@Column(name = "ci_permissao")
	private Long id;

	@Column(name = "nm_chave", nullable = false, length = 80)
	private String chave;

	@Column(name = "ds_descricao", nullable = false, length = 80)
	private String descricao;

	@Transient
	private Boolean checado;

	public Permissao() {
		super();
	}

	public Permissao(String chave) {
		this();
		this.chave = chave;
	}

	public String getChave() {
		return chave;
	}

	public void setChave(String chave) {
		this.chave = chave;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Boolean getChecado() {
		return checado;
	}

	public void setChecado(Boolean checado) {
		this.checado = checado;
	}

	public Permissao duplicate() {
		Permissao permissao = new Permissao();
		permissao.setChave(this.chave);
		permissao.setChecado(this.checado);
		permissao.setDescricao(this.descricao);
		permissao.setId(getId());
		return permissao;
	}

	@Override
	public Map<String, Object> notEmptyFields() {
		Map<String, Object> map = new HashMap<String, Object>();
		if (id != null) {
			map.put("id", id);
		}
		if (chave != null && !chave.trim().isEmpty()) {
			map.put("chave", chave);
		}
		if (descricao != null && !descricao.trim().isEmpty()) {
			map.put("descricao", descricao);
		}
		return map;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Permissao other = (Permissao) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
