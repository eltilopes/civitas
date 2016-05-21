package br.com.civitas.arquitetura.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.civitas.arquitetura.entity.IEntity;

@Entity
@Table(name = "tb_grupo_permissao")
public class GrupoPermissao implements IEntity {

	private static final long serialVersionUID = 2909143488961963990L;

	@Id
	@GeneratedValue(generator = "seq_grupo_permissao", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "seq_grupo_permissao", sequenceName="seq_grupo_permissao", allocationSize=1)
	@Column(name = "ci_grupo_permissao")
	private Long id;
	
	@NotNull
	@Column(name = "nm_nome")
	@Size(max=40)
	private String nome;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "grupoPermissao", fetch = FetchType.LAZY)
	private List<Permissao> permissoes;
	
	public GrupoPermissao() {
		super();
		this.permissoes = new ArrayList<Permissao>();
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public List<Permissao> getPermissoes() {
		return permissoes;
	}

	public void setPermissoes(List<Permissao> permissoes) {
		this.permissoes = permissoes;
	}
	
	@Override
	public Map<String, Object> notEmptyFields() {
		Map<String, Object> map = new HashMap<String, Object>();
		if(id != null ){
			map.put("id", id);
		}if(nome != null && !nome.trim().isEmpty()){
			map.put("nome", nome);
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
		GrupoPermissao other = (GrupoPermissao) obj;
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
