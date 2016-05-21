package br.com.civitas.arquitetura.base.entity;

import java.util.Date;
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

import br.com.civitas.arquitetura.base.Usuario;
import br.com.civitas.arquitetura.entity.IEntity;

@Entity
@Table(name = "tb_controle_senha")
public class ControleSenha implements IEntity {

	private static final long serialVersionUID = 8157545825913176667L;

	@Id
	@GeneratedValue(generator = "seq_controle_senha", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "seq_controle_senha", sequenceName="seq_controle_senha", allocationSize=1)
	@Column(name = "ci_controle_senha")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cd_usuario", nullable = false)
	private Usuario usuario;

	@Column(name="ds_senha",nullable = false, length = 100)
	private String senha;

	@Column(name = "dt_data", nullable = false)
	private Date data;

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}
	

	@Override
	public Map<String, Object> notEmptyFields() {
		Map<String, Object> map = new HashMap<String, Object>();
		if(id != null ){
			map.put("id", id);
		}return map;
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
		ControleSenha other = (ControleSenha) obj;
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