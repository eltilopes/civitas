package br.com.civitas.arquitetura.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.google.common.collect.Lists;

import br.com.civitas.arquitetura.entity.IEntity;

@Entity
@Table(name = "tb_perfil")
public class Perfil implements IEntity {

	private static final long serialVersionUID = -4575097016251699694L;

	@Id
	@GeneratedValue(generator = "seq_perfil", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "seq_perfil", sequenceName="seq_perfil", allocationSize=1)
	@Column(name = "ci_perfil")
	private Long id;
	
	@Column(name = "nm_nome",nullable = false, length = 40)
	private String nome;
	
	@ManyToMany
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinTable(name = "tb_perfil_permissao",
		joinColumns = @JoinColumn(name = "cd_perfil", referencedColumnName = "ci_perfil") , 
		inverseJoinColumns = @JoinColumn(name = "cd_permissao", referencedColumnName = "ci_permissao") )
	private List<Permissao> permissoes = Lists.newArrayList();
	
	@ManyToMany
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinTable(name = "tb_usuario_perfil",
	joinColumns = @JoinColumn(name = "cd_perfil", referencedColumnName = "ci_perfil") , 
	inverseJoinColumns = @JoinColumn(name = "cd_usuario", referencedColumnName = "ci_usuario") )
	private List<Usuario> usuarios = Lists.newArrayList();
	
	@Column(name = "fl_ativo",nullable = false)
	private Boolean ativo;

	public Perfil() {
		super();
		this.ativo = true;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome.toUpperCase();
	}

	public List<Permissao> getPermissoes() {
		return permissoes;
	}

	public void setPermissoes(List<Permissao> permissoes) {
		this.permissoes = permissoes;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}
	
	@Override
	public Map<String, Object> notEmptyFields() {
		Map<String, Object> map = new HashMap<String, Object>();
		if(id != null ){
			map.put("id", id);
		}if(ativo != null ){
			map.put("ativo", ativo);
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
		Perfil other = (Perfil) obj;
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
